package top.mikecao.openchat.server.service;

import java.util.List;
import java.util.Map;

/**
 * @author caohailong
 */

public interface MaxRoomChatIdService {


    /**
     * 保存room id对应对应的最大消息id
     * @param id room id
     * @param max 最大消息id
     */
    void save(long id, long max);

    /**
     * 查询指定room ids对应的最大消息id
     * @param ids room ids
     * @return 返回最大消息id列表
     */
    Map<Long, Long> load(List<Long> ids);
}
