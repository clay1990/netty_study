package com.clay.inializer;

import com.clay.handler.MyHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Auther: yuyao
 * @Date: 2019/5/31 14:31
 * @Description:
 */
public class MyChatInializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
//        //HttpServerCodec: 针对http协议进行编解码
//        pipeline.addLast(new HttpServerCodec());
//        //ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
//        pipeline.addLast(new ChunkedWriteHandler());
//        /**
//         * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
//         * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
//         */
//        pipeline.addLast(new HttpObjectAggregator(8192));
//        //用于处理websocket, /ws为访问websocket时的uri
//        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
//        socketChannel.pipeline().addLast(new MyChatHandler());

//        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//        pipeline.addLast(new StringDecoder());
//        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new HttpObjectAggregator(10*1024*1024));
        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new MyHandler());

    }
}