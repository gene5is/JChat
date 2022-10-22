package com.jchat.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginLaunch extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {

        primaryStage=stage;
        System.out.println(getClass().getResource("LoginView.fxml"));
        Parent root=FXMLLoader.load(getClass().getResource("LoginView.fxml"));
        primaryStage.initStyle(StageStyle.DECORATED);
        Scene scene = new Scene(root, 358, 441);
        scene.setRoot(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e->Platform.exit());
    }

    public static Stage getPrimaryStage(){
        return primaryStage;
    }
}
