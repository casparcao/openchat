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
import top.mikecao.openchat.client.handler.ClientHandler;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.ssl.KeyCertStore;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author mike
 */
public class OpenChatClient {

    public static void main(String[] args) throws InterruptedException, KeyStoreException,
            UnrecoverableEntryException, NoSuchAlgorithmException, IOException, CertificateException {
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


