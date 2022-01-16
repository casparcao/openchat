package top.mikecao.openchat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.Result;
import top.mikecao.openchat.web.command.LoginCommand;
import top.mikecao.openchat.web.service.AuthService;
import top.mikecao.openchat.web.vo.Auth;

/**
 * @author caohailong
 */

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Mono<Result<Auth>> login (@RequestBody @Validated LoginCommand command){
        return authService.auth(command);
    }
}
