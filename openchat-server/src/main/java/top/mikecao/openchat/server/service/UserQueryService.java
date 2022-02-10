package top.mikecao.openchat.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.server.repository.SimpleUserRepository;
import top.mikecao.openchat.server.session.User;
import top.mikecao.openchat.server.session.UserLoader;

/**
 * @author caohailong
 */

@Service
public class UserQueryService implements UserLoader {

    @Autowired
    private SimpleUserRepository repository;

    @Override
    public Mono<User> load(String account) {
        Mono<top.mikecao.openchat.core.entity.User> optional
                = repository.findByEmail(account);
        return optional.map(db -> new User()
                .setId(db.getId())
                .setAccount(db.getEmail())
                .setPassword(db.getPassword()));
    }
}
