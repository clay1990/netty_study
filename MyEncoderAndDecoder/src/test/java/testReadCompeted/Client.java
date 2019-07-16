package testReadCompeted;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

/**
 * @Auther: yuyao
 * @Date: 2019/6/10 17:40
 * @Description:
 */
public class Client {
    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            //指定 EventLoopGroup 以处理客户端事件；适应于NIO的实现
            bootstrap.group(group)
                    //适用于NIO传输的Channel类型
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 9999))
                    //在创建Channel时，向ChannelPipeline中添加一个EchoClientHandler实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,delimiter));
//                            ch.pipeline().addLast(new StringDecoder());

                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,2,0,2));
                            ch.pipeline().addLast(new MsgPackDecoder());
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new MsgPackEncoder());

//                            ch.pipeline().addLast(new HttpObjectAggregator(10*1024*1024));
//                            ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//                            ch.pipeline().addLast(new ObjectEncoder());

                            ch.pipeline().addLast(new EventTriggerClientHandler());
                        }
                    });
            //连接到远程节点，阻塞等待直到连接完成
            ChannelFuture channelFuture = bootstrap.connect().sync();
            //阻塞，直到Channel 关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //关闭线程池并且释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Client().start();

    }
}