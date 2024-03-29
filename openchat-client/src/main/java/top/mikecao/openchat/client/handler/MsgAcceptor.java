package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.model.Chat;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Date;

/**
 * 消息接受器，接受服务器推送的消息
 * @author mike
 */
@Slf4j
@ChannelHandler.Sharable
public class MsgAcceptor extends SimpleChannelInboundHandler<Proto.Message> {

    private ChatStore store;
    /** 当前登录账号 */
    private Account account;

    public void store(ChatStore store){
        this.store = store;
    }
    public void account(Account account){
        this.account = account;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        if(!Proto.MsgType.PUSH.equals(msg.getType())){
            ctx.fireChannelRead(msg);
            return;
        }
        Proto.Chat chat = msg.getPush().getChat();
        if(chat.getSpeaker() == account.getId()){
            //本人的消息不再处理
            return;
        }
        Date ts = new Date();
        ts.setTime(chat.getTs());
        Chat c = new Chat()
                .setId(0)
                .setMessage(chat.getMessage())
                .setSpeaker(chat.getSpeaker())
                .setNickname(chat.getNickname())
                .setRoom(chat.getRoom())
                .setTs(ts)
                .setType(chat.getType());
        store.store(true, c);
    }

}
