package top.mikecao.openchat.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.entity.Chat;
import top.mikecao.openchat.core.serialize.Result;
import top.mikecao.openchat.web.repository.QueryChatRepository;

import java.util.List;

/**
 * @author caohailong
 */

@Service
public class QueryChatService {

    @Autowired
    private QueryChatRepository queryChatRepository;

    public Mono<Result<List<Chat>>> list(long rid, int page, int size) {
        Flux<Chat> flux = queryChatRepository.findByRid(rid, page, size);
        Mono<Long> mono = queryChatRepository.countByRid(rid);
        return flux.collectList()
                .map(Result::ok)
                .zipWith(mono)
                .map(x -> x.getT1().total(x.getT2()));
    }

}
