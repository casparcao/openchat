package top.mikecao.openchat.client.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.connection.Connector;
import top.mikecao.openchat.core.auth.Auth;
import java.util.Objects;

/**
 * @author caohailong
 */

@Slf4j
public class MainApplication extends javafx.application.Application {

    private Stage stage;
    private Connector connector;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 250.0;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("openchat");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
            Platform.runLater(() -> {
                throwable.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.titleProperty().set("异常");
                alert.contentTextProperty().set(throwable.getMessage());
                alert.showAndWait();
            })
        );
        //1. 检查本地是否存在token，以及server列表，如果存在直接使用
        //1.1. 尝试使用本地token连接server，如果成功则直接跳转到主页面
        //1.2. 否则，跳转到登录页
        //2. 如果不存在token，跳转到登录页
        //Auth auth = Storage.load(TOKEN_STORE_LOCATION, Auth.class);
        //if(Objects.isNull(auth)){
            //无本地token，跳到登录页进行初次登录
            login();
        //}else{
            //存在，尝试连接服务器，连接成功直接跳转主页面
            //main(auth);
        //}
        stage.getIcons().add(new Image(
                Objects.requireNonNull(MainApplication.class.getResourceAsStream("/image/logo.png"))));
        stage.show();
    }

    private double xOffset = 0;
    private double yOffset = 0;

    public void login(){
        LoginController login = FxmlRender.paint(stage, "/fxml/login.fxml", 450, 325,
                onMousePressed, onMouseDragged);
        login.application(this);
    }

    private final EventHandler<MouseEvent> onMousePressed = event -> {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    };
    private final EventHandler<MouseEvent> onMouseDragged = event -> {
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    };

    public void main(Auth auth){
        connector = new Connector(auth.getToken(), this);
        connector.connect();
        ChatController chat = FxmlRender.paint(stage, "/fxml/chat.fxml", 800, 600,
                onMousePressed, onMouseDragged);
        chat.application(this);
    }

    public Connector connector(){
        return this.connector;
    }

    private volatile boolean closed = false;

    public boolean closed(){
        return this.closed;
    }

    public void close(){
        this.closed = true;
        if(Objects.nonNull(connector)){
            connector.close();
        }
        stage.close();
        Platform.exit();
    }

    public Stage stage(){
        return this.stage;
    }
}
