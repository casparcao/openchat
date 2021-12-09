package top.mikecao.openchat.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import top.mikecao.openchat.server.entity.User;

import java.util.Optional;

/**
 * @author caohailong
 */

public interface UserRepository extends MongoRepository<User, Integer> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 查询到的用户
     */
    Optional<User> findByUsername(String username);
}
