package top.mikecao.openchat.web.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.web.entity.Relation;
import top.mikecao.openchat.web.vo.Friends;

/**
 * @author caohailong
 */

@Repository
public class QueryRelationRepository {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public Flux<Friends> list(long uid){
        MatchOperation preMatch = new MatchOperation(Criteria.where("uid").is(uid));
        LookupOperation lookup = LookupOperation.newLookup()
                .from("user")
                .localField("fid")
                .foreignField("_id")
                .as("friend");
        UnwindOperation unwind = Aggregation.unwind("friend");
        ProjectionOperation projection = Aggregation.project(
                "friend.username",
                        "friend._id"
        );

        TypedAggregation<Relation> aggregation = Aggregation.newAggregation(Relation.class,
                preMatch,
                lookup,
                unwind,
                projection);
        return mongoTemplate.aggregate(aggregation, Relation.class, Friends.class);
    }
}
