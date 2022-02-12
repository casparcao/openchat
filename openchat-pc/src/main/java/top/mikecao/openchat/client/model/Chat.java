package top.mikecao.openchat.client.model;

import lombok.Data;
import lombok.experimental.Accessors;
import top.mikecao.openchat.core.common.Strings;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Date;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Chat {
    private long id;
    /**
     * 消息发送人
     */
    private long speaker;
    /**
     * 聊天室ID
     */
    private long room;
    private Proto.ChatType type;
    private String message;
    private Date ts;

    public String getMessage(){
        return speaker + "(" + Strings.format(ts) + ") : " + message;
    }
}
