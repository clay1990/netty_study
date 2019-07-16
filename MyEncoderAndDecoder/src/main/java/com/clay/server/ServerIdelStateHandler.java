package com.clay.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Auther: yuyao
 * @Date: 2019/5/31 11:26
 * @Description:
 */
public class ServerIdelStateHandler extends SimpleChannelInboundHandler {

    /**
     * 心跳丢失次数
     */
    private int counter = 0;


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

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //重置心跳丢失次数
        counter = 0;
        System.out.println("心跳检测消息："+ o);
        channelHandlerContext.fireChannelRead(o);
    }
}