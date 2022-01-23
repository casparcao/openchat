package top.mikecao.openchat.client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.controller.ChatController;
import top.mikecao.openchat.client.controller.LoginController;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.registry.Server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author caohailong
 */

@Slf4j
public class MainApplication extends javafx.application.Application {

    private Stage stage;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 250.0;

    @Override
    public void start(Stage stage) throws Exception {
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
        Auth auth = Storage.load("E:/temp/.auth.json", Auth.class);
        if(Objects.isNull(auth)){
            //无本地token，跳到登录页进行初次登录
            displayLogin();
        }else{
            //存在，尝试连接服务器，连接成功直接跳转主页面
            String token = auth.getToken();
            List<Server> servers = auth.getServers();
            //Connector.connect(server, callbak);
        }
        stage.show();
    }

    public void displayLogin(){
        LoginController login = (LoginController) paint("/fxml/login.fxml", 450, 325);
        login.application(this);
    }
    public void displayChat(){
        ChatController chat = (ChatController) paint("/fxml/chat.fxml", 800, 600);
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

}
