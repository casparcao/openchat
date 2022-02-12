package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.model.Chat;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Date;

/**
 * 消息接受器，接受服务器推送的消息
 * @author mike
 */
@Slf4j
public class MsgAcceptor extends SimpleChannelInboundHandler<Proto.Message> {

    private ChatStore store;

    public void store(ChatStore store){
        this.store = store;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        if(!Proto.MsgType.PUSH.equals(msg.getType())){
            ctx.fireChannelRead(msg);
            return;
        }
        Proto.Chat chat = msg.getPush().getChat();
        Date ts = new Date();
        ts.setTime(chat.getTs());
        Chat c = new Chat()
                .setId(0)
                .setMessage(chat.getMessage())
                .setSpeaker(chat.getSpeaker())
                .setRoom(chat.getRoom())
                .setTs(ts)
                .setType(chat.getType());
        store.store(true, c);
    }

}
