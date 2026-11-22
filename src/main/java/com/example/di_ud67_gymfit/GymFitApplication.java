package com.example.di_ud67_gymfit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GymFitApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GymFitApplication.class.getResource("gymfit-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("GymFit Manager — Alta de socio");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }
}
