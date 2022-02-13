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
    private String nickname;
}
