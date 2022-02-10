package top.mikecao.openchat.web.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.core.entity.Relation;

/**
 * @author caohailong
 */

public interface SimpleRelationRepository extends ReactiveMongoRepository<Relation, Long> {

    /**
     * 查询用户的好友关系
     * @param uid uid
     * @return list
     */
    Flux<Relation> findByUid(long uid);
}