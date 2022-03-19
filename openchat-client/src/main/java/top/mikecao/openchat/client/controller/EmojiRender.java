package top.mikecao.openchat.client.controller;

import com.pavlobu.emojitextflow.EmojiTextFlow;
import com.pavlobu.emojitextflow.EmojiTextFlowParameters;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * @author caohailong
 */

public final class EmojiRender {

    private EmojiRender(){}

    private static final EmojiTextFlowParameters PARAM;
    static {
        PARAM = new EmojiTextFlowParameters();
        PARAM.setEmojiScaleFactor(1D);
        PARAM.setTextAlignment(TextAlignment.CENTER);
        PARAM.setFont(Font.font("微软雅黑", FontWeight.THIN, 14));
        PARAM.setTextColor(Color.BLACK);
    }

    public static EmojiTextFlow draw(String content, TextAlignment alignment){
        PARAM.setTextAlignment(alignment);
        EmojiTextFlow etf = new EmojiTextFlow(PARAM);
        etf.parseAndAppend(content);
        return etf;
    }
}
