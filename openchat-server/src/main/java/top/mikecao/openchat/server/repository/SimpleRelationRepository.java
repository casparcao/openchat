package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.core.entity.Relation;

/**
 * @author caohailong
 */

public interface SimpleRelationRepository extends ReactiveMongoRepository<Relation, Long> {

    /**
     * 查询指定聊天室下的所有好友关系
     * @param rid rid 聊天室id
     * @return list
     */
    Flux<Relation> findByRid(long rid);
}
