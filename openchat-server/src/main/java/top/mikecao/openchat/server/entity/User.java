package top.mikecao.openchat.server.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private Set<Friend> friends;
    private Set<Group> groups;

    @Data
    @Accessors(chain = true)
    public static class Group {
        private long gid;
        private long offset;
    }
    @Data
    @Accessors(chain = true)
    public static class Friend {
        private long fid;
        private long offset;
    }
}
