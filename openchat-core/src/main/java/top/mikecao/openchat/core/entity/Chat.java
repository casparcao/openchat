package top.mikecao.openchat.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import top.mikecao.openchat.core.common.Strings;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Date;

/**
 * 存储消息信息
 * @author caohailong
 */

@Data
@Accessors(chain = true)
@Document("chat")
public class Chat {
    @Id
    private long id;
    /**
     * 消息发送人
     */
    @Indexed
    private long speaker;
    /** 消息发送人的昵称 */
    private String nickname;
    /**
     * 聊天室ID
     */
    @Indexed
    private long room;
    private Proto.ChatType type;
    private String message;
    @JsonFormat(pattern = Strings.DATE_TIME)
    private Date ts;

}