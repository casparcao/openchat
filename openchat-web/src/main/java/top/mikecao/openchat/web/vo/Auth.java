package top.mikecao.openchat.web.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import top.mikecao.openchat.core.registry.Server;

import java.util.List;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Auth {

    private String token;
    private List<Server> servers;
}
