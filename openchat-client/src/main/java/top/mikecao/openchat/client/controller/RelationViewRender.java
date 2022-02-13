package top.mikecao.openchat.client.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import top.mikecao.openchat.client.model.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 消息列表渲染器
 * @author caohailong
 */

public final class RelationViewRender {

    private RelationViewRender(){}

    /**我方头像*/
    private static final Image HERE_AVATAR
            = new Image(Objects.requireNonNull(RelationViewRender.class.getResourceAsStream("/image/gavatar.jpg")));
    /**对方头像*/
    private static final Image THERE_AVATAR
            = new Image(Objects.requireNonNull(RelationViewRender.class.getResourceAsStream("/image/wavatar.jpg")));

    /**
     * 将聊天信息渲染为ui形式
     * @param relations relations
     * @return h box
     */
    public static List<CustomBox> render (List<Relation> relations, double width){
        List<CustomBox> result = new ArrayList<>();
        for (Relation relation: relations){
            CustomBox box = box(relation, width);
            result.add(box);
        }
        return result;
    }

    private static CustomBox box(Relation relation, double width){
        int rnd = ThreadLocalRandom.current().nextInt(10);
        //头像
        ImageView avatar = new ImageView(rnd > 5 ? HERE_AVATAR: THERE_AVATAR);
        avatar.setFitHeight(32);
        avatar.setFitWidth(32);

        //聊天内容
        Label content = new Label();
        content.setId("labelContent");
        content.setText(relation.getMax() > relation.getOffset() ? "有": "无" + "未读消息");
        content.setMaxWidth(width - 80D);
        content.setWrapText(true);

        Label name = new Label();
        name.setId("labelName");
        name.setText(relation.getNickname());

        //聊天内容，姓名，时间
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.getChildren().addAll(name, content);

        //最外层，横向box，头像，聊天内容，姓名，时间
        CustomBox box = new CustomBox(relation, avatar.getImage());
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(avatar, vbox);
        return box;
    }

    public static class CustomBox extends HBox {
        private final Relation relation;
        private final Image avatar;
        public CustomBox(Relation relation, Image avatar){
           super();
           this.relation = relation;
           this.avatar = avatar;
        }

        public Relation relation() {
            return relation;
        }

        public Image avatar(){
            return avatar;
        }
    }
}
