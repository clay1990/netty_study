package testReadCompeted;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: yuyao
 * @Date: 2019/6/10 17:42
 * @Description:
 */
public class EventTriggerClientHandler extends ChannelHandlerAdapter {

    private static AtomicInteger SEQ = new AtomicInteger(0);
    static final String ECHO_REQ = "Hi,welcome to Netty ";
    static final String DELIMITER = "$_";
    static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");

        Thread.sleep(1000L);
        for (int i = 0; i < 1000; i++) {
            Random random = new Random();
            UserInfo userInfo = new UserInfo();
            userInfo.setId(random.nextInt(1000000));
            userInfo.setSex("男");
            userInfo.setDesc("random desc " + UUID.randomUUID());
            ctx.writeAndFlush(userInfo);
            System.out.println(i);
        }


//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            int counter = SEQ.incrementAndGet();
//            if(counter % 10 == 0){
//                ctx.writeAndFlush(Unpooled.copiedBuffer((ECHO_REQ + DELIMITER).getBytes()));
//            }
//
//            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));



//        },0,1000, TimeUnit.MILLISECONDS);
    }


}