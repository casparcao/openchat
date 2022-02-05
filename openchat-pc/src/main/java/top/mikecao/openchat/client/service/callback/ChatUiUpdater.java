package top.mikecao.openchat.client.service.callback;

import javafx.collections.ObservableList;
import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.List;

/**
 * 消息存储完成后更新页面展示
 * @author caohailong
 */

public class ChatUiUpdater implements MsgStore.Callback {

    private final ObservableList<Proto.Chat> table;

    public ChatUiUpdater(ObservableList<Proto.Chat> table){
        this.table = table;
    }

    @Override
    public void process(List<Proto.Chat> chats) {
        table.addAll(chats);
    }
}
