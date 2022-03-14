package top.mikecao.openchat.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import top.mikecao.openchat.client.payload.Login;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.http.Client;
import top.mikecao.openchat.core.serialize.Result;

import static top.mikecao.openchat.client.config.Constants.LOGIN_ENDPOINT;
import static top.mikecao.openchat.client.config.Constants.TOKEN_STORE_LOCATION;

/**
 * @author mike
 */
public class LoginController extends Parent {

    @FXML
    public PasswordField txtPassword;
    @FXML
    private Label labelClose;
    @FXML
    private TextField txtEmail;
    private MainApplication application;
    public void application(MainApplication application){
        this.application = application;
    }

    @FXML
    public void onMouseReleased(MouseEvent event){
        //尝试登录
        String email = txtEmail.getText();
        String passwd = txtPassword.getText();
        Login login = new Login()
                .setAccount(email)
                .setPassword(passwd);

        Result<Auth> result = Client.post(LOGIN_ENDPOINT, login, new TypeReference<>() {});
        if(result.success()){
            Storage.store(TOKEN_STORE_LOCATION, result.getData());
            application.main(result.getData());
        }else{
            //提示密码错误
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.titleProperty().set("提示");
            alert.contentTextProperty().set("账号或密码错误");
            alert.showAndWait();
        }
    }

    public void onMouseEntered() {
        //...
    }

    public void onMouseExited() {
        //...
    }

    public void onCloseAction() {
        application.close();
    }
}
