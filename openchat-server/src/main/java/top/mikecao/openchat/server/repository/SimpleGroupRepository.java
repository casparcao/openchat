package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.mikecao.openchat.server.entity.Group;

/**
 * @author caohailong
 */

public interface SimpleGroupRepository extends ReactiveMongoRepository<Group, Long> {
}
