package top.mikecao.openchat.client.connection;

import com.fasterxml.jackson.core.type.TypeReference;
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
import io.netty.handler.timeout.IdleStateHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.controller.MainApplication;
import top.mikecao.openchat.client.handler.HeartBeatHandler;
import top.mikecao.openchat.client.handler.InitHandler;
import top.mikecao.openchat.client.handler.MsgAcceptor;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.common.Constants;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.http.Client;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.registry.Server;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.core.ssl.KeyCertStore;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static top.mikecao.openchat.client.config.Constants.SERVER_ENDPOINT;

/**
 * 负责聊天服务器的连接
 * @author mike
 */
@Slf4j
public class Connector {

    private final String token;
    private final EventLoopGroup worker;
    private Channel channel;
    private final MsgAcceptor acceptor;
    private final InitHandler initializer;
    private final HeartBeatHandler heartBeatHandler;
    private final SslContext sslContext;
    private final Bootstrap bootstrap;
    private final MainApplication application;

    public Connector(String token, MainApplication application){
        this.token = token;
        this.application = application;
        this.worker = new NioEventLoopGroup();
        this.acceptor = new MsgAcceptor();
        this.initializer = new InitHandler(token);
        this.heartBeatHandler = new HeartBeatHandler(this, this.token);
        try {
            this.sslContext = buildSslContext();
        } catch (CertificateException
                | NoSuchAlgorithmException
                | IOException
                | KeyStoreException
                | UnrecoverableEntryException e) {
            log.error("证书异常>>", e);
            throw new AppServerException("证书异常");
        }
        this.bootstrap = bootstrap();
    }

    public void connect() {
        //应用已关闭，不进行连接
        if(this.application.closed()){
            return;
        }
        //如果链接失败，则开始重试，最大尝试n次， 每次间隔m秒
        //失败后刷新ui展示x秒后重试
        //最大尝试次数
        short threshold = 8;
        //等待时间10秒
        long wait = 10L * 1000 * 1000 * 1000;
        for(int loop = 0; loop < threshold; loop++) {
            Result<List<Server>> rls = Client.get(SERVER_ENDPOINT, new TypeReference<>() {}, this.token);
            if (rls.failure()) {
                Platform.runLater(this::alert);
            }else{
                List<Server> servers = rls.or(Collections.emptyList());
                for (Server server : servers) {
                    //依次尝试每个server
                    this.channel = connect0(server.getIp(), server.getPort());
                    if (Objects.nonNull(this.channel)) {
                        break;
                    }
                }
            }
            //已完成连接，跳出循环，不再重试
            if(Objects.nonNull(this.channel)){
                break;
            }
            //本次连接失败，等待下次尝试
            LockSupport.parkNanos(wait);
        }

        if (Objects.isNull(this.channel)) {
            //多次重试后依旧没有成功，终止程序
            this.application.close();
        }
    }

    private Channel connect0(String host, int port){
        Channel ch = null;
        try{
            ChannelFuture future = bootstrap.connect(host, port);
            boolean success = future.await(3, TimeUnit.SECONDS);
            if(success) {
                ch = future.channel();
            }
        }catch (InterruptedException e){
            log.error("连接服务器失败>>", e);
            Thread.currentThread().interrupt();
            return null;
        }
        return Objects.nonNull(ch) && ch.isActive()
                ? ch
                : null;
    }

    private Bootstrap bootstrap() {
        Bootstrap bp = new Bootstrap();
        bp.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast("idleStateHandler", new IdleStateHandler(0, Constants.WRITER_IDLE_TIME, 0))
                                .addLast(sslContext.newHandler(ch.alloc()))
                                .addLast("FrameDecoder", new ProtobufVarint32FrameDecoder())
                                .addLast("ProtobufDecoder", new ProtobufDecoder(Proto.Message.getDefaultInstance()))
                                .addLast("FrameEncoder", new ProtobufVarint32LengthFieldPrepender())
                                .addLast("ProtobufEncoder", new ProtobufEncoder())
                                .addLast("heartBeatHandler", heartBeatHandler)
                                .addLast(initializer)
                                .addLast(acceptor);
                    }
                });
        return bp;
    }

    public Channel channel(){
        return this.channel;
    }

    public void store(ChatStore store){
        this.acceptor.store(store);
    }
    public void account(Account account){
        this.acceptor.account(account);
    }

    public void close(){
        if (Objects.nonNull(this.channel)) {
            this.channel.close().syncUninterruptibly();
        }
        this.worker.shutdownGracefully();
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

    private void alert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.titleProperty().set("警告");
        alert.contentTextProperty().set("服务器连接失败，");
        alert.showAndWait();
    }

}


