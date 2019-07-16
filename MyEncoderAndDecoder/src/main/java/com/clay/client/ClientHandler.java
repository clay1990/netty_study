package com.clay.client;

import com.clay.common.MessageCallBack;
import com.clay.common.MessageRequest;
import com.clay.common.MessageResponse;
import com.clay.common.PingMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:46
 * @Description:
 */
public class ClientHandler extends ChannelHandlerAdapter {

    private volatile Channel channel;

    private ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive...");
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 用于获取客户端发来的数据信息
            System.out.println("Client接受的服务器的信息 :" + msg.toString());
            MessageResponse response = (MessageResponse) msg;
            String messageId = response.getMessageId();
            MessageCallBack callBack = mapCallBack.get(messageId);
            if (callBack != null) {
                mapCallBack.remove(messageId);
                callBack.over(response);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
        System.out.println("channelRegistered...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            System.out.println(((IdleStateEvent) evt).state());
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state().equals(IdleState.READER_IDLE)){
                System.out.println("READER_IDLE_STATE_EVENT");
            }else if(event.state().equals(IdleState.WRITER_IDLE)){
                /**发送心跳,保持长连接*/
                String  s = "ping";
                PingMessage ping = new PingMessage();
                ping.setMessageId("ping:" + UUID.randomUUID().toString());
                ping.setMessage("心跳信息");
                channel.writeAndFlush(ping);
                System.out.println("心跳发送成功!");
            }else if(event.state().equals(IdleState.ALL_IDLE)){
                System.out.println("ALL_IDLE_STATE_EVENT");
            }

        }
    }


    public MessageCallBack sendToServer(MessageRequest request){
        MessageCallBack callBack = new MessageCallBack(request);
        mapCallBack.put(request.getMessageId(),callBack);
        channel.writeAndFlush(request);
        return callBack;
    }
}