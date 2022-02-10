package top.mikecao.openchat.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
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
    /**
     * 聊天室ID
     */
    @Indexed
    private long room;
    private Proto.ChatType type;
    private String message;
    private Date ts;

}