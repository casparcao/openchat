package top.mikecao.openchat.web.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import top.mikecao.openchat.web.entity.Relation;

/**
 * @author caohailong
 */

public interface SimpleRelationRepository extends ReactiveMongoRepository<Relation, Long> {

}