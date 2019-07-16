package netty;

import com.clay.model.MessageRequest;
import com.clay.model.MessageType;
import com.clay.model.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.util.ChatSession;
import sample.util.UiBaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: yuyao
 * @Date: 2019/6/3 10:49
 * @Description:
 */
public class SocketClient {

    public static Channel channel = null;



    public void start(String host , Integer port) throws InterruptedException {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new MyChannelInitializer());//
            // 异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()){
                        System.out.println("连接服务器成功...");
                    }else{
                        System.out.println("连接服务器失败 *__*");
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
    private class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {

            ChannelPipeline pipeline = ch.pipeline();
//            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//            pipeline.addLast(new StringDecoder());
//            pipeline.addLast(new StringEncoder());

            pipeline.addLast(new HttpObjectAggregator(10*1024*1024));

            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
            pipeline.addLast(new ObjectEncoder());
            pipeline.addLast(new MyHandler());
        }

    }

    static class  MyHandler extends ChannelHandlerAdapter  {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            if(ChatSession.currentUser != null){
                MessageRequest req = new MessageRequest();
                req.setUserId(ChatSession.currentUser.getId());
                req.setName(ChatSession.currentUser.getUserName());
                req.setMsgType(MessageType.ONLINE.getType());

                System.out.println(req);
                ctx.channel().writeAndFlush(req);
            }

            SocketClient.channel = ctx.channel();
        }


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("[接收到来自服务器消息]:" + msg.toString());

            MessageRequest messageRequest = (MessageRequest) msg;

            Platform.runLater(() -> {
                //群成员列表
                if(MessageType.FRIENDS.getType().equals(messageRequest.getMsgType())){
                    processFriendList(messageRequest);
                    return;
                }

                //私聊
                if(MessageType.PRIVATE.getType().equals(messageRequest.getMsgType())){
                    processPrivateMsg(messageRequest);
                    return;
                }

                //群聊
                if(MessageType.GROUP.getType().equals(messageRequest.getMsgType())){
                    processGroupMsg(messageRequest);
                    return;
                }

            });
        }

    }

    /**
     *  处理成员列表
     */
    private static void processFriendList(MessageRequest messageRequest){
        List<User> friends = (List<User>) messageRequest.getMsg();

        Stage indexStage = UiBaseService.INSTANCE.getStageController().getStageBy("index");
        ListView<Label> frendlist = (ListView) indexStage.getScene().getRoot().lookup("#friendList");
        TitledPane titledPane = (TitledPane) indexStage.getScene().getRoot().lookup("#friendNumText");


        List<Label> labelViews  = new ArrayList<>();
        friends.forEach(user -> {
            if(ChatSession.currentUser.getId().equals(user.getId())){
                return;
            }

            Label label = new Label();
            label.setMinHeight(20);
            label.setMaxWidth(135);
            label.setText(user.getUserName());
            label.setId(user.getId());
            labelViews.add(label);

        });

        titledPane.setText("群成员（" + labelViews.size() +  "）");

        frendlist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String sendTo = observable.getValue().getId();
            if(sendTo.equals(ChatSession.currentUser.getId())){
                return;
            }
            Stage privateStage = UiBaseService.INSTANCE.getStageController().getStageBy("private" + sendTo);
            if(privateStage == null){
                privateStage = UiBaseService.INSTANCE.getStageController().loadStage("private" + sendTo,
                        "sample/fxml/chatPrivate.fxml", "", StageStyle.DECORATED);
            }
            Label chatId = (Label) privateStage.getScene().getRoot().lookup("#chatId");
            chatId.setText(sendTo);
            privateStage.setTitle("正在与 " + newValue.getText() + " 聊天 ...");
            privateStage.show();
        });

        ObservableList<Label> items = FXCollections.observableArrayList (labelViews);
        frendlist.setItems(items);
    }

    /**
     *  处理群聊信息
     */
    private static void processGroupMsg(MessageRequest messageRequest){
        System.out.println("处理群聊信息");
        Stage indexStage = UiBaseService.INSTANCE.getStageController().getStageBy("index");
        if(!indexStage.isShowing()){
            indexStage.show();
        }
        VBox msgContiner = (VBox) indexStage.getScene().getRoot().lookup("#msgContiner");
        Pane pane = null;
        if(ChatSession.currentUser.getId().equals(messageRequest.getUserId())){
            pane  = UiBaseService.INSTANCE.getStageController().load("sample/fxml/chatItemRight.fxml", Pane.class);
        }else{
            pane  = UiBaseService.INSTANCE.getStageController().load("sample/fxml/chatItemLeft.fxml", Pane.class);
        }
        Hyperlink nameUi = (Hyperlink) pane.lookup("#nameUi");
        Label timeUi = (Label) pane.lookup("#timeUi");
        Label contentUi = (Label) pane.lookup("#contentUi");
        nameUi.setText(messageRequest.getName());
        timeUi.setText(messageRequest.getTime());
        contentUi.setText(messageRequest.getMsg().toString());

        msgContiner.getChildren().add(pane);
    }

    /**
     *  处理私聊信息
     */
    private static void processPrivateMsg(MessageRequest messageRequest){
        String stageName = "private" + messageRequest.getUserId();

        Stage privateStage = UiBaseService.INSTANCE.getStageController().getStageBy(stageName);
        if(privateStage == null){
            //新建私聊窗口
            privateStage = UiBaseService.INSTANCE.getStageController().
                    loadStage(stageName, "sample/fxml/chatPrivate.fxml", "正在与 "+ messageRequest.getName() +" 聊天 ...", StageStyle.DECORATED);
            Label chatId = (Label) privateStage.getScene().getRoot().lookup("#chatId");
            chatId.setText(messageRequest.getUserId());
            privateStage.show();
        }

        VBox msgContiner = (VBox) privateStage.getScene().getRoot().lookup("#msgContiner");
        Pane pane = null;
        System.out.println("当前用户：" + ChatSession.currentUser.getId());
        if(ChatSession.currentUser.getUserName().equals(messageRequest.getName())) {
            System.out.println("回传消息");
            //服务器回传自己的信息
            pane  = UiBaseService.INSTANCE.getStageController().load("sample/fxml/chatItemRight.fxml", Pane.class);
        }else{
            System.out.println("收到别人的消息");
            pane  = UiBaseService.INSTANCE.getStageController().load("sample/fxml/chatItemLeft.fxml", Pane.class);
        }
        Hyperlink nameUi = (Hyperlink) pane.lookup("#nameUi");
        Label timeUi = (Label) pane.lookup("#timeUi");
        Label contentUi = (Label) pane.lookup("#contentUi");
        nameUi.setText(messageRequest.getName());
        timeUi.setText(messageRequest.getTime());
        contentUi.setText(messageRequest.getMsg().toString());

        msgContiner.getChildren().add(pane);
    }

}