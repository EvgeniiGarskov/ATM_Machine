package com.example.atm_mashine.Controllers;

import com.example.atm_machine.Controllers.MenuController;
import com.example.atm_machine.Controllers.WithdrawalController;
import com.example.atm_machine.Helper;
import com.example.atm_machine.Models.User;
import com.example.atm_machine.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuControllerTest {

    @InjectMocks
    private static MenuController menuController;
    @Mock
    private WithdrawalController withdrawalController;
    @Spy
    private Helper mockHelper;
    @Spy
    private UserSession userSession;
    private ActionEvent mockEvent;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 5000));

        Label menuNameLabel = new Label();
        Label menuBalanceLabel = new Label();
        Label menuCardNumberLabel = new Label();
        Label menuDateLabel = new Label();

        menuController.setMenuNameLabel(menuNameLabel);
        menuController.setMenuBalanceLabel(menuBalanceLabel);
        menuController.setMenuCardNumberLabel(menuCardNumberLabel);
        menuController.setMenuDateLabel(menuDateLabel);
    }

    private void setupFastWithdrawalTest(String buttonValue) {

        Button button = new Button(buttonValue);

        mockEvent = mock(ActionEvent.class);
        when(mockEvent.getSource()).thenReturn(button);

        menuController.setWithdrawalController(withdrawalController);
        menuController.setFastWithdrawalButton1000(button);
    }

    @Test
    public void showAccountInfoShouldInitialize() {

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String expectedDateTime = dateTimeFormatter.format(today);

        menuController.showAccountInfo();

        assertEquals("Здравствуйте, " + "Tom" + "!", menuController.getMenuNameLabel().getText());
        assertEquals("5000" + " ₽", menuController.getMenuBalanceLabel().getText());
        assertEquals("**** **** **** " + "9999", menuController.getMenuCardNumberLabel().getText());
        assertEquals(expectedDateTime, menuController.getMenuDateLabel().getText());
    }

    @Test
    public void openDepositSceneShouldNavigateToDepositPage() {

        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        menuController.openDepositScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Deposit.fxml"), any(ActionEvent.class));
    }

    @Test
    public void openWithdrawalSceneShouldNavigateToWithdrawalPage() {

        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        menuController.openWithdrawalScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Withdrawal.fxml"), any(ActionEvent.class));
    }

    @Test
    public void fastWithdrawalShouldInvokeFastWithdrawalWithCorrectParametersWhenButtonClicked() {

        setupFastWithdrawalTest("1000");

        menuController.fastWithdrawal(mockEvent);

        verify(withdrawalController).fastWithdrawal(eq(1000), eq(4000), any(ActionEvent.class));
    }

    @Test
    public void fastWithdrawalShouldNotThrowExceptionWhenInvalidButtonClicked() {

        setupFastWithdrawalTest("abc");

        assertDoesNotThrow(() -> menuController.fastWithdrawal(mockEvent));

        verify(withdrawalController, never()).fastWithdrawal(anyInt(), anyInt(), any(ActionEvent.class));
    }

    @Test
    public void fastWithdrawalShouldNotThrowExceptionWhenNegativeAmountButtonClicked() {

        setupFastWithdrawalTest("-1000");

        assertDoesNotThrow(() -> menuController.fastWithdrawal(mockEvent));

        verify(withdrawalController, never()).fastWithdrawal(anyInt(), anyInt(), any(ActionEvent.class));
    }

    @Test
    public void fastWithdrawalShouldNotWithdrawalWhenNoUserLoggedIn() {

        userSession.setCurrentUser(null);

        setupFastWithdrawalTest("1000");

        menuController.fastWithdrawal(mockEvent);

        verify(withdrawalController, never()).fastWithdrawal(anyInt(), anyInt(), any(ActionEvent.class));
    }

    @Test
    public void openTransferSceneShouldNavigateToTransferPage() {

        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        menuController.openTransferScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Transfer.fxml"), any(ActionEvent.class));
    }

    @Test
    public void openLogoutSceneShouldNavigateToLogoutPage() {

        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        menuController.openLogoutScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Login.fxml"), any(ActionEvent.class));
    }

    @Test
    public void openTransactionSceneShouldNavigateToTransactionPage() {

        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        menuController.openTransactionScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/TransactionsListView.fxml"), any(ActionEvent.class));
    }
}
