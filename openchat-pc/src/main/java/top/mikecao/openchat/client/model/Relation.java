package top.mikecao.openchat.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Relation {
    private String name;
    /** 聊天室id */
    private long rid;
    /** 已读消息最大偏移量 */
    private long offset;
    private boolean group;

    @Override
    public String toString(){
        return name;
    }
}
