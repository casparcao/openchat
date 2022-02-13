package top.mikecao.openchat.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String nickname;
    private String password;

}
