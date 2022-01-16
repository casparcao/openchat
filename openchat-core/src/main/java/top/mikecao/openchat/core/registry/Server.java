package top.mikecao.openchat.core.registry;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Server {
    private String ip;
    private int port;
}
