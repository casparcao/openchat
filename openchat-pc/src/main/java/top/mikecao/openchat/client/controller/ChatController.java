package top.mikecao.openchat.client.controller;

import javafx.fxml.Initializable;
import top.mikecao.openchat.client.MainApplication;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author mike
 */
public class ChatController implements Initializable {

    private MainApplication application;

    public void application(MainApplication application){
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
