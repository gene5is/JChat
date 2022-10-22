package com.jchat.Controller;


import com.Users.User;
import com.Utils.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.swing.*;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Label RegisterField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField nicknameField;
    @FXML
    private PasswordField passwordConfirmField;

    @FXML
    void Register(ActionEvent event) {
        System.out.println(passwordConfirmField.getText());
        if(!passwordField.getText().equals(passwordConfirmField.getText())){
            JOptionPane.showMessageDialog(null,"两次输入密码不一致");
        }
        User user=new User(nicknameField.getText(),usernameField.getText(),passwordField.getText());
        System.out.println(user);
        Register r=new Register(user);
        if(r.Judge()){
            try {
                r.addUser();
            } catch (ClassNotFoundException ex) {
                r.setStatus(false);
                throw new RuntimeException(ex);
            }
        }
        else {
            r.setStatus(false);
        }
        if(r.getStatus()==true){
            JOptionPane.showMessageDialog(null,"注册成功");
        }
    }
}

