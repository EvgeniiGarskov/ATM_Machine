package com.example.atm_mashine.Controllers;

import com.example.atm_machine.*;
import com.example.atm_machine.Controllers.DepositController;
import com.example.atm_machine.Models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositControllerTest {

    @InjectMocks
    private static DepositController depositController;
    @Mock
    private DatabaseHandler mockDatabaseHandler;
    @Spy
    private UserSession userSession;
    private TextField depositTextField;
    private Label depositLabel;

    @BeforeAll
    public static void init() {

        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        depositTextField = new TextField();
        depositController.setDepositTextField(depositTextField);
    }

    private void setupDepositTest(String amount) {

        depositLabel = new Label();
        depositTextField.setText(amount);

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 5000));

        depositController.setDepositLabel(depositLabel);
    }

    private void verifyDepositTestResults(int expectedAmount, String expectedResult, Color expectedColor) {

        assertEquals(expectedAmount, userSession.getCurrentUser().getBalance());
        assertEquals(expectedResult, depositController.getDepositLabel().getText());
        assertEquals(expectedColor, depositController.getDepositLabel().getTextFill());
        assertEquals("", depositController.getDepositTextField().getText());
    }

    @Test
    public void depositShouldUpdateBalanceAndAddTransaction() {

        setupDepositTest("500");

        doNothing().when(mockDatabaseHandler).updateBalance(5500, 1, DatabaseHandler.UPDATE_BALANCE);
        doNothing().when(mockDatabaseHandler).addTransaction(1, "Банкомат", "Внесение наличных", 500);

        depositController.deposit(mock(ActionEvent.class));

        verify(mockDatabaseHandler).updateBalance(eq(5500), eq(1), eq("UPDATE users SET balance = ? WHERE id = ?"));
        verify(mockDatabaseHandler).addTransaction(eq(1), eq("Банкомат"), eq("Внесение наличных"), eq(500));

        verifyDepositTestResults(5500, "Успешно!", Color.GREEN);
    }

    @Test
    public void depositShouldNotMakeDepositIfTheDepositTextFieldIsEmpty() {

        setupDepositTest("");

        depositController.deposit(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyDepositTestResults(5000, "Введите сумму для внесения", Color.RED);
    }

    @Test
    public void depositShouldNotThrowExceptionWhenDepositIsInvalid() {

        setupDepositTest("Число");

        assertDoesNotThrow(() -> depositController.deposit(mock(ActionEvent.class)));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyDepositTestResults(5000, "Некорректный ввод суммы", Color.RED);
    }

    @Test
    public void depositShouldNotUpdateTheBalanceWhenTheValueIsNegative() {

        setupDepositTest("-100");

        depositController.deposit(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyDepositTestResults(5000, "Сумма не может быть отрицательной", Color.RED);
    }

    @Test
    public void depositShouldNotDepositAmountUnderMinimumThresholdAndUpdateBalance() {

        setupDepositTest("50");

        depositController.deposit(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        verifyDepositTestResults(5000, "Сумма внесения меньше 100 ₽", Color.RED);
    }

    @Test
    public void depositShouldNotThrowExceptionWhenUserIsNull() {

        setupDepositTest("500");

        userSession.setCurrentUser(null);

        depositController.deposit(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).updateBalance(anyInt(), anyInt(), anyString());
        verify(mockDatabaseHandler, never()).addTransaction(anyInt(), anyString(), anyString(), anyInt());

        assertEquals("Не удалось получить информацию о пользователе", depositController.getDepositLabel().getText());
        assertEquals(Color.RED, depositController.getDepositLabel().getTextFill());
        assertEquals("", depositController.getDepositTextField().getText());
    }

    @Test
    public void openMenuSceneShouldNavigateToMenuPage() {

        Helper mockHelper = mock(Helper.class);
        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        userSession.setCurrentUser(mock(User.class));

        depositController.setHelper(mockHelper);

        depositController.openMenuScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Menu.fxml"), any(ActionEvent.class));
    }

    @Test
    public void appendNumberToDepositShouldConcatenateButtonNumberToDepositTextField() {

        Button button = new Button("5");

        depositTextField.setText("7");

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getSource()).thenReturn(button);

        depositController.appendNumberToDeposit(mockEvent);

        assertEquals("75", depositController.getDepositTextField().getText());
    }

    @Test
    public void resetDepositFieldsShouldResetDepositFieldsOnButtonClick() {

        depositTextField.setText("test");
        depositLabel = new Label("test");

        depositController.setDepositLabel(depositLabel);

        depositController.resetDepositFields(mock(ActionEvent.class));

        assertEquals("", depositController.getDepositTextField().getText());
        assertEquals("", depositController.getDepositLabel().getText());
    }

    @Test
    public void deleteLastDigitFromDepositShouldDeleteLastCharacterFromDepositTextFieldOnButtonClick() {

        depositTextField.setText("Deposit");

        depositController.deleteLastDigitFromDeposit(mock(ActionEvent.class));

        assertEquals("Deposi", depositController.getDepositTextField().getText());

        depositController.deleteLastDigitFromDeposit(mock(ActionEvent.class));

        assertEquals("Depos", depositController.getDepositTextField().getText());
    }
}
