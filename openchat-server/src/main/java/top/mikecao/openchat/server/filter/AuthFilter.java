package top.mikecao.openchat.server.filter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.Event;
import top.mikecao.openchat.core.HKEY;
import top.mikecao.openchat.core.Headers;
import top.mikecao.openchat.core.Message;

/**
 * <p>
 * 验证该连接的用户是否已经被认证，是否已登录
 * </p>
 * <p>
 * 如果没有登录则直接返回，不再执行后续处理器
 * </p>
 * @author mike
 */
@Component
public class AuthFilter extends SimpleChannelInboundHandler<Message> {

     @Override
     protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
          Headers headers = msg.headers();
          String event = headers.first(HKEY.EVENT, "");
          //不是登录事件，则需要判断是否登录，或者token是否有效
          if(!Event.AUTH.name().equals(event)){
               String token = headers.first(HKEY.TOKEN, "");
               if(!validate(token)){
                    ctx.writeAndFlush(Unpooled.copiedBuffer("未认证的消息", CharsetUtil.UTF_8));
                    return;
               }
          }
          ctx.fireChannelRead(msg);
     }

     private boolean validate(String token) {
          System.out.println("auth executor:"+Thread.currentThread());
          return true;
     }
}
