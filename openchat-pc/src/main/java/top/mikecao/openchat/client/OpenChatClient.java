package top.mikecao.openchat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import top.mikecao.openchat.client.handler.ClientHandler;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.toolset.ssl.KeyCertStore;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author mike
 */
public class OpenChatClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group parent = new Group();
        Paint paint = Color.BLACK;
        Scene scene = new Scene(parent, 300, 300, paint);
        primaryStage.setScene(scene);
        primaryStage.setTitle("asdfasf");
        primaryStage.show();
    }

    public static void main(String[] args) throws InterruptedException, KeyStoreException,
            UnrecoverableEntryException, NoSuchAlgorithmException, IOException, CertificateException {
        launch(args);
        KeyCertStore store = new KeyCertStore("top.mikecao.openchat.pc.keystore","top.mikecao.openchat.pc");
        PrivateKey pk = store.key("top.mikecao.openchat.pc","top.mikecao.openchat.pc");
        X509Certificate x509Certificate = store.certificate("top.mikecao.openchat.pc");
        X509Certificate trustX509Certificate = store.certificate("top.mikecao.openchat.server");

        SslContext sslCtx = SslContextBuilder.forClient()
                .keyManager(pk, x509Certificate)
                .trustManager(trustX509Certificate).build();
        String host = "localhost";
        int port = 8888;
        EventLoopGroup work = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(sslCtx.newHandler(ch.alloc(),host,port))
                                    .addLast("FrameDecoder", new ProtobufVarint32FrameDecoder())
                                    .addLast("ProtobufDecoder", new ProtobufDecoder(Proto.Message.getDefaultInstance()))
                                    .addLast("FrameEncoder", new ProtobufVarint32LengthFieldPrepender())
                                    .addLast("ProtobufEncoder", new ProtobufEncoder())
                                    .addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();

        }finally {
//            work.shutdownGracefully();
        }
    }
}


