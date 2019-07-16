package com.clay.client;

import com.clay.common.MessageRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.UUID;

/**
 * @Auther: yuyao
 * @Date: 2019/5/31 13:58
 * @Description:
 */
public class ClientIdelStateHandler extends SimpleChannelInboundHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("服务端发来心跳：" + o);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state().equals(IdleState.READER_IDLE)){
                System.out.println("READER_IDLE_STATE_EVENT");
            }else if(event.state().equals(IdleState.WRITER_IDLE)){
                /**发送心跳,保持长连接*/
                String  s = "ping";
                MessageRequest ping = new MessageRequest();
                ping.setMessageId("ping:" + UUID.randomUUID().toString());
                ctx.channel().writeAndFlush(ping);
                System.out.println("心跳发送成功!");
            }else if(event.state().equals(IdleState.ALL_IDLE)){
                System.out.println("ALL_IDLE_STATE_EVENT");
            }

        }
    }
}