package top.mikecao.openchat.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.MainApplication;
import top.mikecao.openchat.client.model.Friend;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.http.Client;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author mike
 */
@Slf4j
public class ChatController implements Initializable {

    private MainApplication application;
    @FXML
    private ListView<String> friendsListView;
    private final ExecutorService executorService = new ThreadPoolExecutor(4, 4,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(64),
            Executors.defaultThreadFactory()
    );

    public void application(MainApplication application){
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        executorService.execute(() -> {
            Auth auth = Storage.load("E:/temp/.auth.json", Auth.class);
            List<Friend> friends = Client.get("http://localhost:8080/friends", new TypeReference<>() {}, auth.getToken());
            List<String> list = friends.stream()
                    .map(Friend::getUsername)
                    .collect(Collectors.toList());
            ObservableList<String> observableList = FXCollections.observableList(list);
            Platform.runLater(() -> friendsListView.setItems(observableList));
        });

    }

}
