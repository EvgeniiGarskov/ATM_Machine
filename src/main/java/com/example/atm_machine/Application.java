package com.example.atm_machine;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/Fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
        stage.setTitle("ATM");
        stage.setScene(scene);
        stage.show();
    }

    public static final Logger logger = (Logger) LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Старт приложения");
        launch();
        logger.info("Приложение завершено успешно");
    }
}