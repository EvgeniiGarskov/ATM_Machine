package com.example.atm_machine.Controllers;

import com.example.atm_machine.*;
import com.example.atm_machine.Models.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositController implements Initializable {

    @FXML
    private Button depositGoToMenuButton;
    @FXML
    private Button depositButton;
    @FXML
    private Button depositResetButton;
    @FXML
    private Button depositAppendNumberButton;
    @FXML
    private Button depositDeleteButton;
    @FXML
    private Label depositLabel;
    @FXML
    private TextField depositTextField;

    private DatabaseHandler databaseHandler;
    private UserSession userSession;
    private Helper helper;

    private static final int MIN_DEPOSIT_AMOUNT = 100;
    private static final String ATM_SOURCE = "Банкомат";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        databaseHandler = DatabaseHandler.getDatabaseHandler();
        helper = Helper.getHelper();
        userSession = UserSession.getUserSession();
        Helper.addTextLimiter(depositTextField, 7);
    }

    @FXML
    public void deposit(ActionEvent event) {

        String inputDepositTextField = depositTextField.getText().trim();

        if (inputDepositTextField.isEmpty()) {
            depositUpdateLabel("Введите сумму для внесения", Color.RED);
            return;
        }

        int deposit;
        try {
            deposit = Integer.parseInt(inputDepositTextField);
        } catch (NumberFormatException e) {
            depositUpdateLabel("Некорректный ввод суммы", Color.RED);
            Application.logger.error("Ошибка формата ввода для депозита: " + e.getMessage());
            return;
        }

        if (deposit < 0) {
            depositUpdateLabel("Сумма не может быть отрицательной", Color.RED);
            return;
        }

        if (deposit < MIN_DEPOSIT_AMOUNT) {
            depositUpdateLabel("Сумма внесения меньше 100 ₽", Color.RED);
            return;
        }

        if (userSession.getCurrentUser() == null) {
            depositUpdateLabel("Не удалось получить информацию о пользователе", Color.RED);
            Application.logger.error("Ошибка: не удалось получить информацию о пользователе");
            return;
        }

        int newBalance = deposit + userSession.getCurrentUser().getBalance();
        processDeposit(newBalance, deposit);

        depositUpdateLabel("Успешно!", Color.GREEN);
    }

    private void depositUpdateLabel(String message, Color color) {
        depositLabel.setText(message);
        depositLabel.setTextFill(color);
        depositTextField.setText("");
    }

    private void processDeposit(int newBalance, int deposit) {

        databaseHandler.updateBalance(newBalance, userSession.getCurrentUser().getId(), DatabaseHandler.UPDATE_BALANCE);
        databaseHandler.addTransaction(userSession.getCurrentUser().getId(), ATM_SOURCE, Transaction.CONST_DEPOSIT, deposit);
        userSession.getCurrentUser().setBalance(newBalance);
    }

    @FXML
    public void openMenuScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Menu.fxml", event);
    }

    @FXML
    public void appendNumberToDeposit(ActionEvent event) {

        Button button = (Button) event.getSource();
        depositTextField.appendText(button.getText());
    }

    @FXML
    public void resetDepositFields(ActionEvent event) {

        depositTextField.setText("");
        depositLabel.setText("");
    }

    @FXML
    public void deleteLastDigitFromDeposit(ActionEvent event) {

        String depositDelete = depositTextField.getText().trim();
        if (!depositDelete.isEmpty()) {
            depositTextField.setText(depositDelete.substring(0, depositDelete.length() - 1));
        }
    }

    public TextField getDepositTextField() {
        return depositTextField;
    }

    public void setDepositTextField(TextField depositTextField) {
        this.depositTextField = depositTextField;
    }

    public Label getDepositLabel() {
        return depositLabel;
    }

    public void setDepositLabel(Label depositLabel) {
        this.depositLabel = depositLabel;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
}
