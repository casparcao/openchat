package top.mikecao.openchat.server.filter;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mikecao.openchat.core.serialize.MsgBuilder;
import top.mikecao.openchat.core.exception.AppAuthException;
import top.mikecao.openchat.core.exception.AppClientException;
import top.mikecao.openchat.core.proto.Proto;

/**
 * 统一异常处理器
 * @author caohailong
 */

@Slf4j
@Service
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelDuplexHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        process(ctx, cause);
        //stop propagation
    }

    private void process(ChannelHandlerContext ctx, Throwable cause) {
        Proto.Error.Builder builder = Proto.Error.newBuilder();
        if(cause instanceof AppClientException){
            log.warn("客户端数据异常>>", cause);
            AppClientException e = (AppClientException) cause;
            builder.setCode(e.code())
                    .setMessage(e.getMessage());
        } else if (cause instanceof AppAuthException){
            log.warn("认证异常>>", cause);
            builder.setCode(401)
                    .setMessage("身份认证失败");
        }else {
            log.error("服务端数据异常>>", cause);
            builder.setCode(500)
                    .setMessage("系统内部异常");
        }
        Proto.Message msg = MsgBuilder.get(Proto.MsgType.ERR)
                .setError(builder.build())
                .build();
        ctx.writeAndFlush(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise.addListener(future -> {
            if(!future.isSuccess()){
                Throwable throwable = future.cause();
                process(ctx, throwable);
            }
        }));
    }
}
