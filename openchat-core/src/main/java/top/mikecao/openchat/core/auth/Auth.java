package top.mikecao.openchat.core.auth;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Auth {
    private String token;
}
