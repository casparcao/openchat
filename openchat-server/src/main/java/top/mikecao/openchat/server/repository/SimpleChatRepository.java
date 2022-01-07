package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.mikecao.openchat.server.entity.Chat;

/**
 * @author caohailong
 */

public interface SimpleChatRepository extends ReactiveMongoRepository<Chat, Long> {

}