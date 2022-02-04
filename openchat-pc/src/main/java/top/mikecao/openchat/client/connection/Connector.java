package top.mikecao.openchat.client.connection;

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
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.handler.AuthHandler;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.registry.Server;
import top.mikecao.openchat.core.ssl.KeyCertStore;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 负责聊天服务器的连接
 * @author mike
 */
@Slf4j
public class Connector {

    private final EventLoopGroup worker = new NioEventLoopGroup();
    private Channel channel;

    public void connect(String token, List<Server> servers) {
        for (Server server: servers) {
            //依次尝试每个server
            Channel ch = connect0(server.getIp(), server.getPort(), token);
            if(Objects.nonNull(ch)){
                this.channel = ch;
                break;
            }
        }
        if(Objects.isNull(this.channel)){
            throw new AppServerException("连接服务器失败");
        }
    }

    private Channel connect0(String host, int port, String token) {
        SslContext sslCtx ;
        try {
            sslCtx = buildSslContext();
        } catch (CertificateException
                | NoSuchAlgorithmException
                | IOException
                | KeyStoreException
                | UnrecoverableEntryException e) {
            log.error("证书异常>>", e);
            throw new AppServerException("证书异常");
        }
        Channel ch;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(sslCtx.newHandler(ch.alloc(), host, port))
                                .addLast("FrameDecoder", new ProtobufVarint32FrameDecoder())
                                .addLast("ProtobufDecoder", new ProtobufDecoder(Proto.Message.getDefaultInstance()))
                                .addLast("FrameEncoder", new ProtobufVarint32LengthFieldPrepender())
                                .addLast("ProtobufEncoder", new ProtobufEncoder())
                                .addLast(new AuthHandler(token));
                    }
                });
        try{
            ChannelFuture future = bootstrap.connect(host, port).sync();
            boolean success = future.await(5, TimeUnit.SECONDS);
            if(success) {
                ch = future.channel();
            }else{
                ch = null;
            }
        }catch (InterruptedException e){
            log.error("连接服务器失败>>", e);
            close();
            Thread.currentThread().interrupt();
            throw new AppServerException("连接服务器失败");
        }
        return ch;
    }

    public Channel channel(){
        return this.channel;
    }

    public void channel(Channel channel){
        this.channel = channel;
    }

    public void close(){
        channel.close().syncUninterruptibly();
        worker.shutdownGracefully();
    }

    private static final String STORE_PASS = "top.mikecao.openchat.pc";
    private static final String KEY_PASS = "top.mikecao.openchat.pc";
    private static final String CLIENT_ALIAS = "top.mikecao.openchat.pc";
    private static final String SERVER_ALIAS = "top.mikecao.openchat.server";
    private static final String PATH = "top.mikecao.openchat.pc.keystore";

    private SslContext buildSslContext() throws CertificateException,
            NoSuchAlgorithmException, IOException,
            KeyStoreException, UnrecoverableEntryException {
        KeyCertStore store = new KeyCertStore(PATH, STORE_PASS);
        PrivateKey pk = store.key(CLIENT_ALIAS, KEY_PASS);
        X509Certificate x509Certificate = store.certificate(CLIENT_ALIAS);
        X509Certificate trustX509Certificate = store.certificate(SERVER_ALIAS);
        return SslContextBuilder.forClient()
                .keyManager(pk, x509Certificate)
                .trustManager(trustX509Certificate).build();
    }

}


