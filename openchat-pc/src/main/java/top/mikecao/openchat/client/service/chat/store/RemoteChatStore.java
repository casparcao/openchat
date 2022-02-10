package top.mikecao.openchat.client.service.chat.store;

import com.fasterxml.jackson.core.type.TypeReference;
import top.mikecao.openchat.client.connection.Connector;
import top.mikecao.openchat.client.model.Chat;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.core.http.Client;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.MsgBuilder;
import top.mikecao.openchat.core.serialize.Result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static top.mikecao.openchat.client.config.Constants.CHAT_ENDPOINT;

/**
 * 消息存储，加载逻辑
 * @author caohailong
 */

public class RemoteChatStore implements ChatStore {

    private final Connector connector;
    private final String token;

    public RemoteChatStore(Connector connector, String token){
        this.connector = connector;
        this.token = token;
    }
    private final List<Listener> listeners = new LinkedList<>();

    @Override
    public void store(boolean there, Chat chat) {
        this.store(there, Collections.singletonList(chat));
    }

    @Override
    public void store(boolean there, List<Chat> chats) {
        chats.forEach(chat -> {
            //如果是好友的消息则不需要再发送回服务器了
            if(there){
                return;
            }
            Proto.Chat c = Proto.Chat.newBuilder()
                    .setType(chat.getType())
                    .setSpeaker(chat.getSpeaker())
                    .setRoom(chat.getRoom())
                    .setMessage(chat.getMessage())
                    .build();
            Proto.Message msg = MsgBuilder.get(Proto.MsgType.SEND)
                    .setChat(c)
                    .setToken(token)
                    .build();
            connector.channel().writeAndFlush(msg);
        });
        Objects.requireNonNull(listeners, "回调函数不可为空")
                .forEach(cb -> cb.process(there, chats.get(0).getRoom(), chats));
    }

    @Override
    public void listener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public List<Chat> load(long rid, long page, long size) {
        Result<List<Chat>> result
                = Client.get(CHAT_ENDPOINT + "?rid=" + rid + "&page=" + page + "&size=" + size,
                new TypeReference<>() {}, token);
        if(result.failure()){
            return Collections.emptyList();
        }
        return result.getData();
    }
}
