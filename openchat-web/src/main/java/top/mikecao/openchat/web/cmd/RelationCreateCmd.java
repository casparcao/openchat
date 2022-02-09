package top.mikecao.openchat.web.cmd;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class RelationCreateCmd {
    /** 本人id */
    private long uid;
    /** 好友id */
    private long fid;
    /** 是否是群组 */
    private boolean group;
}
