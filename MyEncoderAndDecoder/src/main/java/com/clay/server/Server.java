package com.clay.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:27
 * @Description:
 */
public class Server {

    public Server(){}

    public void bind(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChildInitizlizer())
                    .option(ChannelOption.SO_BACKLOG , 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    private class ChildInitizlizer extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
            pipeline.addLast(new LengthFieldPrepender(4));
            pipeline.addLast(new ObjectEncoder());
            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));

            pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
//            pipeline.addLast(new ServerIdelStateHandler());
            pipeline.addLast(new ServerHandler());

        }
    }

    public static void main(String[] args) {
        new Server().bind(9999);
    }
}