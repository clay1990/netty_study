package sample.controller;

import com.clay.model.MessageRequest;
import com.clay.model.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import netty.SocketClient;
import sample.util.ChatSession;
import sample.util.ControlledStage;
import sample.util.StageController;
import sample.util.UiBaseService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Auther: yuyao
 * @Date: 2019/6/3 15:15
 * @Description:
 */
public class CharWindController implements ControlledStage,Initializable {


    @FXML
    TextArea waitArea;

    @FXML
    VBox msgContiner;

    @FXML
    ListView friendList;

    public void sendMsg(ActionEvent event){
        MessageRequest req = new MessageRequest();
        req.setUserId(ChatSession.currentUser.getId());
        req.setName(ChatSession.currentUser.getUserName());
        req.setMsgType(MessageType.GROUP.getType());
        req.setMsg(waitArea.getText());
        waitArea.clear();
        SocketClient.channel.writeAndFlush(req);
    }


    public void closeDialog(ActionEvent event){
//        SocketClient.channel.close();
        this.getMyStage().close();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectToServer();
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiBaseService.INSTANCE.getStageController();
        return stageController.getStageBy("index");
    }

    private void connectToServer(){
        new Thread(() -> {
            try {
                new SocketClient().start("127.0.0.1" , 9999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}