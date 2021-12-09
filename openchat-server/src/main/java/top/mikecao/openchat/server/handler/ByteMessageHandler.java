package top.mikecao.openchat.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

/**
 * 处理文件
 *
 * @author mike
 */
@Component
public class ByteMessageHandler extends ChannelInboundHandlerAdapter {
    private static ChannelGroup registers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        registers.add(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg; // (1)
        try{
            System.out.println("=================");
            while(m.isReadable()){
                System.out.print((char)m.readByte());
            }
            System.out.println("=================");
        }catch (Exception e){
            e.printStackTrace();
        }
        registers.writeAndFlush(Unpooled.copiedBuffer("received", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        super.exceptionCaught(ctx, cause);
    }
}
