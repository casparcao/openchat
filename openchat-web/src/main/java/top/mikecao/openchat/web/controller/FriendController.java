package top.mikecao.openchat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.web.service.FriendService;
import top.mikecao.openchat.web.vo.Friends;

import static top.mikecao.openchat.core.auth.TokenGranter.HEADER;


/**
 * @author caohailong
 */

@RestController
public class FriendController {

    @Autowired
    private TokenGranter granter;
    @Autowired
    private FriendService friendService;

    @GetMapping("/friends")
    public Flux<Friends> list(@RequestHeader(HEADER) String token){
        Account account = granter.resolve(token);
        return friendService.list(account.getId());
    }
}
