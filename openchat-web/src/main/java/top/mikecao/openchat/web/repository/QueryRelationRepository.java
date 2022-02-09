package top.mikecao.openchat.web.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.web.vo.Relation;

/**
 * @author caohailong
 */

@Repository
public class QueryRelationRepository {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public Flux<Relation> list(long uid){
        MatchOperation preMatch = new MatchOperation(Criteria.where("uid").is(uid));
        //todo 需要关联群组表
        LookupOperation lookup = LookupOperation.newLookup()
                .from("user")
                .localField("fid")
                .foreignField("_id")
                .as("friend");
        UnwindOperation unwind = Aggregation.unwind("friend");
        ProjectionOperation projection = Aggregation.project(
                "friend.name",
                        "friend._id",
                        "rid",
                        "offset",
                        "group",
                        "max"
        );

        TypedAggregation<top.mikecao.openchat.web.entity.Relation> aggregation = Aggregation.newAggregation(top.mikecao.openchat.web.entity.Relation.class,
                preMatch,
                lookup,
                unwind,
                projection);
        return mongoTemplate.aggregate(aggregation, top.mikecao.openchat.web.entity.Relation.class, Relation.class);
    }
}
