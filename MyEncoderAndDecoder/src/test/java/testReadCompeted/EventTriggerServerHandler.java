package testReadCompeted;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Auther: yuyao
 * @Date: 2019/6/10 17:36
 * @Description:
 */
public class EventTriggerServerHandler extends ChannelHandlerAdapter {
    int counter;
    int readCompleteTimes;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String body = (String) msg;
//        System.out.println("This is " + ++counter + " times receice client: [" + body + "]");
//        body += "$_";
//        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
//        ctx.writeAndFlush(echo);

        System.out.println(msg);


    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelReadComplete();
//        readCompleteTimes++;
//        System.out.println("This is " + readCompleteTimes + " times receive ReadComplete event.");
//    }
}