package top.mikecao.openchat.web.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
@Document(value = "user")
public class User {

    @Id
    private long id;
    @Indexed(unique = true)
    private String email;
    private String username;
    private String password;
    private Set<Room> rooms;
    private Date createTime;

    @Data
    @Accessors(chain = true)
    public static class Room {
        /**
         * 聊天室id
         */
        private long id;
        /**
         * 用户在该聊天室的最大已读消息id，每次用户进入该聊天室阅读消息后更新
         */
        private long offset;
    }
}
