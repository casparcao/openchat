package top.mikecao.openchat.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.core.entity.Relation;
import top.mikecao.openchat.web.repository.SimpleRelationRepository;

/**
 * @author caohailong
 */

@Service
public class QueryRelationService {

    @Autowired
    private SimpleRelationRepository simpleRelationRepository;

    public Flux<Relation> list(long uid) {
        return simpleRelationRepository.findByUid(uid);
    }

}
