package com.example.atm_machine;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Helper {

    private static Helper helper;

    private Helper() {
    }

    public static Helper getHelper() {

        if (helper == null) {
            helper = new Helper();
        }
        return helper;
    }

    public void openNewScene(String window, ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(window));

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Application.logger.error("Ошибка при открытии новой сцены: " + e.getMessage());
        }
    }

    public static void addTextLimiter(final TextField textField, final int maxLength) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
                return;
            }

            //Разрешить ввод только цифр
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }

    public static void addTextCapitalLetter(final TextField textField, final int maxLength) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(newValue.substring(0, maxLength));
                return;
            }

            if (!newValue.isEmpty()) {
                // Меняем первую букву на заглавную
                String capitalized = newValue.substring(0, 1).toUpperCase() + newValue.substring(1);
                textField.setText(capitalized);

                // Разрешить ввод только русских букв
                if (!capitalized.matches("\\sа-яА-Я*")) {
                    textField.setText(capitalized.replaceAll("[^\\sа-яА-Я]", ""));
                }
            }
        });
    }

    public String showDateTime (String dateFormat) {

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);

        return dateTimeFormatter.format(today);
    }
}
