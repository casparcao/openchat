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
import top.mikecao.openchat.client.model.Friend;
import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.client.service.listener.ChatMsgSender;
import top.mikecao.openchat.client.service.listener.ChatTableUpdater;
import top.mikecao.openchat.client.service.store.RdbmsMsgStore;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.auth.granters.DefaultTokenGranter;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.http.Client;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.Json;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static top.mikecao.openchat.client.config.Constants.FRIENDS_ENDPOINT;
import static top.mikecao.openchat.client.config.Constants.TOKEN_STORE_LOCATION;

/**
 * @author mike
 */
@Slf4j
public class ChatController implements Initializable {

    private MainApplication application;
    @FXML
    private ListView<Friend> friendsListView;
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
    private final MsgStore msgStore = new RdbmsMsgStore();
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
        msgStore.listener(chatMsgSender);
    }

    public void account(String account){
        executorService.execute(() -> Platform.runLater(() -> labelAccount.setText(account)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executorService.execute(() -> {
            init();
            //加载好友列表
            friends();
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
        msgStore.listener(chatTableUpdater);
    }

    private void reload(Friend friend){
        //获取当前好友的绑定列表，如果不存在，创建新列表
        ObservableList<Proto.Chat> observableList
                = messages.compute(friend.getId(), (key, exists) -> {
            if(Objects.isNull(exists)){
                //初次加载，加载最新20条聊天消息
                List<Proto.Chat> chats = msgStore.load(account.getId(), friend.getId(), 0L, 20L);
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

    /** 加载好友列表 */
    private void friends() {
        List<Friend> friends = Client.get(FRIENDS_ENDPOINT, new TypeReference<>() {}, auth.getToken());
        ObservableList<Friend> observableList = FXCollections.observableList(friends);
        //模拟数据
        IntStream.range(0, 50).forEach(i -> observableList.add(new Friend().setId(i).setUsername(i+"")));
        friendsListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, oldValue, newValue) -> {
                    labelThere.setText(newValue.getUsername());
                    reload(newValue);
                });
        Platform.runLater(() -> friendsListView.setItems(observableList));
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
        msgStore.store(chat);
    }
}
