package testReadCompeted;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @Auther: yuyao
 * @Date: 2019/6/11 15:44
 * @Description:
 */
public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf o, List<Object> list) throws Exception {
        final byte[] array;
        final int length = o.readableBytes();
        array = new byte[length];
        o.getBytes(o.readerIndex(),array,0,length);
        MessagePack msgPack = new MessagePack();
        list.add(msgPack.read(array));
    }
}