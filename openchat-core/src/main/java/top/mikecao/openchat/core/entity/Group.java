package top.mikecao.openchat.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 群聊组
 * @author caohailong
 */

@Data
@Accessors(chain = true)
@Document("group")
public class Group {
    @Id
    private long id;
    private String name;
    /** 创建人 */
    private long creator;
    /** 创建时间 */
    private Date ts;
}
