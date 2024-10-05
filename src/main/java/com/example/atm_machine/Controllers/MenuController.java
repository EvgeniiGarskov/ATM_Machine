package com.example.atm_machine.Controllers;

import com.example.atm_machine.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Label menuNameLabel;
    @FXML
    private Label menuCardNumberLabel;
    @FXML
    private Label menuBalanceLabel;
    @FXML
    private Label menuDateLabel;
    @FXML
    private Button fastWithdrawalButton500;
    @FXML
    private Button fastWithdrawalButton2000;
    @FXML
    private Button fastWithdrawalButton1000;
    @FXML
    private Button menuGoToTransactionButton;
    @FXML
    private Button menuGoToTransferButton;
    @FXML
    private Button menuGotoDepositButton;
    @FXML
    private Button menuGoToWithdrawalButton;
    @FXML
    private Button menuLogoutButton;

    private Helper helper = Helper.getHelper();
    private UserSession userSession;
    private WithdrawalController withdrawalController = new WithdrawalController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        userSession = UserSession.getUserSession();
        showAccountInfo();
    }

    public void showAccountInfo() {

        menuNameLabel.setText("Здравствуйте, " + userSession.getCurrentUser().getName() + "!");
        menuBalanceLabel.setText(userSession.getCurrentUser().getBalance() + " ₽");
        menuCardNumberLabel.setText("**** **** **** " + userSession.getCurrentUser().getLogin());
        menuDateLabel.setText(helper.showDateTime("dd.MM.yyyy"));
    }

    @FXML
    public void openDepositScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Deposit.fxml", event);
    }

    @FXML
    public void openWithdrawalScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Withdrawal.fxml", event);
    }

    @FXML
    public void fastWithdrawal(ActionEvent event) {

        try {
            int fastWithdrawal;
            try {
                fastWithdrawal = Integer.parseInt(((Button) event.getSource()).getText());
            } catch (NumberFormatException e) {
                Application.logger.error("Произошла ошибка. Неправильное значение: " + e.getMessage());
                return;
            }

            if (fastWithdrawal < 0) {
                throw new IllegalArgumentException("Сумма снятия не может быть отрицательной");
            }

            if (userSession.getCurrentUser() == null) {
                Application.logger.error("Ошибка: не удалось получить информацию о пользователе");
                return;
            }

            int fastNewBalance = userSession.getCurrentUser().getBalance() - fastWithdrawal;

            withdrawalController.fastWithdrawal(fastWithdrawal, fastNewBalance, event);

        } catch (IllegalArgumentException e) {
            Application.logger.error("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    public void openTransferScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Transfer.fxml", event);
    }

    @FXML
    public void openLogoutScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Login.fxml", event);
    }

    @FXML
    public void openTransactionScene(ActionEvent event) {

        helper.openNewScene("/Fxml/TransactionsListView.fxml", event);
    }

    public Label getMenuNameLabel() {
        return menuNameLabel;
    }

    public void setMenuNameLabel(Label menuNameLabel) {
        this.menuNameLabel = menuNameLabel;
    }

    public Label getMenuCardNumberLabel() {
        return menuCardNumberLabel;
    }

    public void setMenuCardNumberLabel(Label menuCardNumberLabel) {
        this.menuCardNumberLabel = menuCardNumberLabel;
    }

    public Label getMenuBalanceLabel() {
        return menuBalanceLabel;
    }

    public void setMenuBalanceLabel(Label menuBalanceLabel) {
        this.menuBalanceLabel = menuBalanceLabel;
    }

    public Label getMenuDateLabel() {
        return menuDateLabel;
    }

    public void setMenuDateLabel(Label menuDateLabel) {
        this.menuDateLabel = menuDateLabel;
    }

    public void setFastWithdrawalButton1000(Button fastWithdrawalButton1000) {
        this.fastWithdrawalButton1000 = fastWithdrawalButton1000;
    }

    public void setWithdrawalController(WithdrawalController withdrawalController) {
        this.withdrawalController = withdrawalController;
    }
}