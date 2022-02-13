package top.mikecao.openchat.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.common.Generator;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.web.cmd.RelationCreateCmd;
import top.mikecao.openchat.core.entity.Relation;
import top.mikecao.openchat.web.repository.SimpleRelationRepository;

import java.util.Date;

/**
 * @author caohailong
 */

@Service
public class RelationCmdService {

    @Autowired
    private Generator<Long> generator;
    @Autowired
    private SimpleRelationRepository simpleRelationRepository;

    public Mono<Result<Long>> create(RelationCreateCmd cmd){

        long rid = generator.next();
        Relation relation = new Relation()
                .setId(generator.next())
                .setUid(cmd.getUid())
                .setFid(cmd.getFid())
                .setGroup(cmd.isGroup())
                .setRid(rid)
                .setTs(new Date())
                .setOffset(0);
        return simpleRelationRepository.insert(relation)
                .map(x -> Result.ok(x.getRid()));

    }
}
