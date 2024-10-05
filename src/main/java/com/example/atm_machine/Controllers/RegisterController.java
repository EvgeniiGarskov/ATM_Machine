package com.example.atm_machine.Controllers;

import com.example.atm_machine.Application;
import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Label registerNameLabel;
    @FXML
    private TextField registerNameTextField;
    @FXML
    private Label registerSurnameLabel;
    @FXML
    private TextField registerSurnameTextField;
    @FXML
    private Label registerPasswordLabel;
    @FXML
    private TextField registerPasswordTextField;
    @FXML
    private Button registerAddUserButton;
    @FXML
    private Label registerErrorLabel;
    @FXML
    private Button registerGoToLoginButton;

    private DatabaseHandler databaseHandler;
    private Helper helper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        databaseHandler = DatabaseHandler.getDatabaseHandler();
        helper = Helper.getHelper();

        Helper.addTextLimiter(registerPasswordTextField, 4);
        Helper.addTextCapitalLetter(registerNameTextField, 30);
        Helper.addTextCapitalLetter(registerSurnameTextField, 30);
    }

    @FXML
    public void addUser(ActionEvent event) {

        String nameText = registerNameTextField.getText().trim();
        String surnameText = registerSurnameTextField.getText().trim();
        String passwordText = registerPasswordTextField.getText().trim();

        String newLogin;
        try {
            newLogin = Integer.toString(Integer.parseInt(databaseHandler.returnNewLogin()) + 1);
        } catch (NumberFormatException e) {
            registerUpdateLabel("Ошибка при получении логина. Попробуйте снова", Color.RED);
            return;
        }

        if (nameText.isEmpty() || surnameText.isEmpty() || passwordText.isEmpty()) {
            registerUpdateLabel("Заполните все поля!", Color.RED);
            return;
        }

        //Можно эти два условия разделить
        if (nameText.length() > 30 || surnameText.length() > 30) {
            registerUpdateLabel("Имя и фамилия должны содержать не более 30 символов!", Color.RED);
            registerNameTextField.setText("");
            registerSurnameTextField.setText("");
            return;
        }
        if (passwordText.length() != 4) {
            registerUpdateLabel("Пароль должен состоять из 4 цифр", Color.RED);
            registerPasswordTextField.setText("");
            return;
        }
        if (databaseHandler.getRecipientId(newLogin) != 0) {
            registerUpdateLabel("Произошла ошибка. Попробуйте снова", Color.RED);
            Application.logger.error("Такой логин уже используется");
            return;
        }
        databaseHandler.addUser(nameText, surnameText, newLogin, passwordText);
        registerUpdateLabel("Успешно!\nВаш логин: " + newLogin + "\nПароль: " + passwordText, Color.GREEN);

        registerNameTextField.setText("");
        registerSurnameTextField.setText("");
        registerPasswordTextField.setText("");
    }

    private void registerUpdateLabel(String message, Color color) {
        registerErrorLabel.setText(message);
        registerErrorLabel.setTextFill(color);
    }

    @FXML
    public void openLoginScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Login.fxml", event);
    }

    public TextField getRegisterNameTextField() {
        return registerNameTextField;
    }

    public void setRegisterNameTextField(TextField registerNameTextField) {
        this.registerNameTextField = registerNameTextField;
    }

    public TextField getRegisterSurnameTextField() {
        return registerSurnameTextField;
    }

    public void setRegisterSurnameTextField(TextField registerSurnameTextField) {
        this.registerSurnameTextField = registerSurnameTextField;
    }

    public TextField getRegisterPasswordTextField() {
        return registerPasswordTextField;
    }

    public void setRegisterPasswordTextField(TextField registerPasswordTextField) {
        this.registerPasswordTextField = registerPasswordTextField;
    }

    public Label getRegisterErrorLabel() {
        return registerErrorLabel;
    }

    public void setRegisterErrorLabel(Label registerErrorLabel) {
        this.registerErrorLabel = registerErrorLabel;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
}

