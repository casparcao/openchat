package top.mikecao.openchat.core.auth;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 认证信息
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Account {
    private long id;
    /** 邮箱或者唯一账号 */
    private String username;
}
