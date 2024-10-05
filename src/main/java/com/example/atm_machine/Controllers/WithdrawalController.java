package com.example.atm_machine.Controllers;

import com.example.atm_machine.*;
import com.example.atm_machine.Models.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class WithdrawalController implements Initializable {

    @FXML
    private TextField withdrawalTextField;
    @FXML
    private Label withdrawalLabel;
    @FXML
    private Button withdrawalGoToMenuButton;
    @FXML
    private Button withdrawalButton;
    @FXML
    private Button withdrawalResetButton;
    @FXML
    private Button withdrawalAppendNumberButton;
    @FXML
    private Button withdrawalDeleteButton;

    private DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandler();
    private Helper helper = Helper.getHelper();
    private UserSession userSession = UserSession.getUserSession();
    private AlertFactory alertFactory = new AlertFactoryImpl();

    private static final String ATM_SOURCE = "Банкомат";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Helper.addTextLimiter(withdrawalTextField, 7);
    }

    @FXML
    public void withdrawal(ActionEvent event) {

        String inputWithdrawalTextField = withdrawalTextField.getText().trim();

        if (inputWithdrawalTextField.isEmpty()) {
            withdrawalUpdateLabel("Введите сумму для снятия", Color.RED);
            return;
        }

        int withdrawal;
        try {
            withdrawal = Integer.parseInt(inputWithdrawalTextField);
        } catch (NumberFormatException e) {
            withdrawalUpdateLabel("Неверный ввод. Введите число", Color.RED);
            Application.logger.error("Ошибка формата ввода для внесения: " + e.getMessage());
            return;
        }

        if (withdrawal < 0) {
            withdrawalUpdateLabel("Сумма не может быть отрицательной", Color.RED);
            return;
        }

        if (userSession.getCurrentUser() == null) {
            withdrawalUpdateLabel("Не удалось получить информацию о пользователе", Color.RED);
            Application.logger.error("Ошибка: не удалось получить информацию о пользователе");
            return;
        }

        if (withdrawal > userSession.getCurrentUser().getBalance()) {
            withdrawalUpdateLabel("Такую сумму нельзя снять", Color.RED);
            return;
        }
        if (withdrawal % 100 != 0) {
            withdrawalUpdateLabel("Сумма снятия не кратна 100 ₽", Color.RED);
            return;
        }

        int newBalance = userSession.getCurrentUser().getBalance() - withdrawal;
        processWithdrawal(newBalance, withdrawal);

        withdrawalUpdateLabel("Успешно!", Color.GREEN);
    }

    public void fastWithdrawal(int fastWithdrawal, int fastNewBalance, ActionEvent event) {

        if (fastWithdrawal > userSession.getCurrentUser().getBalance()) {
            alertFactory.createInformationAlert(Alert.AlertType.ERROR, "Снятие наличных", "Такую сумму нельзя снять!", null);
            return;
        }

        processWithdrawal(fastNewBalance, fastWithdrawal);

        alertFactory.createInformationAlert(Alert.AlertType.INFORMATION, "Снятие наличных", "Операция прошла успешно, \nСпасибо!", "/Styles/correct.png");

        helper.openNewScene("/Fxml/Menu.fxml", event);
    }

    private void withdrawalUpdateLabel(String message, Color color) {
        withdrawalLabel.setText(message);
        withdrawalLabel.setTextFill(color);
        withdrawalTextField.setText("");
    }

    private void processWithdrawal(int newBalance, int withdrawal) {

        databaseHandler.updateBalance(newBalance, userSession.getCurrentUser().getId(), DatabaseHandler.UPDATE_BALANCE);
        databaseHandler.addTransaction(userSession.getCurrentUser().getId(), ATM_SOURCE, Transaction.CONST_WITHDRAWAL, withdrawal);
        userSession.getCurrentUser().setBalance(newBalance);
    }

    @FXML
    public void openMenuScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Menu.fxml", event);
    }

    @FXML
    public void appendNumberToWithdrawal(ActionEvent event) {

        Button button = (Button) event.getSource();
        withdrawalTextField.appendText(button.getText());
    }

    @FXML
    public void resetWithdrawalFields(ActionEvent event) {

        withdrawalTextField.setText("");
        withdrawalLabel.setText("");
    }

    @FXML
    public void deleteLastDigitFromWithdrawal(ActionEvent event) {

        String withdrawalDelete = withdrawalTextField.getText().trim();
        if (!withdrawalDelete.isEmpty()) {
            withdrawalTextField.setText(withdrawalDelete.substring(0, withdrawalDelete.length() - 1));
        }
    }

    public TextField getWithdrawalTextField() {
        return withdrawalTextField;
    }

    public void setWithdrawalTextField(TextField withdrawalTextField) {
        this.withdrawalTextField = withdrawalTextField;
    }

    public Label getWithdrawalLabel() {
        return withdrawalLabel;
    }

    public void setWithdrawalLabel(Label withdrawalLabel) {
        this.withdrawalLabel = withdrawalLabel;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    public void setAlertFactory(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }
}
