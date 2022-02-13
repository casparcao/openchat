package top.mikecao.openchat.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Relation {
    private String nickname;
    /** 聊天室id */
    private long rid;
    /** 已读消息最大偏移量 */
    private long offset;
    private boolean group;
    /** 该聊天室中最大消息id max > offset则标识有未读消息*/
    private long max;
}
