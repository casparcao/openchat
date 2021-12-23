package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.entity.Chat;
import top.mikecao.openchat.server.producer.MsgProducer;
import top.mikecao.openchat.server.repository.SimpleChatRepository;
import top.mikecao.openchat.toolset.common.Generator;

import java.util.Date;

/**
 * 点对点消息发送处理
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class P2pChatHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private SimpleChatRepository simpleChatRepository;
    @Autowired
    private MsgProducer producer;
    @Autowired
    private Generator<Long> generator;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        Proto.MsgType type = msg.getType();
        //不是点对点消息，不做处理
        if(!Proto.MsgType.P2P.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        long id = generator.next();
        Proto.Chat pc = msg.getChat();
        Chat chat = new Chat()
                .setId(id)
                .setMessage(pc.getMessage())
                .setBroadcast(pc.getBroadcast())
                .setType(pc.getType())
                .setFrom(pc.getFrom())
                .setTo(pc.getTo())
                .setTs(new Date());
        // 1. 将消息保存到消息库
        simpleChatRepository.save(chat)
                .subscribe();
        // 2. 将消息投递到消息队列
        producer.produce(id + "", pc);
        // 如果对方在线，消息队列中的消息被消费时，直接投递给对方
        // 否则，消息队列消息不做处理直接抛弃，等对方上线时，重新从库中拉去未读的消息
        //ctx.writeAndFlush();
        //do not propagate
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("点对点消息处理异常>>", cause);
        ctx.flush();
    }
}
