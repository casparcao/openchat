package top.mikecao.openchat.server.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.util.Set;

/**
 * 聊天室，群组聊天室，好友聊天室
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Room {
    @Id
    private long id;
    /** 名称 */
    private String name;
    /** 聊天室中包含的用户 */
    private Set<Long> users;
}
