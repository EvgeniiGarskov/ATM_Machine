package com.example.atm_mashine.Controllers;


import com.example.atm_machine.Controllers.LoginController;
import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
import com.example.atm_machine.Models.User;
import com.example.atm_machine.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @InjectMocks
    private static LoginController loginController;
    @Mock
    private DatabaseHandler mockDatabaseHandler;
    @Mock
    private static Helper mockHelper;
    @Spy
    private UserSession userSession;
    private TextField loginField;
    private PasswordField passwordField;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        loginField = new TextField();
        passwordField = new PasswordField();

        loginController.setLoginField(loginField);
        loginController.setPasswordField(passwordField);
    }

    private void setupUserAuthenticationTest(String login, String password) {

        loginField.setText(login);
        passwordField.setText(password);

        Label errorLoginLabel = new Label();
        loginController.setErrorLoginLabel(errorLoginLabel);
    }

    private void assertUserAuthenticationTestResults(String expectedResult, String expectedLogin) {

        assertNull(userSession.getCurrentUser());
        assertEquals(expectedResult, loginController.getErrorLoginLabel().getText());
        assertEquals(Color.RED, loginController.getErrorLoginLabel().getTextFill());
        assertEquals(expectedLogin, loginController.getLoginField().getText());
        assertEquals("", loginController.getPasswordField().getText());

        verify(mockHelper, never()).openNewScene(anyString(), any(ActionEvent.class));
    }

    @Test
    public void userAuthenticationShouldAuthenticateUserAndNavigateToMenuPage() {

        loginField.setText("9999");
        passwordField.setText("9999");

        when(mockDatabaseHandler.getAuthUser(anyString(), anyString())).thenReturn(new User(1, "9999", "9999", "Tom", "Harris", 5000));

        loginController.userAuthentication(mock(ActionEvent.class));

        verify(mockDatabaseHandler).getAuthUser(eq("9999"), eq("9999"));

        assertNotNull(userSession.getCurrentUser());

        verify(mockHelper).openNewScene(eq("/Fxml/Menu.fxml"), any(ActionEvent.class));
    }

    @Test
    public void userAuthenticationShouldNotMakeUserAuthenticationIfTheLoginFieldIsEmpty() {

        setupUserAuthenticationTest("", "");

        loginController.userAuthentication(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).getAuthUser(anyString(), anyString());

        assertUserAuthenticationTestResults("Введите логин", "");
    }

    @Test
    public void userAuthenticationShouldNotMakeUserAuthenticationIfThePasswordFieldIsEmpty() {

        setupUserAuthenticationTest("9999", "");

        loginController.userAuthentication(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).getAuthUser(anyString(), anyString());

        assertUserAuthenticationTestResults("Введите пароль", "9999");
    }

    @Test
    public void userAuthenticationShouldNotAuthenticateWithShortLoginOrPasswordAndShowErrorMessage() {

        setupUserAuthenticationTest("9999", "888");

        loginController.userAuthentication(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).getAuthUser(anyString(), anyString());

        assertUserAuthenticationTestResults("Логин и пароль должны состоять из 4 цифр", "");
    }

    @Test
    public void userAuthenticationShouldNotAuthenticateWithInvalidLoginPasswordAndShowErrorMessage() {

        setupUserAuthenticationTest("9999", "8888");

        when(mockDatabaseHandler.getAuthUser(anyString(), anyString())).thenReturn(null);

        loginController.userAuthentication(mock(ActionEvent.class));

        verify(mockDatabaseHandler).getAuthUser(eq("9999"), eq("8888"));

        assertUserAuthenticationTestResults("Не верный логин или пароль", "");
    }

    @Test
    public void openRegisterSceneShouldNavigateToRegisterPage() {

        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        loginController.setDatabaseHandler(mockDatabaseHandler);

        loginController.openRegisterScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Register.fxml"), any(ActionEvent.class));
    }
}
