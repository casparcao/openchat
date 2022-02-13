package top.mikecao.openchat.client.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import top.mikecao.openchat.client.model.Chat;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.common.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 消息列表渲染器
 * @author caohailong
 */

public final class MsgViewRender {

    private MsgViewRender(){}

    /**我方头像*/
    private static final Image HERE_AVATAR
            = new Image(Objects.requireNonNull(MsgViewRender.class.getResourceAsStream("/image/gavatar.jpg")));
    /**对方头像*/
    private static final Image THERE_AVATAR
            = new Image(Objects.requireNonNull(MsgViewRender.class.getResourceAsStream("/image/wavatar.jpg")));

    /**
     * 将聊天信息渲染为ui形式
     * @param chats chats
     * @param current 当前登录用户
     * @return h box
     */
    public static List<HBox> render (Account current, List<Chat> chats, double width){
        List<HBox> result = new ArrayList<>();
        for (Chat chat: chats){
            HBox hbox = box(current, chat, width);
            result.add(hbox);
        }
        return result;
    }

    /**
     * 将聊天信息渲染为ui形式
     * @param chats chats
     * @param own 是否是自己发送的消息
     * @return h box
     */
    public static List<HBox> render (boolean own, List<Chat> chats, double width){
        List<HBox> result = new ArrayList<>();
        for (Chat chat: chats){
            HBox hbox = box(own, chat, width);
            result.add(hbox);
        }
        return result;
    }

    private static HBox box(boolean own, Chat chat, double width){
        //头像
        ImageView avatar = new ImageView(own ? HERE_AVATAR: THERE_AVATAR);
        avatar.setFitHeight(32);
        avatar.setFitWidth(32);

        //聊天内容
        Label content = new Label();
        content.setId("labelContent");
        content.setText(chat.getMessage());
        content.setMaxWidth(width - 80D);
        content.setWrapText(true);

        Label name = new Label();
        name.setId("labelName");
        name.setText(Strings.format(chat.getTs()) + "  " + chat.getNickname());

        //聊天内容，姓名，时间
        VBox vbox = new VBox();
        if(own){
            vbox.setAlignment(Pos.CENTER_RIGHT);
        }else{
            vbox.setAlignment(Pos.CENTER_LEFT);
        }
        vbox.getChildren().addAll(name, content);

        //最外层，横向box，头像，聊天内容，姓名，时间
        HBox hbox = new HBox();
        if(own){
            hbox.setAlignment(Pos.CENTER_RIGHT);
            hbox.getChildren().addAll(vbox, avatar);
        }else{
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(avatar, vbox);
        }
        return hbox;
    }

    private static HBox box(Account account, Chat chat, double width) {
        //是否是自己的消息
        boolean own = account.getId() == chat.getSpeaker();
        return box(own, chat, width);
    }
}
