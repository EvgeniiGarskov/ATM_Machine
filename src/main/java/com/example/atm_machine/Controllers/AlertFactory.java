package com.example.atm_machine.Controllers;

import javafx.scene.control.Alert;

public interface AlertFactory {

    void createInformationAlert(Alert.AlertType alertType, String title, String contentText, String imagePath);
}