package top.mikecao.openchat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.mikecao.pchat.toolset.ssl.KeyCertStore;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author mike
 */

@Component
public class Server {

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup(20);
    EventExecutorGroup loginExecutorGroup = new DefaultEventExecutorGroup(16);

    @PostConstruct
    public void start() throws CertificateException,
            NoSuchAlgorithmException,
            KeyStoreException,
            IOException,
            UnrecoverableEntryException, InterruptedException {

        SslContext sslCtx = buildSslContext();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer(sslCtx))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.bind(8888).sync();
    }

    @Autowired
    @Qualifier("byteToObjectDecoder")
    private ByteToMessageDecoder byteToMessageDecoder;
    @Autowired
    @Qualifier("authFilter")
    private ChannelInboundHandler authFilter;
    @Autowired
    @Qualifier("loginHandler")
    private ChannelInboundHandler loginHandler;
    @Autowired
    @Qualifier("textMessageHandler")
    private ChannelInboundHandler textMessageHandler;
    @Autowired
    @Qualifier("byteMessageHandler")
    private ChannelInboundHandler byteMessageHandler;

    private ChannelInitializer<Channel> channelInitializer(SslContext sslCtx) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline()
                        .addLast("SslHandler", sslCtx.newHandler(ch.alloc()))
                        .addLast("FrameDecoder",
                                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                        0,
                                        4,
                                        0,
                                        4))
                        .addLast("ObjectDecoder", byteToMessageDecoder)
                        .addLast("AuthFilter", authFilter)
                        .addLast(loginExecutorGroup, "LoginHandler", loginHandler)
                        .addLast("TextHandler", textMessageHandler)
                        .addLast("ByteHandler", byteMessageHandler);
            }
        };
    }

    @Value("${ssl.cert.store.pass}")
    private String storePass;
    @Value("${ssl.cert.key.pass}")
    private String keyPass;
    @Value("${ssl.cert.key.server.alias}")
    private String serverAlias;
    @Value("${ssl.cert.key.client.alias}")
    private String clientAlias;
    @Value("${ssl.cert.path}")
    private String path;


    private SslContext buildSslContext() throws CertificateException,
            NoSuchAlgorithmException, IOException,
            KeyStoreException, UnrecoverableEntryException {
        KeyCertStore store = new KeyCertStore(path, storePass);
        PrivateKey pk = store.key(serverAlias, keyPass);
        X509Certificate x509Certificate = store.certificate(serverAlias);
        X509Certificate trustX509Certificate = store.certificate(clientAlias);
        return SslContextBuilder.forServer(pk, x509Certificate).trustManager(trustX509Certificate).build();
    }

    @PreDestroy
    public void destroy(){
        boss.shutdownGracefully();
        work.shutdownGracefully();
        loginExecutorGroup.shutdownGracefully();
    }
}
