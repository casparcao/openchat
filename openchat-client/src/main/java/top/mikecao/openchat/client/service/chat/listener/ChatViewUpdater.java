package top.mikecao.openchat.client.service.chat.listener;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import top.mikecao.openchat.client.controller.MsgViewRender;
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

public class ChatViewUpdater implements ChatStore.Listener {

    /**
     * 表格数据绑定的引用
     */
    private final Map<Long, ObservableList<HBox>> map;
    private final double width;

    public ChatViewUpdater(Map<Long, ObservableList<HBox>> map, double width){
        this.map = map;
        this.width = width;
    }

    @Override
    public void process(boolean there, long rid, List<Chat> chats) {
        ObservableList<HBox> observableList = map.compute(rid, (key, exists) -> {
            if(Objects.isNull(exists)){
                return FXCollections.observableList(new ArrayList<>());
            }
            return exists;
        });
        Platform.runLater(() ->
            observableList.addAll(MsgViewRender.render(!there, chats, width))
        );
    }
}
