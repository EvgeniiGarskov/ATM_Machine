package com.example.atm_machine.Controllers;

import com.example.atm_machine.Helper;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;

public class AlertFactoryImpl implements AlertFactory {

    @Override
    public void createInformationAlert(Alert.AlertType alertType, String title, String contentText, String imagePath) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        if (imagePath != null) {
            alert.setGraphic(new ImageView(Helper.class.getResource(imagePath).toString()));
        }
        alert.showAndWait();
    }
}