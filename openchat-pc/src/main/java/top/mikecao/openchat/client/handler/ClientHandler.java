package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author mike
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Proto.Message> {

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) throws Exception {
        log.info("ClientHandler.channelRead>>" + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        executor.execute(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名密码(分号分割):");
            String line = scanner.nextLine();
            String[] lines = line.split(";");
            String account = lines[0];
            String passwd = lines[1];
            Proto.LoginRequest loginRequest = Proto.LoginRequest.newBuilder()
                    .setAccount(account)
                    .setPasswd(passwd)
                    .build();
            Proto.Message message = Proto.Message.newBuilder()
                    .setId(ThreadLocalRandom.current().nextInt(1000))
                    .setTs(System.currentTimeMillis())
                    .setType(Proto.MsgType.LOGIN)
                    .setLogin(loginRequest)
                    .build();
            ctx.writeAndFlush(message);

            System.out.println("请输入要发送给对方的消息(分号分隔):");
            line = scanner.nextLine();
            lines = line.split(";");
            long from = Long.parseLong(lines[0]);
            long t0 = Long.parseLong(lines[1]);
            String content = lines[2];
            Proto.Chat chatRequest = Proto.Chat.newBuilder()
                    .setFrom(from)
                    .setTo(t0)
                    .setMessage(content)
                    .setType(Proto.ChatType.TEXT)
                    .build();
            message = Proto.Message.newBuilder()
                    .setId(ThreadLocalRandom.current().nextInt(1000))
                    .setTs(System.currentTimeMillis())
                    .setType(Proto.MsgType.P2P)
                    .setChat(chatRequest)
                    .build();
            ctx.writeAndFlush(message);
        });
    }

}
