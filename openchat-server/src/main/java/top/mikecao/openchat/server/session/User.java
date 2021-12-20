package top.mikecao.openchat.server.session;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class User {
    private long id;
    private String account;
    private String password;
}
