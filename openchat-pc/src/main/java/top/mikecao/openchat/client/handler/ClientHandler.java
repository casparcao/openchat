package top.mikecao.openchat.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg; // (1)
        try{
            while(m.isReadable()){
                System.out.print((char)m.readByte());
                System.out.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf buf = ctx.alloc().buffer();
        byte[] headers = "EVENT:AUTH;UNAME:tom;PASSWD:jerry".getBytes();
        byte[] body = "hello world".getBytes();
        buf.writeInt(headers.length + body.length + 6);
        buf.writeShort(headers.length);
        buf.writeInt(body.length);
        buf.writeBytes(headers);
        buf.writeBytes(body);
        ctx.writeAndFlush(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        super.exceptionCaught(ctx, cause);
    }
}
