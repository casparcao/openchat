package top.mikecao.openchat.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.entity.User;

/**
 * @author caohailong
 */

@Repository
public class MgmtUserRepository {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public void ack(long uid, Proto.Ack ack) {
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(uid).and("rooms._id").is(ack.getRoom())),
                Update.update("rooms.$.offset", ack.getOffset()),
                User.class);
    }
}
