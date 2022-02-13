package top.mikecao.openchat.web.cmd;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class LoginCmd {

    private String account;
    private String password;
}
