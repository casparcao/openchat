package top.mikecao.openchat.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
@Document(value = "relation")
@CompoundIndexes(value = {
        @CompoundIndex(unique = true, name = "uk_u_f", def = "{'uid': 1, 'fid': 1}"),
        @CompoundIndex(unique = true, name = "uk_r_u", def = "{'rid': 1, 'uid': 1}")
})
public class Relation {

    private long id;
    /** 用户id */
    private long uid;
    /** 好友id，群组id */
    private long fid;
    /** 聊天室id */
    private long rid;
    /** 聊天室昵称，好友昵称，或者群组名称 */
    private String nickname;
    /** 是否是群聊 */
    private boolean group;
    /** 加入时间 */
    private Date ts;
    /** 已读最大消息的偏移量 */
    private long offset;
    /** 最大消息id max > offset标识有未读消息*/
    private long max;
}
