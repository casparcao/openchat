package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.mikecao.openchat.server.entity.Room;

/**
 * @author caohailong
 */

public interface SimpleRoomRepository extends ReactiveMongoRepository<Room, Long> {
}
