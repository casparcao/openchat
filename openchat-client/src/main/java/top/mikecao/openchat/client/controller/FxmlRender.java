package top.mikecao.openchat.client.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.exception.AppServerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author caohailong
 */

@Slf4j
public final class FxmlRender {
    private FxmlRender(){}


    public static <T> T paint(Stage stage,
                              String fxml, double width, double height,
                              EventHandler<? super MouseEvent> onMousePressed,
                              EventHandler<? super MouseEvent> onMouseDragged){
        FXMLLoader loader = new FXMLLoader();
        Parent pane = paint(fxml, loader);
        if(Objects.nonNull(onMouseDragged)){
            pane.setOnMouseDragged(onMouseDragged);
        }
        if(Objects.nonNull(onMousePressed)){
            pane.setOnMousePressed(onMousePressed);
        }
        Scene scene = new Scene(pane, width, height);
        stage.setScene(scene);
        stage.sizeToScene();
        return loader.getController();
    }

    public static Parent paint(String fxml, FXMLLoader loader) {
        InputStream is = FxmlRender.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(FxmlRender.class.getResource(fxml));
        Parent pane;
        try {
            pane = loader.load(is);
        } catch (IOException e) {
            log.error("加载FXML失败>>", e);
            e.printStackTrace();
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
        return pane;
    }
}
