package com.example.atm_mashine;

import com.example.atm_machine.DatabaseHandler;
import com.example.atm_machine.Helper;
import com.example.atm_machine.Models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseHandlerTest {

    @InjectMocks
    private static DatabaseHandler databaseHandler;
    @Mock
    private static Connection mockConnection;
    @Mock
    private static PreparedStatement mockPreparedStatement;
    @Mock
    private static ResultSet mockResultSet;

    @BeforeEach
    public void initEach() throws SQLException {

        databaseHandler = spy(DatabaseHandler.getDatabaseHandler());

        when(databaseHandler.connectDB()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @AfterEach
    public void cleanUpEach() throws SQLException {

        verify(mockConnection).close();
    }

    @Test
    public void getAuthUserShouldReturnUser() throws SQLException {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("TestName");
        when(mockResultSet.getString("surname")).thenReturn("TestSurname");
        when(mockResultSet.getInt("balance")).thenReturn(500);

        User user = databaseHandler.getAuthUser("9999", "9999");

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("9999", user.getLogin());
        assertEquals("9999", user.getPassword());
        assertEquals("TestName", user.getName());
        assertEquals("TestSurname", user.getSurname());
        assertEquals(500, user.getBalance());

        verify(mockPreparedStatement, times(1)).setString(1, "9999");
        verify(mockPreparedStatement, times(1)).setString(2, "9999");
        verify(mockPreparedStatement, times(1)).executeQuery();

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
    }

    @Test
    public void getAuthUserShouldNotReturnUser() throws SQLException {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        User user = databaseHandler.getAuthUser("invalidUser", "invalidPassword");

        assertNull(user);

        verify(mockPreparedStatement, times(1)).setString(1, "invalidUser");
        verify(mockPreparedStatement, times(1)).setString(2, "invalidPassword");
        verify(mockPreparedStatement, times(1)).executeQuery();

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
    }

    @Test
    public void getAuthUserShouldNotThrowExceptionWhenSQLExceptionOccurs() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertDoesNotThrow(() -> databaseHandler.getAuthUser("9999", "9999"));
    }

    @Test
    public void updateBalanceShouldUpdateDataInDatabase() throws SQLException {

        databaseHandler.updateBalance(500, 1, "UPDATE users SET balance = ? WHERE id = ?");

        // Проверяем, что запрос был выполнен один раз и соответствующие значения были установлены
        verify(mockPreparedStatement, times(1)).setInt(1, 500);
        verify(mockPreparedStatement, times(1)).setInt(2, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();

        verify(mockPreparedStatement).close();
    }

    @Test
    public void updateBalanceShouldNotThrowExceptionWhenSQLExceptionOccurs() throws SQLException {

        //Тестируем сценарий, когда происходит ошибка SQLException, чтобы убедиться, что метод обрабатывает исключение корректно и не выбрасывает необработанное исключение.

        // Настройка исключения при выполнении метода prepareStatement
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // Проверяем, что метод executeUpdate не выбрасывает необработанное исключение
        assertDoesNotThrow(() -> databaseHandler.updateBalance(500,1, "UPDATE users SET balance = ? WHERE login = ?"));
    }

    @Test
    public void addTransactionShouldAddTransactionToDatabase() throws SQLException {

        Helper mockHelper = mock(Helper.class);
        when(mockHelper.showDateTime("dd.MM.yyyy HH:mm")).thenReturn("testDate");

        databaseHandler.setHelper(mockHelper);

        databaseHandler.addTransaction(1, "testName", "testOperation", 500);

        // Проверяем, что запрос был выполнен один раз и соответствующие значения были установлены
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).setString(2, "testName");
        verify(mockPreparedStatement, times(1)).setString(3, "testOperation");
        verify(mockPreparedStatement, times(1)).setString(4, "testDate");
        verify(mockPreparedStatement, times(1)).setInt(5, 500);
        verify(mockPreparedStatement, times(1)).executeUpdate();

        verify(mockPreparedStatement).close();
    }

    @Test
    public void addTransactionShouldNotThrowExceptionWhenSQLExceptionOccurs() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertDoesNotThrow(() -> databaseHandler.addTransaction(1, "testName", "testOperation", 500));
    }

    @Test
    public void getNameAndSurnameShouldReturnNameAndSurname() throws SQLException {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("name")).thenReturn("TestName");
        when(mockResultSet.getString("surname")).thenReturn("TestSurname");

        String resultString = databaseHandler.getNameAndSurname("testLogin");

        assertNotNull(resultString);
        assertEquals("TestName TestSurname", resultString);

        verify(mockPreparedStatement, times(1)).setString(1, "testLogin");
        verify(mockPreparedStatement, times(1)).executeQuery();

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
    }

    @Test
    public void getNameAndSurnameShouldNotThrowExceptionWhenSQLExceptionOccurs() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertDoesNotThrow(() -> databaseHandler.getNameAndSurname("testLogin"));
    }

    @Test
    public void addUserShouldAddUserToDatabase() throws SQLException {

        databaseHandler.addUser("testName", "testSurname", "testLogin", "testPassword");

        // Проверяем, что запрос был выполнен один раз и соответствующие значения были установлены
        verify(mockPreparedStatement, times(1)).setString(1, "testLogin");
        verify(mockPreparedStatement, times(1)).setString(2, "testPassword");
        verify(mockPreparedStatement, times(1)).setString(3, "testName");
        verify(mockPreparedStatement, times(1)).setString(4, "testSurname");
        verify(mockPreparedStatement, times(1)).setInt(5, 0);
        verify(mockPreparedStatement, times(1)).executeUpdate();

        verify(mockPreparedStatement).close();
    }

    @Test
    public void addUserShouldNotThrowExceptionWhenSQLExceptionOccurs() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertDoesNotThrow(() -> databaseHandler.addUser("testName", "testSurname", "testLogin", "testPassword"));
    }

    @Test
    public void getRecipientIdShouldReturnRecipientId() throws SQLException {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);

        int result = databaseHandler.getRecipientId("testLogin");

        assertEquals(1, result);

        verify(mockPreparedStatement, times(1)).setString(1, "testLogin");
        verify(mockPreparedStatement, times(1)).executeQuery();

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
    }

    @Test
    public void getRecipientIdShouldNotThrowExceptionWhenSQLExceptionOccurs() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertDoesNotThrow(() -> databaseHandler.getRecipientId("testLogin"));
    }

    @Test
    public void returnNewLoginShouldReturnLoginWhenUserExists() throws SQLException {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("login")).thenReturn("1551");

        String result = databaseHandler.returnNewLogin();

        assertEquals("1551", result);

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getString("login");

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
    }

    @Test
    public void returnNewShouldHandleSQLException() throws SQLException {

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertDoesNotThrow(() -> databaseHandler.returnNewLogin());
    }
}
