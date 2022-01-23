package top.mikecao.openchat.client.payload;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Login {
    private String account;
    private String password;
}
