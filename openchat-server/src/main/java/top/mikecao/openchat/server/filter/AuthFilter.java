package top.mikecao.openchat.server.filter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.session.Auth;
import top.mikecao.openchat.server.session.TokenGranter;

/**
 * <p>
 * 验证该连接的用户是否已经被认证，是否已登录
 * </p>
 * <p>
 * 如果没有登录则直接返回，不再执行后续处理器
 * </p>
 * <p>
 *     如果已经登录，解析登录用户信息，并设置channel的用户属性，以便后续使用
 * </p>
 * @author mike
 */
@Component
@ChannelHandler.Sharable
public class AuthFilter extends SimpleChannelInboundHandler<Proto.Message> {

     public static final String AUTH_ATTR_NAME = "auth";
     @Autowired
     private TokenGranter granter;

     @Override
     protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
          Proto.MsgType type = msg.getType();
          if(Proto.MsgType.LOGIN.equals(type)){
               ctx.fireChannelRead(msg);
               return;
          }
          //不是登录事件，则需要判断是否登录，或者token是否有效
          String token = msg.getToken();
          Auth auth = granter.resolve(token);
          ctx.channel().attr(AttributeKey.valueOf(AUTH_ATTR_NAME)).set(auth);
          ctx.fireChannelRead(msg);
     }

}
