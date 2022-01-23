package top.mikecao.openchat.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import top.mikecao.openchat.client.MainApplication;
import top.mikecao.openchat.client.payload.Login;
import top.mikecao.openchat.core.auth.Auth;
import top.mikecao.openchat.core.file.Storage;
import top.mikecao.openchat.core.http.Client;
import top.mikecao.openchat.core.serialize.Result;

import java.net.*;
import java.util.ResourceBundle;

/**
 * @author mike
 */
public class LoginController implements Initializable {

    @FXML
    public PasswordField txtPassword;
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

        Result<Auth> result = Client.request("http://localhost:8080/login", login, new TypeReference<>() {});
        if(result.success()){
            Storage.store("E:/temp/.auth.json", result.getData());
            application.displayChat();
        }else{
            System.out.println(result);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //do nothing
    }

    public void onMouseEntered(MouseEvent mouseEvent) {
    }

    public void onMouseExited(MouseEvent mouseEvent) {
    }
}
