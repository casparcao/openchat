package top.mikecao.openchat.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.Event;
import top.mikecao.openchat.core.HKEY;
import top.mikecao.openchat.core.Headers;
import top.mikecao.openchat.core.Message;
import top.mikecao.openchat.server.entity.User;
import top.mikecao.openchat.server.repository.UserRepository;

import java.util.Optional;

/**
 * @author mike
 */

@Component
public class LoginHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Headers headers = msg.headers();
        String event = headers.first(HKEY.EVENT, "");
        //不是登录事件，不做处理
        if(!Event.AUTH.name().equals(event)){
            ctx.fireChannelRead(msg);
            return;
        }
        String username = headers.first(HKEY.UNAME, "");
        String password = headers.first(HKEY.PASSWD, "");
        boolean validate = validate(username, password);
        if(!validate){
            //todo ,返回数据格式化
            ctx.writeAndFlush(Unpooled.copiedBuffer("用户名或密码错误", CharsetUtil.UTF_8));
        }else{
            ctx.writeAndFlush(Unpooled.copiedBuffer("登录成功", CharsetUtil.UTF_8));
        }
        //do not propagate
    }

    private boolean validate(String username, String password) {
        Optional<User> optional = userRepository.findByUsername(username);
        return optional
                .map(x->x.getPassword().equals(password))
                .orElse(false);
    }
}
