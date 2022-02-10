package top.mikecao.openchat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.entity.Relation;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.web.service.QueryRelationService;

import java.util.List;
import java.util.stream.Collectors;

import static top.mikecao.openchat.core.auth.TokenGranter.HEADER;


/**
 * @author caohailong
 */

@RestController
public class QueryRelationController {

    @Autowired
    private TokenGranter granter;
    @Autowired
    private QueryRelationService queryRelationService;

    @GetMapping("/relations")
    public Mono<Result<List<Relation>>> list(@RequestHeader(HEADER) String token){
        Account account = granter.resolve(token);
        return queryRelationService.list(account.getId())
                .collect(Collectors.toList())
                .map(Result::ok);
    }
}
