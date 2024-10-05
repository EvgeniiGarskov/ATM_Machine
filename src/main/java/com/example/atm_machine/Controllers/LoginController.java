package com.example.atm_machine.Controllers;

import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
import com.example.atm_machine.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private Button goToRegisterButton;
    @FXML
    private Label errorLabel;

    private DatabaseHandler databaseHandler;
    private Helper helper;
    private UserSession userSession;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        databaseHandler = DatabaseHandler.getDatabaseHandler();
        helper = Helper.getHelper();
        userSession = UserSession.getUserSession();

        Helper.addTextLimiter(loginField, 4);
        Helper.addTextLimiter(passwordField, 4);
    }

    @FXML
    public void userAuthentication(ActionEvent event) {

        String loginText = loginField.getText().trim();
        String passwordText = passwordField.getText().trim();

        if (loginText.isEmpty()) {
            loginUpdateLabel("Введите логин");
            return;
        }

        if (passwordText.isEmpty()) {
            loginUpdateLabel("Введите пароль");
            return;
        }

        if (loginText.length() != 4 || passwordText.length() != 4) {
            loginUpdateLabel("Логин и пароль должны состоять из 4 цифр");
            loginClearFieldsAndMessage();
            return;
        }

        userSession.setCurrentUser(databaseHandler.getAuthUser(loginText, passwordText));

        if (userSession.getCurrentUser() == null) {
            loginUpdateLabel("Не верный логин или пароль");
            loginClearFieldsAndMessage();
            return;
        }
        helper.openNewScene("/Fxml/Menu.fxml", event);
    }

    private void loginUpdateLabel(String message) {
        
        errorLabel.setText(message);
        errorLabel.setTextFill(Color.RED);
    }

    private void loginClearFieldsAndMessage() {

        loginField.setText("");
        passwordField.setText("");
    }

    @FXML
    public void openRegisterScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Register.fxml", event);
    }

    public TextField getLoginField() {
        return loginField;
    }

    public void setLoginField(TextField loginField) {
        this.loginField = loginField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(PasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public Label getErrorLoginLabel() {
        return errorLabel;
    }

    public void setErrorLoginLabel(Label errorLoginLabel) {
        this.errorLabel = errorLoginLabel;
    }

    public void setDatabaseHandler(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }
}