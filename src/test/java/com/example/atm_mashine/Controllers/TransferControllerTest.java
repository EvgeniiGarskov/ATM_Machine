package com.example.atm_mashine.Controllers;

import com.example.atm_machine.Controllers.TransferController;
import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
import com.example.atm_machine.Models.User;
import com.example.atm_machine.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {

    @InjectMocks
    private static TransferController transferController;
    @Spy
    private static UserSession userSession;
    @Mock
    private DatabaseHandler mockDatabaseHandler;
    private Label transferLabel;
    private Label transferRecipient;
    private TextField transferLoginTextField;
    private TextField transferTextField;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        transferLabel = new Label();
        transferLoginTextField = new TextField();
        transferRecipient = new Label();
        transferTextField = new TextField();
        transferController.setTransferLabel(transferLabel);
        transferController.setTransferLoginTextField(transferLoginTextField);
    }

    private void setupTransferTest(String login, String amount) {

        transferLoginTextField.setText(login);
        transferTextField.setText(amount);

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 1500));

        transferController.setTransferRecipientLabel(transferRecipient);
        transferController.setTransferTextField(transferTextField);
    }

    private void setupFindUserTest(String login) {

        transferLoginTextField.setText(login);

        transferController.setTransferTextField(transferTextField);
        transferController.setTransferRecipientLabel(transferRecipient);
    }

    private void verifyFindUserTestResults(String expectedResult) {

        assertEquals(expectedResult, transferController.getTransferLabel().getText());
        assertEquals("", transferController.getTransferLoginTextField().getText());
        assertEquals("", transferController.getTransferRecipientLabel().getText());
        assertEquals("", transferController.getTransferTextField().getText());
    }

    private void verifyTransferTestResults(int amount, String expectedResult, Color expectedColor, String expectedLogin, String expectedAmount) {

        assertEquals(amount, userSession.getCurrentUser().getBalance());
        assertEquals(expectedResult, transferController.getTransferLabel().getText());
        assertEquals(expectedColor, transferController.getTransferLabel().getTextFill());
        assertEquals("", transferController.getTransferRecipientLabel().getText());
        assertEquals(expectedLogin, transferController.getTransferLoginTextField().getText());
        assertEquals(expectedAmount, transferController.getTransferTextField().getText());
    }

    @Test
    public void transferShouldUpdateBalanceAndAddTransactionAndExecuteTransferSuccessfully() {

        setupTransferTest("8888", "500");

        doNothing().when(mockDatabaseHandler).updateBalance(1000, 1, DatabaseHandler.UPDATE_BALANCE);
        doNothing().when(mockDatabaseHandler).updateBalance(500, 2, DatabaseHandler.TRANSFER_UPDATE_BALANCE);
        doNothing().when(mockDatabaseHandler).addTransaction(1, "Tina Smith", "Перевод клиенту", 500);
        doNothing().when(mockDatabaseHandler).addTransaction(2, "Tom Harris", "Входящий перевод", 500);
        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn("Tina Smith");
        when(mockDatabaseHandler.getRecipientId(anyString())).thenReturn(2);

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler).updateBalance(eq(1000), eq(1), eq("UPDATE users SET balance = ? WHERE id = ?"));
        verify(mockDatabaseHandler).updateBalance(eq(500), eq(2), eq("UPDATE users SET balance = balance + ? WHERE id = ?"));
        verify(mockDatabaseHandler).addTransaction(eq(1), eq("Tina Smith"), eq("Перевод клиенту"), eq(500));
        verify(mockDatabaseHandler).addTransaction(eq(2), eq("Tom Harris"), eq("Входящий перевод"), eq(500));

        verifyTransferTestResults(1000, "Перевод выполнен успешно!", Color.GREEN, "", "");
    }

    @Test
    public void transferShouldNotMakeTransferIfTheTransferLoginTextFieldIsEmpty() {

        setupTransferTest("", "500");

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Введите логин", Color.RED, "", "500");
    }

    @Test
    public void transferShouldNotMakeTransferIfTheTransferTextFieldIsEmpty() {

        setupTransferTest("8888", "");

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Введите сумму для перевода", Color.RED, "8888", "");
    }

    @Test
    public void transferShouldNotUpdateBalanceOrAddTransactionWhenInputIsInvalid() {

        setupTransferTest("8888", "Сумма");

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn("Tina Smith");

        assertDoesNotThrow(() -> transferController.transfer(mock(ActionEvent.class)));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Некорректный ввод суммы", Color.RED, "8888", "");
    }

    @Test
    public void transferShouldNotUpdateTheBalanceWhenTheValueIsNegative() {

        setupTransferTest("7777", "-500");

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn("Tina Smith");

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Сумма не может быть отрицательной", Color.RED, "7777", "");
    }

    @Test
    public void transferShouldNotProcessTransferWhenUserIsNotLoggedIn() {

        setupTransferTest("8888", "500");

        userSession.setCurrentUser(null);

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn("Tina Smith");

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        assertEquals("Не удалось получить информацию о пользователе", transferController.getTransferLabel().getText());
        assertEquals(Color.RED, transferController.getTransferLabel().getTextFill());
        assertEquals("", transferController.getTransferRecipientLabel().getText());
        assertEquals("8888", transferController.getTransferLoginTextField().getText());
        assertEquals("500", transferController.getTransferTextField().getText());
    }

    @Test
    public void transferShouldNotTransferAmountMaximumBalanceUserAndUpdateBalance() {

        setupTransferTest("8888", "2000");

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn("Tina Smith");

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Такую сумму нельзя перевести", Color.RED, "8888", "");
    }

    @Test
    public void transferShouldNotTransferWithShortLogin() {

        setupTransferTest("888", "500");

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Логин должен состоять из 4 цифр", Color.RED, "", "500");
    }

    @Test
    public void transferShouldNotTransferIfUserNotFound() {

        setupTransferTest("7777", "500");

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn(null);

        transferController.transfer(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyTransferTestResults(1500, "Пользователь не найден", Color.RED, "", "500");
    }

    @Test
    public void findUserShouldDisplayRecipientInformationOnSearch() {
        setupFindUserTest("9999");

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn("9999");

        transferController.findUserToTransferAmount(mock(ActionEvent.class));

        assertEquals("Получатель:\n" + "9999", transferController.getTransferRecipientLabel().getText());
    }

    @Test
    public void findUserShouldShowErrorMessageWhenLoginFieldIsEmpty() {

        setupFindUserTest("");

        transferController.findUserToTransferAmount(mock(ActionEvent.class));

        verifyFindUserTestResults("Введите логин");
    }

    @Test
    public void findUserShouldDisplayErrorForInvalidLoginOnSearch() {

        setupFindUserTest("999");

        transferController.findUserToTransferAmount(mock(ActionEvent.class));

        verifyFindUserTestResults("Логин должен состоять из 4 цифр");
    }

    @Test
    public void findUserShouldDisplayUserNotFoundOnSearch() {

        setupFindUserTest("8888");

        when(mockDatabaseHandler.getNameAndSurname(anyString())).thenReturn(null);

        transferController.findUserToTransferAmount(mock(ActionEvent.class));

        verifyFindUserTestResults("Пользователь не найден");
    }

    @Test
    public void openMenuSceneShouldNavigateToMenuPage() {

        Helper mockHelper = mock(Helper.class);
        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        transferController.setHelper(mockHelper);

        transferController.openMenuScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Menu.fxml"), any(ActionEvent.class));
    }

    @Test
    public void resetTransferFieldsShouldResetTransferFieldsOnButtonClick() {

        transferLoginTextField.setText("8888");
        transferTextField = new TextField();
        transferTextField.setText("500");
        transferLabel.setText("test");

        transferController.setTransferTextField(transferTextField);

        transferController.resetTransferFields(mock(ActionEvent.class));

        assertEquals("", transferController.getTransferLoginTextField().getText());
        assertEquals("", transferController.getTransferTextField().getText());
        assertEquals("", transferController.getTransferLabel().getText());
    }
}
