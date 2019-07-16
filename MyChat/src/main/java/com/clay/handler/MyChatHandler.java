package com.clay.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Date;

/**
 * @Auther: yuyao
 * @Date: 2019/5/31 14:18
 * @Description:
 */
public class MyChatHandler extends SimpleChannelInboundHandler<Object> {

    //保留所有与服务器建立连接的channel对象，这边的GlobalEventExecutor在写博客的时候解释一下，看其doc
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final String WEB_SOCKET_URL = "ws://localhost:9999/ws";
    private WebSocketServerHandshaker handshaker;



    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if(o instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest)o;
            this.handHttpRequest(channelHandlerContext,request);
        }else if(o instanceof WebSocketFrame){
            WebSocketFrame frame = (WebSocketFrame)o;
            this.handWebSocketFrame(channelHandlerContext,frame);
        }
    }


    //表示服务端与客户端连接建立
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器加入");
        Channel channel = ctx.channel();
        /**
         * 广播
         */
        TextWebSocketFrame tws = new TextWebSocketFrame(" 【服务器】 -" +channel.remoteAddress() +" 加入\n");
        channelGroup.writeAndFlush(tws);
        channelGroup.add(channel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        TextWebSocketFrame tws = new TextWebSocketFrame(" 【服务器】 -" +channel.remoteAddress() +" 离开\n");
        channelGroup.writeAndFlush(tws);

        //验证一下每次客户端断开连接，连接自动地从channelGroup中删除调。
        System.out.println(channelGroup.size());
        //当客户端和服务端断开连接的时候，下面的那段代码netty会自动调用，所以不需要人为的去调用它
        //channelGroup.remove(channel);
    }

    //连接处于活动状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() +" 上线了");

        StringBuilder friends = new StringBuilder();
        channelGroup.forEach(channel1 -> {
            friends.append(channel1.id()).append(";");
        });
        TextWebSocketFrame friendList = new TextWebSocketFrame("friends:" + friends.toString());
        channelGroup.writeAndFlush(friendList);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() +" 下线了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private void handWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        if(frame instanceof CloseWebSocketFrame){
            //如果是关闭websocket的指令
//            handshaker.close(ctx.channel(),(CloseWebSocketFrame) frame.retain());
        }
        if(frame instanceof PingWebSocketFrame){
            //如果是ping消息
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if(!(frame instanceof TextWebSocketFrame)){
            System.out.println("目前暂不支持二进制消息 ");
            throw new RuntimeException("目前暂不支持二进制消息 ");
        }

        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("服务端收到客户端消息 =======>>>>" + request);
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + "-" + ctx.channel().id() + "====>>>" + request);

        Channel channel = ctx.channel();
        System.out.println("channelGroup.size = " + channelGroup.size());
        channel.writeAndFlush(tws.retain());
        channelGroup.forEach(channel1 -> {
            if (channel != channel1){
                channel1.writeAndFlush(tws.retain());
            }
        });
//        NettyConfig.group.writeAndFlush(tws);
    }

    private void handHttpRequest(ChannelHandlerContext ctx,FullHttpRequest request){
        if(!request.getDecoderResult().isSuccess() || !("websocket").equals(request.headers().get("Upgrade"))){
            //不是websocket请求
            this.sendHttpResponse(ctx,request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.BAD_REQUEST));
            return;
        }


        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL,null,false);
        handshaker = wsFactory.newHandshaker(request);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(),request);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpMessage request, DefaultFullHttpResponse response){
        if(response.getStatus().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if(response.getStatus().code() != 200){
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

}