package top.mikecao.openchat.web.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.entity.Chat;

/**
 * @author caohailong
 */

@Repository
public class QueryChatRepository {

    @Autowired
    private ReactiveMongoTemplate template;

    public Flux<Chat> findByRid(long rid, int page, int size) {
        Criteria criteria = Criteria.where("rid").is(rid);
        Query query = Query.query(criteria);
        //page is zero based
        query.skip((long)page * size).limit(size);
        return template.find(query, Chat.class);
    }

    public Mono<Long> countByRid(long rid) {
        Criteria criteria = Criteria.where("rid").is(rid);
        Query query = Query.query(criteria);
        return template.count(query, Chat.class);
    }
}
