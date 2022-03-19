package top.mikecao.openchat.client.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.stage.Popup;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caohailong
 */

@Slf4j
public final class EmojiSearcher {

    private EmojiSearcher(){}

    private static final Popup POPUP;
    private static final EmojiSearchController CONTROLLER;

    static {
        POPUP = new Popup();
        FXMLLoader loader = new FXMLLoader();
        Parent pane = FxmlRender.paint("/fxml/emojis.fxml", loader);
        CONTROLLER = loader.getController();
        CONTROLLER.popup(POPUP);
        POPUP.setWidth(492);
        POPUP.setHeight(300);
        POPUP.getContent().add(pane);
        POPUP.setAutoHide(true);
    }

    public static void display(Stage stage, double x, double y,
                               TextArea area){
        POPUP.setX(x);
        POPUP.setY(y);
        POPUP.show(stage);
        CONTROLLER.area(area);
    }
}
