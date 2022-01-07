package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.mikecao.openchat.server.entity.Chat;

import java.util.List;

/**
 * @author caohailong
 */

public interface SimpleChatRepository extends ReactiveMongoRepository<Chat, Long> {

    /**
     * 查询指定聊天室的，id大于指定值的聊天记录
     * @param room rid
     * @param offset 已读消息id
     * @return chats
     */
    List<Chat> findByRoomAndIdGreaterThan(long room, long offset);
}