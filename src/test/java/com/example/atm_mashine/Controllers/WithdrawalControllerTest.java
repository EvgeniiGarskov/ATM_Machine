package com.example.atm_mashine.Controllers;

import com.example.atm_machine.Controllers.AlertFactory;
import com.example.atm_machine.Controllers.WithdrawalController;
import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
import com.example.atm_machine.Models.User;
import com.example.atm_machine.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class WithdrawalControllerTest {

    @InjectMocks
    private static WithdrawalController withdrawalController;
    @Mock
    private DatabaseHandler mockDatabaseHandler;
    @Spy
    private UserSession userSession;
    private Helper mockHelper;
    private TextField withdrawalTextField;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        withdrawalTextField = new TextField();
        withdrawalController.setWithdrawalTextField(withdrawalTextField);
    }

    private void setupWithdrawalTest(String amount) {

        Label withdrawalLabel = new Label();
        withdrawalTextField.setText(amount);

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 5000));

        withdrawalController.setWithdrawalLabel(withdrawalLabel);
    }

    private void verifyWithdrawalResults(int expectedAmount, String expectedResult, Color expectedColor) {

        assertEquals(expectedAmount, userSession.getCurrentUser().getBalance());
        assertEquals(expectedResult, withdrawalController.getWithdrawalLabel().getText());
        assertEquals(expectedColor, withdrawalController.getWithdrawalLabel().getTextFill());
        assertEquals("", withdrawalController.getWithdrawalTextField().getText());
    }

    @Test
    public void withdrawalShouldUpdateBalanceAndAddTransaction() {

        setupWithdrawalTest("500");

        doNothing().when(mockDatabaseHandler).updateBalance(4500, 1, DatabaseHandler.UPDATE_BALANCE);
        doNothing().when(mockDatabaseHandler).addTransaction(1, "Банкомат", "Снятие наличных", 500);

        withdrawalController.withdrawal(mock(ActionEvent.class));

        verify(mockDatabaseHandler).updateBalance(eq(4500), eq(1), eq("UPDATE users SET balance = ? WHERE id = ?"));
        verify(mockDatabaseHandler).addTransaction(eq(1), eq("Банкомат"), eq("Снятие наличных"), eq(500));

        verifyWithdrawalResults(4500, "Успешно!", Color.GREEN);
    }

    @Test
    public void withdrawalShouldNotMakeWithdrawalIfTheWithdrawalTextFieldIsEmpty() {

        setupWithdrawalTest("");

        withdrawalController.withdrawal(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyWithdrawalResults(5000, "Введите сумму для снятия", Color.RED);
    }

    @Test
    public void withdrawalShouldNotThrowExceptionWhenWithdrawalIsInvalid() {

        setupWithdrawalTest("Число");

        assertDoesNotThrow(() -> withdrawalController.withdrawal(mock(ActionEvent.class)));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyWithdrawalResults(5000, "Неверный ввод. Введите число", Color.RED);
    }

    @Test
    public void withdrawalShouldNotUpdateTheBalanceWhenTheValueIsNegative() {

        setupWithdrawalTest("-100");

        withdrawalController.withdrawal(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyWithdrawalResults(5000, "Сумма не может быть отрицательной", Color.RED);
    }

    @Test
    public void withdrawalShouldNotProcessWithdrawalWhenUserIsNotLoggedIn() {

        setupWithdrawalTest("500");

        userSession.setCurrentUser(null);

        withdrawalController.withdrawal(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        assertEquals("Не удалось получить информацию о пользователе", withdrawalController.getWithdrawalLabel().getText());
        assertEquals(Color.RED, withdrawalController.getWithdrawalLabel().getTextFill());
        assertEquals("", withdrawalController.getWithdrawalTextField().getText());
    }

    @Test
    public void withdrawalShouldNotWithdrawalAmountMaximumBalanceUserAndUpdateBalance() {

        setupWithdrawalTest("5500");

        withdrawalController.withdrawal(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyWithdrawalResults(5000, "Такую сумму нельзя снять", Color.RED);
    }

    @Test
    public void withdrawalShouldNotWithdrawalInvalidAmount() {

        setupWithdrawalTest("50");

        withdrawalController.withdrawal(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyWithdrawalResults(5000, "Сумма снятия не кратна 100 ₽", Color.RED);
    }

    @Test
    public void fastWithdrawalShouldUpdateBalanceAndAddTransaction() {

        mockHelper = mock(Helper.class);
        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        doNothing().when(mockDatabaseHandler).updateBalance(4500, 1, DatabaseHandler.UPDATE_BALANCE);
        doNothing().when(mockDatabaseHandler).addTransaction(1, "Банкомат", "Снятие наличных", 500);

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 5000));

        AlertFactory mockAlertFactory = spy(AlertFactory.class);

        withdrawalController.setHelper(mockHelper);
        withdrawalController.setAlertFactory(mockAlertFactory);

        int fastWithdrawal = 500;
        int fastWithdrawalAfter = userSession.getCurrentUser().getBalance() - fastWithdrawal;

        withdrawalController.fastWithdrawal(fastWithdrawal, fastWithdrawalAfter, mock(ActionEvent.class));

        verify(mockDatabaseHandler).updateBalance(eq(4500), eq(1), eq("UPDATE users SET balance = ? WHERE id = ?"));
        verify(mockDatabaseHandler).addTransaction(eq(1), eq("Банкомат"), eq("Снятие наличных"), eq(500));

        assertEquals(4500, userSession.getCurrentUser().getBalance());

        verify(mockHelper).openNewScene(eq("/Fxml/Menu.fxml"), any(ActionEvent.class));
        verify(mockAlertFactory).createInformationAlert(Alert.AlertType.INFORMATION, "Снятие наличных", "Операция прошла успешно, \nСпасибо!", "/Styles/correct.png");
    }

    @Test
    public void fastWithdrawalShouldNotWithdrawalAmountMaximumBalanceUserAndUpdateBalance() {

        mockHelper = mock(Helper.class);

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 300));

        AlertFactory mockAlertFactory = spy(AlertFactory.class);

        withdrawalController.setHelper(mockHelper);
        withdrawalController.setAlertFactory(mockAlertFactory);

        int fastWithdrawal = 500;
        int fastWithdrawalAfter = userSession.getCurrentUser().getBalance() - fastWithdrawal;

        withdrawalController.fastWithdrawal(fastWithdrawal, fastWithdrawalAfter, mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        assertEquals(300, userSession.getCurrentUser().getBalance());

        verify(mockHelper, never()).openNewScene(anyString(), any(ActionEvent.class));
        verify(mockAlertFactory).createInformationAlert(Alert.AlertType.ERROR, "Снятие наличных", "Такую сумму нельзя снять!", null);

    }

    @Test
    public void openMenuSceneShouldNavigateToMenuPage() {

        mockHelper = mock(Helper.class);
        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        userSession.setCurrentUser(mock(User.class));

        withdrawalController.setHelper(mockHelper);

        withdrawalController.openMenuScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Menu.fxml"), any(ActionEvent.class));
    }

    @Test
    public void appendNumberToWithdrawalShouldConcatenateButtonNumberToDepositTextField() {

        Button button = new Button("5");

        withdrawalTextField.setText("7");

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getSource()).thenReturn(button);

        withdrawalController.appendNumberToWithdrawal(mockEvent);

        assertEquals("75", withdrawalController.getWithdrawalTextField().getText());
    }

    @Test
    public void resetWithdrawalFieldsShouldResetWithdrawalFieldsOnButtonClick() {

        withdrawalTextField.setText("test");
        Label withdrawalLabel = new Label("test");

        withdrawalController.setWithdrawalLabel(withdrawalLabel);

        withdrawalController.resetWithdrawalFields(mock(ActionEvent.class));

        assertEquals("", withdrawalController.getWithdrawalTextField().getText());
        assertEquals("", withdrawalController.getWithdrawalLabel().getText());
    }

    @Test
    public void deleteLastDigitFromWithdrawalShouldDeleteLastCharacterFromWithdrawalTextFieldOnButtonClick() {

        withdrawalTextField.setText("Withdrawal");

        withdrawalController.deleteLastDigitFromWithdrawal(mock(ActionEvent.class));

        assertEquals("Withdrawa", withdrawalController.getWithdrawalTextField().getText());

        withdrawalController.deleteLastDigitFromWithdrawal(mock(ActionEvent.class));

        assertEquals("Withdraw", withdrawalController.getWithdrawalTextField().getText());
    }
}
