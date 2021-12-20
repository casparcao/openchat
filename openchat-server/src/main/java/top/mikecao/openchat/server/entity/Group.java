package top.mikecao.openchat.server.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * 群组，每个群组包含n个人，最大100人
 * @author caohailong
 */

@Data
@Accessors(chain = true)
@Document("group")
public class Group {
    @Id
    private long id;
    /** 名称 */
    private String name;
    /** 群组中包含的用户 */
    private Set<Long> users;
}
