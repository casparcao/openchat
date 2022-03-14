package top.mikecao.openchat.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.core.registry.Server;
import top.mikecao.openchat.core.serialize.Result;

import java.util.List;

/**
 * @author caohailong
 */

@Service
public class QueryServerService {

    @Autowired
    private Registry registry;

    public Mono<Result<List<Server>>> servers(){
        return registry.fetch()
                .map(Result::ok);
    }
}
