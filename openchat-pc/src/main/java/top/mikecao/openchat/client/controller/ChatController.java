package top.mikecao.openchat.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.util.concurrent.DefaultThreadFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.MainApplication;
import top.mikecao.openchat.client.model.Friend;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.http.Client;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static top.mikecao.openchat.client.config.Constants.FRIENDS_ENDPOINT;
import static top.mikecao.openchat.client.config.Constants.TOKEN_STORE_LOCATION;

/**
 * @author mike
 */
@Slf4j
public class ChatController implements Initializable {

    @FXML
    public Label labelClose;
    private MainApplication application;
    @FXML
    private ListView<Friend> friendsListView;
    @FXML
    private Label labelThere;
    @FXML
    private Label labelAccount;
    private final ExecutorService executorService = new ThreadPoolExecutor(4, 4,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(64),
            new DefaultThreadFactory(ChatController.class)
    );

    public void application(MainApplication application){
        this.application = application;
    }

    public void account(String account){
        executorService.execute(() -> Platform.runLater(() -> labelAccount.setText(account)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executorService.execute(() -> {
            Auth auth = Storage.load(TOKEN_STORE_LOCATION, Auth.class);
            if(Objects.isNull(auth)){
                log.error("无Token信息");
                throw new AppServerException("无认证信息");
            }

            List<Friend> friends = Client.get(FRIENDS_ENDPOINT, new TypeReference<>() {}, auth.getToken());
            ObservableList<Friend> observableList = FXCollections.observableList(friends);
            IntStream.range(0, 50).forEach(i -> {
                observableList.add(new Friend().setId(i).setUsername(i+""));
            });
            friendsListView.getSelectionModel().selectedItemProperty().addListener((ob, oldValue, newValue) -> {
                labelThere.setText(newValue.getUsername());
            });
            Platform.runLater(() -> friendsListView.setItems(observableList));
        });

    }

    public void onMouseReleased(MouseEvent mouseEvent) throws Exception {
        EventType<? extends MouseEvent> et = mouseEvent.getEventType();
        log.info("event>>" + et.getName());
        application.close();
    }
}
