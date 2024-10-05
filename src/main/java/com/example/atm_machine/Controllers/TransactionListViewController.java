package com.example.atm_machine.Controllers;

import com.example.atm_machine.*;
import com.example.atm_machine.Models.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TransactionListViewController implements Initializable {

    @FXML
    private Button transactionGoToMenuButton;
    @FXML
    private ListView<Transaction> transactionListView;

    private ObservableList<Transaction> transactionObservableList = FXCollections.observableArrayList();

    private UserSession userSession;
    private DatabaseHandler databaseHandler;
    private Helper helper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        databaseHandler = DatabaseHandler.getDatabaseHandler();
        helper = Helper.getHelper();
        userSession = UserSession.getUserSession();

        transactionListView.setItems(transactionObservableList);
        transactionListView.setCellFactory(transactionListView -> new TransactionCellController());

        addTransactionsListView();
    }

    public void addTransactionsListView() {

        String select = "SELECT * FROM transactions WHERE user_id = ?";

        try (Connection conn = databaseHandler.connectDB();
             PreparedStatement prSt = conn.prepareStatement(select)) {
            prSt.setInt(1, userSession.getCurrentUser().getId());
            try (ResultSet transactions = prSt.executeQuery()) {
                while (transactions.next()) {
                    Transaction transaction = new Transaction(
                            transactions.getString("name"),
                            transactions.getString("operation"),
                            transactions.getString("date"),
                            transactions.getInt("amount"));

                    transactionObservableList.add(0, transaction);
                }
            } catch (SQLException e) {
                Application.logger.error("An error occurred while processing transactions from the database: " + e.getMessage());
            }
        } catch (SQLException e) {
            Application.logger.error("Failed to get transactions for login: " + userSession.getCurrentUser().getLogin() + e.getMessage());
        }
    }

    @FXML
    public void openMenuScene(ActionEvent event) {

        helper.openNewScene("/Fxml/Menu.fxml", event);
    }

    public ObservableList<Transaction> getTransactionObservableList() {
        return transactionObservableList;
    }

    public void setTransactionObservableList(ObservableList<Transaction> transactionObservableList) {
        this.transactionObservableList = transactionObservableList;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
}