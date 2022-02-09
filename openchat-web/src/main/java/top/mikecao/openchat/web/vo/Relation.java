package top.mikecao.openchat.web.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Relation {
    /** 好友或者群组 id*/
    private long id;
    private String name;
    /** 聊天室id */
    private long rid;
    /** 已读消息最大偏移量 */
    private long offset;
    private boolean group;
    private long max;

}
