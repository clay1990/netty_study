package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import sample.application.LoginApplication;
import com.clay.model.User;
import sample.util.ChatSession;
import sample.util.StageController;
import sample.util.UiBaseService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private LoginApplication loginApplication;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login;
    @FXML
    private Label unametip;
    @FXML
    private Label pwdtip;

    private  static Map<String, User> userMap = new HashMap<>();

    StageController stageController = UiBaseService.INSTANCE.getStageController();

    static {
        userMap.put("clay" , new User("1","clay","123"));
        userMap.put("lm" , new User("2","lm","123"));
        userMap.put("jack" , new User("3","jack","123"));
        userMap.put("tom" , new User("4","tom","123"));
        userMap.put("admin" , new User("5","admin","123"));
    }

    public void setApp(LoginApplication loginApplication){
        this.loginApplication = loginApplication;
    }

    public void onLogin(ActionEvent event){
        System.out.println("login");
        String uname = username.getText();
        String pwd = password.getText();
        if(null == uname || "".equals(uname.trim())){
            unametip.setText("请输入用户名");
            return;
        }else{
            unametip.setText("");
        }
        if(null == pwd || "".equals(pwd.trim())){
            pwdtip.setText("请输入密码");
            return;
        }else{
            pwdtip.setText("");

        }
        System.out.println("uname = " +uname + ",pwd = " + pwd);

        User user = userMap.get(uname);
        if(user == null){
            return;
        }

        if(!pwd.equals(user.getPassword())){
            pwdtip.setText("密码错误");
            return;
        }

        ChatSession.currentUser = user;
        loginApplication.hideStage();

        StageController stageController = UiBaseService.INSTANCE.getStageController();
        stageController.loadStage("index", "sample/fxml/index.fxml", "群聊("+ ChatSession.currentUser.getUserName() +")", StageStyle.DECORATED);
//        stageController.loadStage("private", "sample/fxml/chatPrivate.fxml", "", StageStyle.DECORATED);
        stageController.setStage("index");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
