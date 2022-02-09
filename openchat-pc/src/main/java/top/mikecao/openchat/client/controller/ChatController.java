package top.mikecao.openchat.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.util.concurrent.DefaultThreadFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.MainApplication;
import top.mikecao.openchat.client.model.Relation;
import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.client.service.chat.listener.ChatMsgSender;
import top.mikecao.openchat.client.service.chat.listener.ChatTableUpdater;
import top.mikecao.openchat.client.service.chat.store.RdbmsChatStore;
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

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static top.mikecao.openchat.client.config.Constants.*;

/**
 * @author mike
 */
@Slf4j
public class ChatController implements Initializable {

    private MainApplication application;
    @FXML
    private ListView<Relation> friendsListView;
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
    private TableView<Proto.Chat> tableMessages;
    @FXML
    private TableColumn<String, String> columnMessage;
    /** 正在输入的内容 */
    @FXML
    private TextArea txtContent;
    /** 发送按钮 */
    @FXML
    private Button btnSend;
    /** 消息记录列表数据源 ，每个key对应一个好友*/
    private final Map<Long, ObservableList<Proto.Chat>> messages = new HashMap<>(32);
    private final ChatStore chatStore = new RdbmsChatStore();
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
        ChatMsgSender chatMsgSender = new ChatMsgSender(this.application.connector(), this.auth.getToken());
        chatStore.listener(chatMsgSender);
    }

    public void account(String account){
        executorService.execute(() -> Platform.runLater(() -> labelAccount.setText(account)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executorService.execute(() -> {
            init();
            //加载好友列表
            relations();
        });
    }

    private void init() {
        auth = Storage.load(TOKEN_STORE_LOCATION, Auth.class);
        if(Objects.isNull(auth)){
            log.error("无Token信息");
            throw new AppServerException("无认证信息");
        }
        account = tokenGranter.resolve(auth.getToken());
        ChatTableUpdater chatTableUpdater = new ChatTableUpdater(messages);
        //配置消息存储器
        chatStore.listener(chatTableUpdater);


    }

    /** 加载好友列表 */
    private void relations() {
        Result<List<Relation>> relations
                = Client.get(RELATIONS_ENDPOINT, new TypeReference<>() {}, auth.getToken());
        if(relations.failure()){
            log.error("加载好友信息异常>>" + relations);
            throw new AppServerException("加载好友信息失败");
        }
        ObservableList<Relation> observableList = FXCollections.observableList(relations.getData());
        //模拟数据
        IntStream.range(0, 50).forEach(i -> observableList.add(new Relation().setRid(i).setName(i+"")));
        friendsListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, oldValue, newValue) -> {
                    labelThere.setText(newValue.getName());
                    reload(newValue);
                });
        Platform.runLater(() -> friendsListView.setItems(observableList));
    }

    private void reload(Relation relation){
        //获取当前好友的绑定列表，如果不存在，创建新列表
        ObservableList<Proto.Chat> observableList
                = messages.compute(relation.getRid(), (key, exists) -> {
            if(Objects.isNull(exists)){
                //初次加载，加载最新20条聊天消息
                List<Proto.Chat> chats
                        = chatStore.load(relation.getRid(), 0L, 20L);
                return FXCollections.observableList(new ArrayList<>(chats));
            }
            return exists;
        });
        IntStream.range(0, 50).forEach(i -> observableList.add(Proto.Chat.newBuilder().setMessage(i+"").build()));
        Platform.runLater(() -> {
            columnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
            tableMessages.setItems(observableList);
        });
    }

    public void onCloseAction(MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> et = mouseEvent.getEventType();
        log.info("event>>" + et.getName());
        application.close();
    }

    public void onChatSubmit(MouseEvent mouseEvent){
        log.info("发送按钮触发>>" + mouseEvent.getEventType().getName());
        String text = txtContent.getText();
        Proto.Chat chat = Proto.Chat.newBuilder()
                .setMessage(text)
                .setRoom(0L)
                .setSpeaker(account.getId())
                .setType(Proto.ChatType.TEXT)
                .build();
        chatStore.store(chat);
    }
}
