package top.mikecao.openchat.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.*;

import java.util.List;


/**
 * 每个消息都由5部分构成：
 * <ul>
 *     <li>消息总长度T：4个字节，表明整个消息的长度，因此消息最长为int值的最大值</li>
 *     <li>消息头长度H：2个字节，表明消息头的长度</li>
 *     <li>消息体长度B：4个字节，表明消息体的长度</li>
 *     <li>消息头：H个字节</li>
 *     <li>消息体：B个字节</li>
 *     <li>T = H + B</li>
 * </ul>
 *
 * 消息格式如下：
 * <p>
 *     --4--|-2-|--4--|----------n-----------|----------------------------------n-------------------------
 * </p>
 * <p>
 *     -总长-|-头长-|-体长-|----------头----------|--------------------体----------------------------------
 * </p>
 * @author mike
 */

@Component
public class ByteToObjectDecoder extends ByteToMessageDecoder {

    /**
     * 长度字节大小(头长2字节，体长4字节)
     */
    private static final int LENGTH_FIELD_SIZE = 6;
    /**
     * 如果不是格式良好的消息，直接抛弃
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int readable = in.readableBytes();
        if(readable < LENGTH_FIELD_SIZE){
            return;
        }
        //header length
        int headerLength = in.readShort();
        //body length
        int bodyLength = in.readInt();
        int total = headerLength + bodyLength;
        if(readable < total){
            return;
        }
        byte[] header = new byte[headerLength];
        in.readBytes(header);
        Headers headers = Headers.decode(header);
        byte[] body = new byte[bodyLength];
        in.readBytes(body);
        Message message = new Message(headers, body);
        out.add(message);
    }
}
