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
    /** 好友id */
    private long fid;
    private Date createTime;
}
