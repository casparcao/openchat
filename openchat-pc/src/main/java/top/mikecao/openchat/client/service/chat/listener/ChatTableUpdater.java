package top.mikecao.openchat.client.service.chat.listener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.core.proto.Proto;

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
    private final Map<Long, ObservableList<Proto.Chat>> map;

    public ChatTableUpdater(Map<Long, ObservableList<Proto.Chat>> map){
        this.map = map;
    }

    @Override
    public void process(boolean there, long rid, List<Proto.Chat> chats) {
        ObservableList<Proto.Chat> observableList = map.compute(rid, (key, exists) -> {
            if(Objects.isNull(exists)){
                return FXCollections.observableList(new ArrayList<>());
            }
            return exists;
        });
        observableList.addAll(chats);
    }
}
