package com.clay.decoder;

import com.clay.constant.ConstantValue;
import com.clay.protocol.SmartCarProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:11
 * @Description:
 */
public class SmartCarDecoder extends ByteToMessageDecoder {
    /**
     * <pre>
     * 协议开始的标准head_data，int类型，占据4个字节.
     * 表示数据的长度contentLength，int类型，占据4个字节.
     * </pre>
     */
    public final int BASE_LENGTH = 4 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() >= BASE_LENGTH){
            if(byteBuf.readableBytes() > 2048){
                byteBuf.skipBytes(byteBuf.readableBytes());
            }

            //记录包头开始的index
            int beginReader;

            while (true){
                beginReader = byteBuf.readerIndex();
                byteBuf.markReaderIndex();
                //读到了协议的开始标志，结束while循环
                if(byteBuf.readInt() == ConstantValue.HEAD_DATA){
                    break;
                }

                byteBuf.resetReaderIndex();
                byteBuf.readByte();

                if(byteBuf.readableBytes() < BASE_LENGTH){
                    return;
                }
            }

            int length = byteBuf.readInt();
            if(byteBuf.readableBytes() < length){
                byteBuf.readerIndex(beginReader);
            }

            byte[] data = new byte[length];
            byteBuf.readBytes(data);

            SmartCarProtocol  protocol = new SmartCarProtocol(data.length,data);
            list.add(protocol);
        }
    }
}