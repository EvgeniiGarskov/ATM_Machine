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

public class TransferController implements Initializable {

    @FXML
    private TextField transferLoginTextField;
    @FXML
    private TextField transferTextField;
    @FXML
    private Label transferLabel;
    @FXML
    private Label transferRecipientLabel;
    @FXML
    private Button transferButton;
    @FXML
    private Button transferResetButton;
    @FXML
    private Button transferSearchButton;
    @FXML
    private Button transferGoToMenuButton;

    private DatabaseHandler databaseHandler;
    private UserSession userSession;
    private Helper helper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        databaseHandler = DatabaseHandler.getDatabaseHandler();
        helper = Helper.getHelper();
        userSession = UserSession.getUserSession();
        Helper.addTextLimiter(transferLoginTextField, 4);
        Helper.addTextLimiter(transferTextField, 7);
    }

    @FXML
    public void transfer(ActionEvent event) {

        String inputTransferLoginTextField = transferLoginTextField.getText().trim();

        if (inputTransferLoginTextField.isEmpty()) {
            transferUpdateLabel("Введите логин", Color.RED);
            return;
        }

        String inputTransferTextField = transferTextField.getText().trim();

        if (inputTransferTextField.isEmpty()) {
            transferUpdateLabel("Введите сумму для перевода", Color.RED);
            return;
        }

        String transferNameAndSurname = databaseHandler.getNameAndSurname(inputTransferLoginTextField);

        if (processFindUserToTransferAmount(inputTransferLoginTextField, transferNameAndSurname)) {

            int transfer;
            try {
                transfer = Integer.parseInt(inputTransferTextField);
            } catch (NumberFormatException e) {
                transferUpdateLabel("Некорректный ввод суммы", Color.RED);
                transferTextField.setText("");
                Application.logger.error("Ошибка формата ввода для перевода: " + e.getMessage());
                return;
            }


            if (transfer < 0) {
                transferUpdateLabel("Сумма не может быть отрицательной", Color.RED);
                transferTextField.setText("");
                return;
            }

            if (userSession.getCurrentUser() == null) {
                transferUpdateLabel("Не удалось получить информацию о пользователе", Color.RED);
                Application.logger.error("Ошибка: не удалось получить информацию о пользователе");
                return;
            }

            if (transfer > userSession.getCurrentUser().getBalance()) {
                transferUpdateLabel("Такую сумму нельзя перевести", Color.RED);
                transferTextField.setText("");
                return;
            }

            int transferId = databaseHandler.getRecipientId(inputTransferLoginTextField);
            int newBalance = userSession.getCurrentUser().getBalance() - transfer;

            processTransfer(newBalance, transfer, transferId, transferNameAndSurname);

            transferUpdateLabel("Перевод выполнен успешно!", Color.GREEN);

            transferClearLabel();
        }
    }

    @FXML
    public void findUserToTransferAmount(ActionEvent event) {

        String inputTransferLoginTextField = transferLoginTextField.getText().trim();

        if (inputTransferLoginTextField.isEmpty()) {
            transferUpdateLabel("Введите логин", Color.RED);
            return;
        }

        String transferNameAndSurname = databaseHandler.getNameAndSurname(inputTransferLoginTextField);

        if (processFindUserToTransferAmount(inputTransferLoginTextField, transferNameAndSurname)) {

            transferRecipientLabel.setText("Получатель:\n" + databaseHandler.getNameAndSurname(inputTransferLoginTextField));
        }
    }

    private void transferUpdateLabel(String message, Color color) {
        transferLabel.setText(message);
        transferLabel.setTextFill(color);
    }

    private boolean processFindUserToTransferAmount(String inputTransferLoginTextField, String transferNameAndSurname) {

        if (inputTransferLoginTextField.length() != 4) {
            transferUpdateLabel("Логин должен состоять из 4 цифр", Color.RED);
            transferLoginTextField.setText("");
            return false;
        }

        if (transferNameAndSurname == null) {
            transferUpdateLabel("Пользователь не найден", Color.RED);
            transferRecipientLabel.setText("");
            transferLoginTextField.setText("");

            return false;
        }
        return true;
    }

    private void processTransfer(int newBalance, int transfer, int transferId, String transferNameAndSurname) {

        databaseHandler.updateBalance(newBalance, userSession.getCurrentUser().getId(), DatabaseHandler.UPDATE_BALANCE);
        databaseHandler.updateBalance(transfer, transferId, DatabaseHandler.TRANSFER_UPDATE_BALANCE);
        userSession.getCurrentUser().setBalance(newBalance);
        databaseHandler.addTransaction(userSession.getCurrentUser().getId(), transferNameAndSurname, Transaction.CONST_TRANSFER, transfer);
        databaseHandler.addTransaction(transferId, userSession.getCurrentUser().getName() + " " + userSession.getCurrentUser().getSurname(), Transaction.CONST_INCOMING_TRANSFER, transfer);
    }

    private void transferClearLabel() {

        transferLoginTextField.setText("");
        transferRecipientLabel.setText("");
        transferTextField.setText("");
    }

    @FXML
    public void openMenuScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Menu.fxml", event);
    }

    @FXML
    public void resetTransferFields(ActionEvent event) {

        transferLoginTextField.setText("");
        transferTextField.setText("");
        transferLabel.setText("");
    }

    public TextField getTransferLoginTextField() {
        return transferLoginTextField;
    }

    public void setTransferLoginTextField(TextField transferLoginTextField) {
        this.transferLoginTextField = transferLoginTextField;
    }

    public TextField getTransferTextField() {
        return transferTextField;
    }

    public void setTransferTextField(TextField transferTextField) {
        this.transferTextField = transferTextField;
    }

    public Label getTransferLabel() {
        return transferLabel;
    }

    public void setTransferLabel(Label transferLabel) {
        this.transferLabel = transferLabel;
    }

    public Label getTransferRecipientLabel() {
        return transferRecipientLabel;
    }

    public void setTransferRecipientLabel(Label transferRecipientLabel) {
        this.transferRecipientLabel = transferRecipientLabel;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
}
