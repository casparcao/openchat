package top.mikecao.openchat.server.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.MsgBuilder;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.session.*;

/**
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class LoginHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private UserLoader userLoader;
    @Autowired
    private ChannelStore channelStore;
    @Autowired
    private TokenGranter tokenGranter;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        Proto.MsgType type = msg.getType();
        //不是登录事件，不做处理
        if(!Proto.MsgType.LOGIN.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        Proto.Login.Request lr = msg.getLogin().getRequest();
        String account = lr.getAccount();
        String password = lr.getPasswd();

        Mono<User> mono = userLoader.load(account);
        //只有用户存在，并且密码校验通过才算登录成功

        mono.filter(x -> validate(x, password))
                .map(x -> {
                    Proto.Message.Builder result = MsgBuilder.get(Proto.MsgType.LOGIN);
                    //生成token，并返回客户端
                    String token = tokenGranter.grant(new Account().setId(x.getId()).setUsername(x.getAccount()));
                    result.setToken(token);
                    Proto.Login.Response response = Proto.Login.Response.newBuilder()
                            .setMsg("登录成功")
                            .build();
                    Channel channel = ctx.channel();
                    //保存channel，以备后续处理
                    channelStore.store(x.getId(), channel);
                    result.setLogin(Proto.Login.newBuilder().setResponse(response).build());
                    return result.build();
                })
                .switchIfEmpty(Mono.just(
                        MsgBuilder.get(Proto.MsgType.LOGIN)
                                .setLogin(Proto.Login.newBuilder().setResponse(Proto.Login.Response.newBuilder()
                                        .setMsg("账号或者密码不正确")
                                        .build()).build())
                                .build()))
                .subscribe(ctx::writeAndFlush);
        //do not propagate
    }

    private boolean validate(User user, String password) {
        return user.getPassword().equals(password);
    }
}
