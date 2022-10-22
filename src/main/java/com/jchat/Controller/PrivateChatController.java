package com.jchat.Controller;

import com.Users.Message;


import com.Users.MessageType;
import com.jchat.demo.Listener;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrivateChatController  implements  Initializable{
    @FXML
    private TextArea textshow;
    @FXML
    private TextArea messagetext;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private AnchorPane main;
    @FXML
    private Button sendButton;
    @FXML
    private ListView messageList;
    @FXML
    private Circle status;
    @FXML
    private Label usernamelabel;
    private double xOffset;
    private double yOffset;
    Logger logger = LoggerFactory.getLogger(PrivateChatController.class);
    public MessageType messageType;

    public void sendMessage() throws IOException {
        String msg=messagetext.getText();
        if(!msg.isEmpty()){
            if(messageType==MessageType.PRIVATECHAT)
                PrivateListener.send(msg);
            if(messageType==MessageType.GROUPCHAT)
                Listener.send(msg);
            this.messagetext.clear();
        }
    }

    public synchronized void addToChat(Message msg) {
        System.out.println("addToChat: "+msg);

        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            protected HBox call() throws Exception {
                return null;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            messageList.getItems().add(msg.getName()+":"+msg.getMsg());
        });

        Task<HBox> yourMessages = new Task<HBox>() {

            @Override
            protected HBox call() throws Exception {
                return null;
            }
        };
        yourMessages.setOnSucceeded(event -> messageList.getItems().add(usernamelabel.getText()+":"+msg.getMsg()));

        if (msg.getName().equals(usernamelabel.getText())) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }
    }

    public void setUsernameLabel(String username) {
        this.usernamelabel.setText(username);
    }
    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }


    /* Method to display server messages */
    public synchronized void addAsServer(Message msg) {
        Task<HBox> task = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            messageList.getItems().add(String.valueOf(task.getValue()));
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        main.setOnMousePressed(event -> {
            xOffset = LoginLaunch.getPrimaryStage().getX() - event.getScreenX();
            yOffset = LoginLaunch.getPrimaryStage().getY() - event.getScreenY();
            main.setCursor(Cursor.CLOSED_HAND);
        });

        main.setOnMouseDragged(event -> {
            LoginLaunch.getPrimaryStage().setX(event.getScreenX() + xOffset);
            LoginLaunch.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });

        main.setOnMouseReleased(event -> {
            main.setCursor(Cursor.DEFAULT);
        });

        messagetext.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    sendMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ke.consume();
            }
        });
    }
}
