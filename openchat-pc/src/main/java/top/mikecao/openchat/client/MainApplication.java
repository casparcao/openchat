package top.mikecao.openchat.client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.connection.Connector;
import top.mikecao.openchat.client.controller.ChatController;
import top.mikecao.openchat.client.controller.LoginController;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.exception.AppServerException;

import java.io.IOException;
import java.io.InputStream;
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
            //main("a@b.com", auth);
        //}
        stage.getIcons().add(new Image(
                Objects.requireNonNull(MainApplication.class.getResourceAsStream("/image/logo.png"))));
        stage.show();
    }

    public void login(){
        LoginController login = (LoginController) paint("/fxml/login.fxml", 450, 325);
        login.application(this);
    }
    public void main(String email, Auth auth){
        this.connector = new Connector();
        connector.connect(auth.getToken(), auth.getServers());
        ChatController chat = (ChatController) paint("/fxml/chat.fxml", 800, 600);
        chat.account(email);
        chat.application(this);
    }

    public Initializable paint(String fxml, double width, double height){
        FXMLLoader loader = new FXMLLoader();
        InputStream is = this.getClass().getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(this.getClass().getResource(fxml));
        BorderPane pane;
        try {
            pane = loader.load(is);
        } catch (IOException e) {
            log.error("加载FXML失败>>", e);
            throw new AppServerException("页面加载失败");
        }finally {
            try {
                if (Objects.nonNull(is)) {
                    is.close();
                }
            }catch (IOException e){
                log.error("流关闭异常>>", e);
            }
        }
        Scene scene = new Scene(pane, width, height);
        stage.setScene(scene);
        stage.sizeToScene();
        return loader.getController();
    }

    public Connector connector(){
        return this.connector;
    }

    public void close(){
        connector.close();
        stage.close();
    }
}
