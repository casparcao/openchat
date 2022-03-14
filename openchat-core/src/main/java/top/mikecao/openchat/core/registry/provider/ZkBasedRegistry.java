package top.mikecao.openchat.core.registry.provider;

import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.core.registry.Server;

import java.util.List;

/**
 * 待实现
 * @author caohailong
 */

public class ZkBasedRegistry implements Registry {

    @Override
    public void register(long id, Server server) {
        //...
    }

    @Override
    public void unregister(long id) {
        //...
    }

    @Override
    public Mono<List<Server>> fetch() {
        return null;
    }
}
