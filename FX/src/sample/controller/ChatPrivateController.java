package sample.controller;

import com.clay.model.MessageRequest;
import com.clay.model.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
 * @Date: 2019/6/4 18:01
 * @Description:
 */
public class ChatPrivateController implements ControlledStage, Initializable {

    @FXML
    TextArea waitArea;

    @FXML
    VBox msgContiner;

    @FXML
    Label chatId;

    public void sendMsg(ActionEvent event){
        MessageRequest req = new MessageRequest();
        req.setUserId(ChatSession.currentUser.getId());
        req.setName(ChatSession.currentUser.getUserName());
        req.setMsgType(MessageType.PRIVATE.getType());
        //向谁发信息
        req.setSendTo(chatId.getText());
        System.out.println("sendTo = " + chatId.getText());
        req.setMsg(waitArea.getText());
        waitArea.clear();
        SocketClient.channel.writeAndFlush(req);
    }


    public void closeDialog(ActionEvent event){
        this.getMyStage().close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiBaseService.INSTANCE.getStageController();
        return stageController.getStageBy("private" + chatId.getText());
    }

}