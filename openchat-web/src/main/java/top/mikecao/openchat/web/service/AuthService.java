package top.mikecao.openchat.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.entity.User;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.web.cmd.LoginCmd;
import top.mikecao.openchat.web.repository.SimpleUserRepository;
import top.mikecao.openchat.core.auth.Auth;

/**
 * @author caohailong
 */

@Service
public class AuthService {

    @Autowired
    private SimpleUserRepository simpleUserRepository;
    @Autowired
    private TokenGranter tokenGranter;
    @Autowired
    private Registry registry;

    public Mono<Result<Auth>> auth(LoginCmd command){
        //1. 校验身份，通过，颁发令牌
        Mono<User> mono = simpleUserRepository.findByEmail(command.getAccount());
        //只有用户存在，并且密码校验通过才算登录成功
        //2. 返回可用的聊天服务器的ip,port，用于客户端链接
        return mono.filter(user -> validate(user, command.getPassword()))
                .map(user ->
                    //生成token，并返回客户端
                    tokenGranter.grant(new Account()
                            .setId(user.getId())
                            .setNickname(user.getNickname())))
                //查询注册的聊天服务器的路由信息
                .zipWith(registry.fetch())
                .map(x ->
                    Result.ok(new Auth()
                            .setToken(x.getT1())
                            .setServers(x.getT2())))
                .switchIfEmpty(Mono.just(Result.error("用户名或者密码错误")));
    }

    private boolean validate(User user, String password) {
        return user.getPassword().equals(password);
    }
}
