package top.mikecao.openchat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.registry.Server;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.web.service.QueryServerService;

import java.util.List;

/**
 * @author caohailong
 */

@RestController
public class QueryServerController {

    @Autowired
    private QueryServerService queryServerService;

    @GetMapping("/servers")
    public Mono<Result<List<Server>>> servers (){
        return queryServerService.servers();
    }
}
