package testReadCompeted;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Auther: yuyao
 * @Date: 2019/6/10 17:33
 * @Description:
 */
public class Server {

    public void bind(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,delimiter));

//                            ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
//                            ch.pipeline().addLast(new StringDecoder());

                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,2,0,2));
                            ch.pipeline().addLast(new MsgPackDecoder());
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new MsgPackEncoder());


//                            ch.pipeline().addLast(new HttpObjectAggregator(10*1024*1024));
//                            ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new EventTriggerServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG , 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅释放线程资源
            System.out.println("优雅释放线程资源");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args){
        new Server().bind(9999);
    }
}