package com.example.atm_machine.Controllers;

import com.example.atm_machine.Models.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class TransactionCellController extends ListCell<Transaction> {

    @FXML
    public Label cellNameLabel;
    @FXML
    public Label cellOperationLabel;
    @FXML
    public Label cellAmountLabel;
    @FXML
    public Label cellDateLabel;
    @FXML
    public ImageView transactionCellImage;
    @FXML
    public AnchorPane anchorPane;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Transaction transaction, boolean empty) {
        super.updateItem(transaction, empty);

        if (empty || transaction == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/Fxml/TransactionCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            cellNameLabel.setText(transaction.getName());
            cellOperationLabel.setText(transaction.getOperation());
            cellAmountLabel.setText(transaction.getAmount() != 0 ? transaction.getAmount() + " ₽" : "N/A");
            cellDateLabel.setText(transaction.getDate() != null ? transaction.getDate() : "N/A");

            Image transactionImage1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Styles/211624_c_up_arrow_icon.png")));
            Image transactionImage2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Styles/211615_down_c_arrow_icon.png")));

            switch (transaction.getOperation()) {
                case Transaction.CONST_DEPOSIT, Transaction.CONST_INCOMING_TRANSFER -> transactionCellImage.setImage(transactionImage2);
                case Transaction.CONST_WITHDRAWAL, Transaction.CONST_TRANSFER -> transactionCellImage.setImage(transactionImage1);
            }

            setText(null);
            setGraphic(anchorPane);
        }
    }
}
