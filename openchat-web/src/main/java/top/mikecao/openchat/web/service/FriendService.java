package top.mikecao.openchat.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.web.repository.QueryRelationRepository;
import top.mikecao.openchat.web.vo.Friends;

/**
 * @author caohailong
 */

@Service
public class FriendService {

    @Autowired
    private QueryRelationRepository queryRelationRepository;

    public Flux<Friends> list(long id) {
        return queryRelationRepository.list(id);
    }

}
