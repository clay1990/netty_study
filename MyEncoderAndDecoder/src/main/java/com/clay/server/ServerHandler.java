package com.clay.server;

import com.clay.common.MessageRequest;
import com.clay.common.MessageResponse;
import com.clay.common.PingMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.lang.reflect.Method;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:34
 * @Description:
 */
public class ServerHandler extends ChannelHandlerAdapter {




    private volatile Channel channel;

    /**
     * 心跳丢失次数
     */
    private int counter = 0;

    public Channel getChannel() {
        return channel;
    }



    /**
     *  注册时装channel赋值
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel  = ctx.channel();
        System.out.println("channelRegistered ... ");
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channelActive ... ");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("channelInactive ... ");
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //重置心跳丢失次数
        counter = 0;


        if(msg instanceof PingMessage){
            PingMessage request = (PingMessage) msg;
            System.out.println("Server接受的客户端的心跳信息 :" + request.toString());
            return;
        }

        // 用于获取客户端发来的数据信息
        MessageRequest request = (MessageRequest) msg;
        System.out.println("Server接受的客户端的信息 :" + request.toString());


        Method declaredMethod = Class.forName(request.getClassName()).getDeclaredMethod(request.getMethodName(), request.getTypeParameters());
        Object invoke = declaredMethod.invoke(BeanLoader.beanMap.get(request.getClassName()), request.getParametersVal());


        MessageResponse response = new MessageResponse();
        response.setMessageId(request.getMessageId());
        response.setResult("channelId:" + ctx.channel().id() +"=====>调用订单接口成功:" + invoke);
        ctx.writeAndFlush(response);
        // .addListener(ChannelFutureListener.CLOSE);

        // 当有写操作时，不需要手动释放msg的引用
        // 当只有读操作时，才需要手动释放msg的引用
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state().equals(IdleState.READER_IDLE)){
                if (counter >= 3) {
                    ctx.channel().close().sync();
                    System.out.println("已与" + ctx.channel().remoteAddress() + "断开连接.");
                }else {
                    counter ++;
                    System.out.println(ctx.channel().remoteAddress() + "丢失了第 " + counter + " 个心跳包");
                }
            }
        }
    }


}