package top.mikecao.openchat.core.registry;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 注册表，用于netty server的注册与发现
 * @author caohailong
 */

public interface Registry {

    /**
     * 将某个服务器信息注册到注册表中
     * @param server server
     */
    void register(long id, Server server);

    /**
     * 主动从注册表中移除某个服务器
     * @param id id
     */
    void unregister(long id);

    /**
     * 拉取注册表中服务器信息
     * @return list
     */
    Mono<List<Server>> fetch();
}
