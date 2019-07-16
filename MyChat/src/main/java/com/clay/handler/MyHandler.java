package com.clay.handler;

import com.clay.model.MessageRequest;
import com.clay.model.MessageType;
import com.clay.model.User;
import com.clay.model.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Auther: yuyao
 * @Date: 2019/6/3 10:54
 * @Description:
 */
public class MyHandler extends ChannelHandlerAdapter {

    //保留所有与服务器建立连接的channel对象，这边的GlobalEventExecutor在写博客的时候解释一下，看其doc
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Map<String, UserInfo> channelMap = new ConcurrentHashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void syncFriends(){
        List<UserInfo> userInfoList = new ArrayList<>(channelMap.values());
        if(userInfoList != null && userInfoList.size() > 0){
            List<User> collect = userInfoList.stream().map(UserInfo::getUser).collect(Collectors.toList());
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setMsgType(MessageType.FRIENDS.getType());
            messageRequest.setMsg(collect);
            channelGroup.writeAndFlush(messageRequest);
        }
    }

    public MessageRequest bulidMessage(String userId,String userName,Object msg,String sendTo ,String messageType){
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setUserId(userId);
        messageRequest.setName(userName);
        messageRequest.setMsg(msg);
        messageRequest.setSendTo(sendTo);
        messageRequest.setMsgType(messageType);
        messageRequest.setTime(sdf.format(new Date()));
        return messageRequest;

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest req = (MessageRequest) msg;
        System.out.println("[接收到来自客户端的信息]:" + msg.toString());
        System.out.println("当前线程：" + Thread.currentThread().getName());

        String messageType = req.getMsgType();

        if(messageType.equals(MessageType.ONLINE.getType())){
            //用户上线
            User user = new User();
            user.setId(req.getUserId());
            user.setUserName(req.getName());
            channelMap.put(req.getUserId(),new UserInfo(user , ctx.channel()));

            channelGroup.writeAndFlush(bulidMessage(null,"系统消息",req.getName() + " 加入群聊",null,MessageType.GROUP.getType()));

            //将所有用户列表返回
            syncFriends();

        }else if(messageType.equals(MessageType.GROUP.getType())){
            channelGroup.writeAndFlush(bulidMessage(req.getUserId(),req.getName(),req.getMsg(),req.getSendTo(),MessageType.GROUP.getType()));
        }else if(messageType.equals(MessageType.PRIVATE.getType())){
            //私聊信息
            UserInfo from = channelMap.get(req.getUserId());
            UserInfo sendTo = channelMap.get(req.getSendTo());
            if(sendTo != null){
                Channel channel = sendTo.getChannel();
                if(channel == null) return;
                channel.writeAndFlush(bulidMessage(req.getUserId(),req.getName(),req.getMsg(),req.getSendTo(),MessageType.PRIVATE.getType()));
            }

            if(from != null){
                Channel channel = from.getChannel();
                if(channel == null) return;
                //也向自己发一条信息
                System.out.println("from.getChannel = " + channel.id() + ";ctx.getChannel = " + ctx.channel().id());
                channel.writeAndFlush(bulidMessage(req.getSendTo(),req.getName(),req.getMsg(),req.getUserId(),MessageType.PRIVATE.getType()));
            }
        }


    }



    //表示服务端与客户端连接建立
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器加入");
        Channel channel = ctx.channel();
        channelGroup.add(channel);


    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.remove(channel);
        channelMap.forEach((key,value) -> {
            if(channel.equals(value.getChannel())){
                channelMap.remove(key);
                channelGroup.writeAndFlush(bulidMessage(null,"系统消息",value.getUser().getUserName() + " 退出群聊",null,MessageType.GROUP.getType()));
                syncFriends();

                System.out.println();
            }
        });

    }

    //连接处于活动状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() +" 上线了");
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
}