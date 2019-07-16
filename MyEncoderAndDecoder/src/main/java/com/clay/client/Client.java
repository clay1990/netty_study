package com.clay.client;

import com.clay.inter.OrderService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:44
 * @Description:
 */
public class Client {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 连接服务器
     *
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(int port, String host) throws Exception {

        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new MyChannelHandler());//
            // 异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        ClientHandler clientHandler = channelFuture.channel().pipeline().get(ClientHandler.class);
                        RpcClientLoader.getInstance().setMessageSendHandler(clientHandler);
                    }
                }
            });

            // 等待链接关闭
            f.channel().closeFuture().sync();

            System.out.println("****__________bye__________****");

        } finally {
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }

    }

    /**
     * 网络事件处理器
     */
    private class MyChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
            pipeline.addLast(new LengthFieldPrepender(4));
            pipeline.addLast(new ObjectEncoder());
            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));

            //心跳检测
            pipeline.addLast(new IdleStateHandler(0,6,0, TimeUnit.SECONDS));
//            pipeline.addLast(new ClientIdelStateHandler());
            pipeline.addLast(new ClientHandler());


        }

    }

    public static void main(String[] args) throws Exception {

        executorService.submit(() -> {
                    for (int i = 0; i < 1000; i++) {
                        OrderService orderService = (OrderService) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class[]{OrderService.class}, new MessageSendInvocationHandler());
                        String order = orderService.order(i, i, i);
                        try {
                            Thread.sleep(8000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
        });
        executorService.shutdown();
        new Client().connect(9999,"127.0.0.1");
    }

}