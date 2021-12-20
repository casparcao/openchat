package top.mikecao.openchat.server.session;

import reactor.core.publisher.Mono;

/**
 * @author caohailong
 */

public interface UserLoader {

    /**
     * 根据账号，查询用户信息
     * @param account account
     * @return user
     */
    Mono<User> load(String account);
}
