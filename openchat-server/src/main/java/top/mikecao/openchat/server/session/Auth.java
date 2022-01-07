package top.mikecao.openchat.server.session;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 认证信息
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Auth {
    private long id;
    private String account;
}
