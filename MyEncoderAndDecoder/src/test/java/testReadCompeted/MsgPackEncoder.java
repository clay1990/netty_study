package testReadCompeted;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @Auther: yuyao
 * @Date: 2019/6/11 15:42
 * @Description:
 */
public class MsgPackEncoder extends MessageToByteEncoder{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack msgPack = new MessagePack();
        byteBuf.writeBytes(msgPack.write(o));
    }
}