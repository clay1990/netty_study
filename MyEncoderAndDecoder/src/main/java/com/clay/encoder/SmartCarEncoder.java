package com.clay.encoder;

import com.clay.protocol.SmartCarProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:08
 * @Description:
 */
public class SmartCarEncoder extends MessageToByteEncoder<SmartCarProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SmartCarProtocol smartCarProtocol, ByteBuf byteBuf) throws Exception {

        byteBuf.writeInt(smartCarProtocol.getHead_data());
        byteBuf.writeInt(smartCarProtocol.getContentLength());
        byteBuf.writeBytes(smartCarProtocol.getContent());
    }
}