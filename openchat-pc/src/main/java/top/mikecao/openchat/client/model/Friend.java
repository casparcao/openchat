package top.mikecao.openchat.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Friend {
    private long id;
    private String username;
}
