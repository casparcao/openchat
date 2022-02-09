package top.mikecao.openchat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.web.cmd.RelationCreateCmd;
import top.mikecao.openchat.web.service.RelationCmdService;

import static top.mikecao.openchat.core.auth.TokenGranter.HEADER;


/**
 * @author caohailong
 */

@RestController
public class RelationCmdController {

    @Autowired
    private TokenGranter granter;
    @Autowired
    private RelationCmdService relationCmdService;

    @PostMapping("/relations")
    public Mono<Result<Long>> list(@RequestHeader(HEADER) String token,
                                             @RequestBody RelationCreateCmd cmd){
        Account account = granter.resolve(token);
        return relationCmdService.create(cmd);
    }
}
