package com.example.atm_mashine.Controllers;

import com.example.atm_machine.Controllers.RegisterController;
import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerTest {

    @InjectMocks
    private static RegisterController registerController;
    @Mock
    private DatabaseHandler mockDatabaseHandler;
    private TextField registerNameTextField;
    private TextField registerSurnameTextField;
    private TextField registerPasswordTextField;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        registerNameTextField = new TextField();
        registerSurnameTextField = new TextField();
        registerPasswordTextField = new TextField();
        Label errorRegisterLabel = new Label();
        registerController.setRegisterNameTextField(registerNameTextField);
        registerController.setRegisterSurnameTextField(registerSurnameTextField);
        registerController.setRegisterPasswordTextField(registerPasswordTextField);
        registerController.setRegisterErrorLabel(errorRegisterLabel);
    }

    private void setupAddUserTest(String name, String surname, String password) {

        registerNameTextField.setText(name);
        registerSurnameTextField.setText(surname);
        when(mockDatabaseHandler.returnNewLogin()).thenReturn("9696");
        registerPasswordTextField.setText(password);
    }

    private void verifyAddUserTestResults(String expectedResult, Color expectedColor, String expectedName, String expectedSurname, String expectedPassword) {

        assertEquals(expectedResult, registerController.getRegisterErrorLabel().getText());
        assertEquals(expectedColor, registerController.getRegisterErrorLabel().getTextFill());
        assertEquals(expectedName, registerController.getRegisterNameTextField().getText());
        assertEquals(expectedSurname, registerController.getRegisterSurnameTextField().getText());
        assertEquals(expectedPassword, registerController.getRegisterPasswordTextField().getText());
    }

    @Test
    public void addUserShouldVerifyUserRegistrationWithValidData() {

        setupAddUserTest("Tom", "Harris", "9999");

        doNothing().when(mockDatabaseHandler).addUser("Tom", "Harris", "9697", "9999");

        registerController.addUser(mock(ActionEvent.class));

        verify(mockDatabaseHandler).addUser(eq("Tom"), eq("Harris"), eq("9697"), eq("9999"));
        verifyAddUserTestResults("Успешно!\nВаш логин: " + "9697" + "\nПароль: " + "9999", Color.GREEN, "", "", "");
    }

    @Test
    public void addUserShouldNotThrowExceptionWhenLoginRetrievalFails() {

        setupAddUserTest("Tom", "Harris", "9999");

        when(mockDatabaseHandler.returnNewLogin()).thenReturn("Логин");

        assertDoesNotThrow(() -> registerController.addUser(mock(ActionEvent.class)));

        verify(mockDatabaseHandler, never()).addUser(anyString(), anyString(), anyString(), anyString());
        verifyAddUserTestResults("Ошибка при получении логина. Попробуйте снова", Color.RED, "Tom", "Harris", "9999");
    }

    @Test
    public void addUserShouldNotRegisterWithEmptyFields() {

        setupAddUserTest("1", "1", "");

        registerController.addUser(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).addUser(anyString(), anyString(), anyString(), anyString());
        verifyAddUserTestResults("Заполните все поля!", Color.RED, "1", "1", "");
    }

    @Test
    public void addUserShouldNotRegisterWithExcessiveNameAndSurnameLength() {

        setupAddUserTest("TomTomTommTomTomTommTomTomTommm", "Harris", "9999");

        registerController.addUser(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).addUser(anyString(), anyString(), anyString(), anyString());
        verifyAddUserTestResults("Имя и фамилия должны содержать не более 30 символов!", Color.RED, "", "", "9999");
    }

    @Test
    public void addUserShouldNotRegisterWithShortLoginOrPassword() {

        setupAddUserTest("Tom", "Harris", "99");

        registerController.addUser(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).addUser(anyString(), anyString(), anyString(), anyString());
        verifyAddUserTestResults("Пароль должен состоять из 4 цифр", Color.RED, "Tom", "Harris", "");
    }

    @Test
    public void addUserShouldNotRegisterWithNonUniqueLogin() {

        setupAddUserTest("Tom", "Harris", "9999");

        when(mockDatabaseHandler.getRecipientId(anyString())).thenReturn(1);

        registerController.addUser(mock(ActionEvent.class));

        verify(mockDatabaseHandler, never()).addUser(anyString(), anyString(), anyString(), anyString());
        verifyAddUserTestResults("Произошла ошибка. Попробуйте снова", Color.RED, "Tom", "Harris", "9999");
    }

    @Test
    public void openLoginSceneShouldNavigateToLoginPage() {

        Helper mockHelper = mock(Helper.class);
        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        registerController.setHelper(mockHelper);

        registerController.openLoginScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Login.fxml"), any(ActionEvent.class));
    }
}
