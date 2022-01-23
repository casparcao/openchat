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

    /** 未知服务器信息 */
    public static final Server UNKNOWN = new Server();
}
