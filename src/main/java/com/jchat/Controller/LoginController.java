package com.jchat.Controller;


import com.Users.MessageType;
import com.Users.User;
import com.Utils.UserDao;
import com.jchat.demo.Listener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label pleaseLogin;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label passwordLabel;
    private PrivateChatController privateChatController;
    private UserListController userListController;

    private static LoginController instance;
    @FXML
    void Login(ActionEvent event) {
        User user=new User();
        user.setUserName(usernameField.getText());
        user.setPassword(passwordField.getText());
        UserDao dao=new UserDao();
        User user1=dao.login(user);

        if(user1==null){
            JOptionPane.showMessageDialog(null,"该用户不存在");
            passwordField.setText("");
        }
        else if(user.getUserName().equals(user1.getUserName())&&user.getPassword().equals(user1.getPassword())) {
            JOptionPane.showMessageDialog(null, "登录成功");
            try {

                FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("UserListView.fxml"));
                Parent window=(AnchorPane)fxmlLoader.load();
                userListController=fxmlLoader.<UserListController>getController();
                userListController.setMyUsername(user1.getUserName());
                FXMLLoader fxmlLoader1=new FXMLLoader(getClass().getResource("PrivateChatView.fxml"));
                Parent window1=(AnchorPane)fxmlLoader1.load();
                privateChatController=fxmlLoader1.<PrivateChatController>getController();
                privateChatController.setUsernameLabel(user1.getUserName());
                privateChatController.messageType=MessageType.GROUPCHAT;
                Listener listener = new Listener("localhost", 1234, user1.getUserName(),privateChatController ,userListController);
                Thread x = new Thread(listener);
                x.start();

                Scene scene=new Scene(window);
                Stage stage1=new Stage();
                Stage stage2=new Stage();
                stage1.setScene(scene);

                Scene scene1=new Scene(window1);
                stage2.setScene(scene1);
                stage1.show();
                stage2.show();

                LoginLaunch.getPrimaryStage().setIconified(true);
                LoginLaunch.getPrimaryStage().close();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    void Register(ActionEvent event) {
        try {
            //一定需要使用try-catch，不然编译器不会让你过的，Trust me!
            Parent anotherRoot = FXMLLoader.load(getClass().getResource("RegisterController.fxml"));
            System.out.println(getClass().getResource("RegisterController.fxml"));
            Stage anotherStage = new Stage();
            anotherStage.setTitle("Another Window Triggered by Clicking");
            anotherStage.setScene(new Scene(anotherRoot, 304, 427));
            anotherStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public LoginController() {
        instance = this;
    }
    public static LoginController getInstance() {
        return instance;
    }
}
