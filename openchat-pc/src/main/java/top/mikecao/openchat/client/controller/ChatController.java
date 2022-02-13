package top.mikecao.openchat.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.util.concurrent.DefaultThreadFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.MainApplication;
import top.mikecao.openchat.client.model.Chat;
import top.mikecao.openchat.client.model.Relation;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.client.service.chat.listener.ChatViewUpdater;
import top.mikecao.openchat.client.service.chat.store.RemoteChatStore;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.auth.granters.DefaultTokenGranter;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.http.Client;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.Json;
import top.mikecao.openchat.core.serialize.Result;

import java.util.*;
import java.util.concurrent.*;

import static top.mikecao.openchat.client.config.Constants.*;

/**
 * @author mike
 */
@Slf4j
public class ChatController extends Parent {

    private MainApplication application;
    @FXML
    private ListView<RelationViewRender.CustomBox> listViewRelation;
    /** 对方姓名*/
    @FXML
    private Label labelThere;
    /** 本人姓名 */
    @FXML
    private Label labelAccount;
    @FXML
    private Label labelClose;
    /** 聊天记录 */
    @FXML
    private ListView<HBox> listViewMessage;
    /** 正在输入的内容 */
    @FXML
    private TextArea txtInput;
    /** 发送按钮 */
    @FXML
    private Button btnSend;
    /** 消息记录列表数据源 ，每个key对应一个房间*/
    private final Map<Long, ObservableList<HBox>> messages = new HashMap<>(32);
    private ChatStore chatStore;
    private final TokenGranter tokenGranter = new DefaultTokenGranter(Json.mapper());
    private Account account;
    private Auth auth;

    private final ExecutorService executorService = new ThreadPoolExecutor(4, 4,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(64),
            new DefaultThreadFactory(ChatController.class)
    );

    public void application(MainApplication application){
        this.application = application;

        executorService.execute(() -> {
            init();
            //加载好友列表
            relations();
        });
    }

    public void account(String account){
        executorService.execute(() -> Platform.runLater(() -> labelAccount.setText(account)));
    }

    private void init() {
        auth = Storage.load(TOKEN_STORE_LOCATION, Auth.class);
        if(Objects.isNull(auth)){
            log.error("无Token信息");
            throw new AppServerException("无认证信息");
        }
        chatStore = new RemoteChatStore(this.application.connector(), this.auth.getToken());
        account = tokenGranter.resolve(auth.getToken());
        ChatViewUpdater chatViewUpdater = new ChatViewUpdater(messages, listViewMessage.getWidth());
        //配置消息存储器
        chatStore.listener(chatViewUpdater);
        this.application.connector().store(chatStore);
    }

    /** 加载好友列表 */
    private void relations() {
        Result<List<Relation>> relations
                = Client.get(RELATIONS_ENDPOINT, new TypeReference<>() {}, auth.getToken());
        if(relations.failure()){
            log.error("加载好友信息异常>>" + relations);
            throw new AppServerException("加载好友信息失败");
        }
        ObservableList<RelationViewRender.CustomBox> observableList
                = FXCollections.observableList(RelationViewRender.render(relations.getData(), listViewRelation.getWidth()));
        //模拟数据
        listViewRelation.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, oldValue, newValue) -> {
                    labelThere.setText(newValue.relation().getNickname());
                    labelThere.setGraphic(newValue.avatar());
                    reload(newValue.relation());
                });
        Platform.runLater(() -> listViewRelation.setItems(observableList));
    }

    private void reload(Relation relation){
        //获取当前好友的绑定列表，如果不存在，创建新列表
        ObservableList<HBox> observableList
                = messages.compute(relation.getRid(), (key, exists) -> {
            if(Objects.isNull(exists)){
                //初次加载，加载最新20条聊天消息
                List<Chat> chats
                        = chatStore.load(relation.getRid(), 0L, 20L);
                return FXCollections.observableList(MsgViewRender.render(account, chats, listViewMessage.getWidth()));
            }
            return exists;
        });
        Platform.runLater(() -> {
            listViewMessage.getStylesheets().add("/style/msglistview.css");
            listViewMessage.setItems(observableList);
            listViewMessage.getItems().addListener((ListChangeListener<HBox>) change -> {
                while(change.next()) {
                    listViewMessage.scrollTo(change.getTo());
                }
            });
        });
    }

    public void onCloseAction(MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> et = mouseEvent.getEventType();
        log.info("event>>" + et.getName());
        application.close();
    }

    public void onChatSubmit(MouseEvent mouseEvent){
        log.info("发送按钮触发>>" + mouseEvent.getEventType().getName());
        String text = txtInput.getText();
        Relation current = listViewRelation.getSelectionModel()
                .selectedItemProperty()
                .get().relation();
        Chat chat = new Chat()
                .setMessage(text)
                .setRoom(current.getRid())
                .setSpeaker(account.getId())
                .setNickname(account.getNickname())
                .setTs(new Date())
                .setType(Proto.ChatType.TEXT);
        chatStore.store(false, chat);
        txtInput.setText("");
    }
}
