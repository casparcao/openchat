package top.mikecao.openchat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.ssl.KeyCertStore;

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
public class OpenChatServer {

    private final EventLoopGroup boss = new NioEventLoopGroup(1);
    private final EventLoopGroup work = new NioEventLoopGroup();
    private final EventExecutorGroup loginExecutorGroup = new DefaultEventExecutorGroup(16);
    private final EventExecutorGroup p2pChatExecutorGroup = new DefaultEventExecutorGroup(16);
    private final EventExecutorGroup p2gChatExecutorGroup = new DefaultEventExecutorGroup(16);
    private final EventExecutorGroup unreadMsgFetchExecutorGroup = new DefaultEventExecutorGroup(16);

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
    @Qualifier("authFilter")
    private ChannelInboundHandler authFilter;
    @Autowired
    @Qualifier("loginHandler")
    private ChannelInboundHandler loginHandler;
    @Autowired
    @Qualifier("p2pChatHandler")
    private ChannelInboundHandler p2pChatHandler;
    @Autowired
    @Qualifier("p2gChatHandler")
    private ChannelInboundHandler p2gChatHandler;
    @Autowired
    @Qualifier("unreadMsgFetchHandler")
    private ChannelInboundHandler unreadMsgFetchHandler;
    @Autowired
    @Qualifier("exceptionHandler")
    private ChannelDuplexHandler exceptionHandler;

    private ChannelInitializer<Channel> channelInitializer(SslContext sslCtx) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline()
                        .addLast("SslHandler", sslCtx.newHandler(ch.alloc()))
                        .addLast("FrameDecoder", new ProtobufVarint32FrameDecoder())
                        .addLast("ProtobufDecoder", new ProtobufDecoder(Proto.Message.getDefaultInstance()))
                        .addLast("FrameEncoder", new ProtobufVarint32LengthFieldPrepender())
                        .addLast("ProtobufEncoder", new ProtobufEncoder())
                        .addLast("AuthFilter", authFilter)
                        .addLast(unreadMsgFetchExecutorGroup, "unreadMsgFetchHandler", unreadMsgFetchHandler)
                        .addLast(loginExecutorGroup, "LoginHandler", loginHandler)
                        .addLast(p2pChatExecutorGroup, "p2pChatHandler", p2pChatHandler)
                        .addLast(p2gChatExecutorGroup, "p2gChatHandler", p2gChatHandler)
                        .addLast("exceptionHandler", exceptionHandler);
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
        p2pChatExecutorGroup.shutdownGracefully();
    }
}
