package top.mikecao.openchat.client.service.chat.listener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import top.mikecao.openchat.client.model.Chat;
import top.mikecao.openchat.client.service.chat.ChatStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 消息存储完成后更新页面展示
 * @author caohailong
 */

public class ChatTableUpdater implements ChatStore.Listener {

    /**
     * 表格数据绑定的引用
     */
    private final Map<Long, ObservableList<Chat>> map;

    public ChatTableUpdater(Map<Long, ObservableList<Chat>> map){
        this.map = map;
    }

    @Override
    public void process(boolean there, long rid, List<Chat> chats) {
        ObservableList<Chat> observableList = map.compute(rid, (key, exists) -> {
            if(Objects.isNull(exists)){
                return FXCollections.observableList(new ArrayList<>());
            }
            return exists;
        });
        observableList.addAll(chats);
    }
}
