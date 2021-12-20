package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.server.entity.User;

/**
 * @author caohailong
 */

public interface SimpleUserRepository extends ReactiveMongoRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * @param email 用户名
     * @return 查询到的用户
     */
    Mono<User> findByEmail(String email);
}
