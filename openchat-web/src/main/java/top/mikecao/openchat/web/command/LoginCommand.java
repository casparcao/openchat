package top.mikecao.openchat.web.command;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class LoginCommand {

    private String account;
    private String password;
}
