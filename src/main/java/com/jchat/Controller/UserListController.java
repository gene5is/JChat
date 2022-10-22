package com.jchat.Controller;

import com.Users.Message;
import com.Users.MessageType;
import com.Users.User;
import com.jchat.demo.Listener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;


public class UserListController {

    @FXML
    private ListView userList;

    @FXML
    private HBox hbox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label onlineCountLabel;

    @FXML
    private Label label;

    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label myusername;
    Logger logger = LoggerFactory.getLogger(PrivateChatController.class);

    private PrivateChatController privateChatController;

    public void setUserList(Message msg) {
        logger.info("setUserList() method Enter");
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(msg.getUsers());
            System.out.println(users);
            userList.setItems(users);
            userList.getSelectionModel().selectedItemProperty().addListener(new NewChatWindow());
            setOnlineLabel(String.valueOf(msg.getUserList().size()));

        });
        logger.info("setUserList() method Exit");
    }
    public void setMyUsername(String myUsername){
        this.myusername.setText(myUsername);
    }
    public void setOnlineLabel(String usercount) {
        System.out.println(onlineCountLabel);
        System.out.println("{ usercount: "+usercount+"}");
        Platform.runLater(() -> onlineCountLabel.setText(usercount));
    }


    private class NewChatWindow implements ChangeListener {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            System.out.println("打开一个新窗口");
            System.out.println("destUser: "+(User)t1);
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("PrivateChatView.fxml"));

            try {
                Parent window=(AnchorPane)fxmlLoader.load();
                privateChatController=fxmlLoader.getController();
                privateChatController.messageType=MessageType.PRIVATECHAT;
                privateChatController.setUsernameLabel(myusername.getText());
                Scene scene=new Scene(window);
                Stage stage=new Stage();
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,"无法私聊");
                e.printStackTrace();
            }

            PrivateListener listener = new PrivateListener("localhost", 1234, myusername.getText(),((User)t1).getUserName(),privateChatController);
            Thread x=new Thread(listener);
            x.start();
        }
    }
}