package top.mikecao.openchat.server.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.MsgBuilder;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.session.ChannelStore;
import top.mikecao.openchat.server.session.TokenGranter;
import top.mikecao.openchat.server.session.User;
import top.mikecao.openchat.server.session.UserLoader;

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
        Proto.LoginRequest lr = msg.getLogin();
        String account = lr.getAccount();
        String password = lr.getPasswd();

        Mono<User> mono = userLoader.load(account);
        //只有用户存在，并且密码校验通过才算登录成功

        mono.filter(x -> validate(x, password))
                .map(x -> {
                    Proto.Message.Builder result = MsgBuilder.get(Proto.MsgType.LOGIN);
                    //生成token，并返回客户端
                    String token = tokenGranter.grant(x);
                    result.setToken(token);
                    Proto.Response response = Proto.Response.newBuilder()
                            .setCode(200)
                            .setMessage("登录成功")
                            .build();
                    Channel channel = ctx.channel();
                    //保存channel，以备后续处理
                    channelStore.store(x.getId(), channel);
                    result.setResponse(response);
                    return result.build();
                })
                .switchIfEmpty(Mono.just(
                        MsgBuilder.get(Proto.MsgType.LOGIN)
                                .setResponse(Proto.Response.newBuilder()
                                        .setCode(400)
                                        .setMessage("账号或者密码不正确")
                                        .build())
                                .build()))
                .subscribe(ctx::writeAndFlush);
        //do not propagate
    }

    private boolean validate(User user, String password) {
        return user.getPassword().equals(password);
    }
}
