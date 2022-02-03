package top.mikecao.openchat.web.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author caohailong
 */

@Data
@Accessors(chain = true)
public class Friends {
    private long id;
    private String username;
}
