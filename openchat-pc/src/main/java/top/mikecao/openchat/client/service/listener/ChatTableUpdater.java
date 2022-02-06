package top.mikecao.openchat.client.service.listener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 消息存储完成后更新页面展示
 * @author caohailong
 */

public class ChatTableUpdater implements MsgStore.Listener {

    /**
     * 表格数据绑定的引用
     */
    private final Map<Long, ObservableList<Proto.Chat>> map;

    public ChatTableUpdater(Map<Long, ObservableList<Proto.Chat>> map){
        this.map = map;
    }

    @Override
    public void process(long fid, List<Proto.Chat> chats) {
        ObservableList<Proto.Chat> observableList = map.compute(fid, (key, exists) -> {
            if(Objects.isNull(exists)){
                return FXCollections.observableList(new ArrayList<>());
            }
            return exists;
        });
        observableList.addAll(chats);
    }
}
