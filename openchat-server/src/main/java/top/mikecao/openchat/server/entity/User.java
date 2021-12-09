package top.mikecao.openchat.server.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author caohailong
 */

@Data
@Document(value = "user")
public class User {

    @Id
    private int id;
    private String username;
    private String password;

}
