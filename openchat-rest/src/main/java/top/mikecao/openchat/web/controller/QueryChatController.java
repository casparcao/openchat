package top.mikecao.openchat.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.entity.Chat;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.web.service.QueryChatService;

import java.util.List;

/**
 * @author caohailong
 */

@RestController
public class QueryChatController {

    @Autowired
    private TokenGranter granter;
    @Autowired
    private QueryChatService queryChatService;

    @GetMapping("/chats")
    public Mono<Result<List<Chat>>> list(@RequestParam("rid") long rid,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size){
        //
        return queryChatService.list(rid, page, size);
    }
}
