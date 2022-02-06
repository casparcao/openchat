package top.mikecao.openchat.client.service.listener;

import top.mikecao.openchat.client.connection.Connector;
import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.MsgBuilder;

import java.util.List;

/**
 * 用户输入内容后，点击发送按钮，将消息保存到本地存储后，触发的事件，用来将消息发送到服务器进行广播
 * @author caohailong
 */

public class ChatMsgSender implements MsgStore.Listener {

    private final Connector connector;
    private final String token;

    public ChatMsgSender(Connector connector, String token){
        this.connector = connector;
        this.token = token;
    }

    @Override
    public void process(long fid, List<Proto.Chat> chats) {
        chats.forEach(chat -> {
            //如果是好友的消息则不需要再发送回服务器了
            if(chat.getSpeaker() == fid){
                return;
            }
            Proto.Message msg = MsgBuilder.get(Proto.MsgType.SEND)
                    .setChat(chat)
                    .setToken(token)
                    .build();
            connector.channel().writeAndFlush(msg);
        });
    }
}
