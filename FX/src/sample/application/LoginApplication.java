package sample.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.controller.LoginController;
import sample.util.StageController;
import sample.util.UiBaseService;

import java.util.Map;

public class LoginApplication extends Application {


    Stage loginStage = new Stage();

    @Override
    public void start(Stage primaryStage) throws Exception{

        initComponet();

        loginStage.setTitle("用户登录");
        FXMLLoader loader =  new FXMLLoader(getClass().getClassLoader().getResource("sample/fxml/login.fxml"));
        Pane page = (Pane) loader.load();
        Scene scene = new Scene(page, 500, 450);
        loginStage.setScene(scene);
        loginStage.sizeToScene();

        LoginController loginController =  loader.getController();
        loginController.setApp(this);


        loginStage.show();
    }

    //初始化组件
    public void initComponet(){
        StageController stageController = UiBaseService.INSTANCE.getStageController();
        Map<String, Object> componetMap = UiBaseService.INSTANCE.getComponetMap();
        componetMap.put("chatItemLeftPane",stageController.load("sample/fxml/chatItemLeft.fxml" , Pane.class));
        componetMap.put("chatItemRightPane",stageController.load("sample/fxml/chatItemRight.fxml" , Pane.class));
    }

    public void hideStage(){
        loginStage.hide();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
