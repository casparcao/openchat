package top.mikecao.openchat.web.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
@Document(value = "relation")
@CompoundIndex(unique = true, name = "uk_u_f", def = "{'uid': 1, 'fid': 1}")
public class Relation {

    private long id;
    /** 用户id */
    private long uid;
    /** 好友id，群组id */
    private long fid;
    /** 聊天室id，如果是群组，则群组id=房间id(fid=rid) */
    private long rid;
    /** 是否是群聊 */
    private boolean group;
    /** 创建时间*/
    private Date ts;
    /** 已读最大消息的偏移量 */
    private long offset;
    /** 最大消息id max > offset标识有未读消息*/
    private long max;
}
