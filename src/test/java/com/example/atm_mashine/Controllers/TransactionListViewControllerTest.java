package com.example.atm_mashine.Controllers;

import com.example.atm_machine.*;
import com.example.atm_machine.Controllers.TransactionListViewController;
import com.example.atm_machine.Models.Transaction;
import com.example.atm_machine.Models.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionListViewControllerTest {

    @InjectMocks
    private static TransactionListViewController transactionListViewController;
    @Mock
    private static DatabaseHandler mockDatabaseHandler;
    @Spy
    private static UserSession userSession;
    @Mock
    private static ResultSet mockResultSet;
    @Mock
    private static Connection mockConnection;
    @Mock
    private static PreparedStatement mockPreparedStatement;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void initEach() {

        userSession.setCurrentUser(new User(1, "9999", "9999", "Tom", "Harris", 5000));
    }

    @Test
    public void addTransactionsListViewShouldPopulateListView() throws SQLException {

        when(mockDatabaseHandler.connectDB()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("name")).thenReturn("TestName");
        when(mockResultSet.getString("operation")).thenReturn("TestOperation");
        when(mockResultSet.getString("date")).thenReturn("TestDate");
        when(mockResultSet.getInt("amount")).thenReturn(500);

        ObservableList<Transaction> mockTransactionObservableList = FXCollections.observableArrayList();

        transactionListViewController.setTransactionObservableList(mockTransactionObservableList);

        transactionListViewController.addTransactionsListView();

        assertEquals(2, transactionListViewController.getTransactionObservableList().size());
        assertFalse(transactionListViewController.getTransactionObservableList().isEmpty());
        assertEquals("TestName", transactionListViewController.getTransactionObservableList().get(0).getName());
        assertEquals("TestOperation", transactionListViewController.getTransactionObservableList().get(1).getOperation());

        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();

        verify(mockResultSet).close();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    public void testAddTransactionsListView_WhenSQLExceptionThrownFromPreparedStatement() throws SQLException {

        when(mockDatabaseHandler.connectDB()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("\nPreparedStatement failed"));

        assertDoesNotThrow(() -> transactionListViewController.addTransactionsListView());

        verify(mockConnection).close();
    }

    @Test
    public void testAddTransactionsListView_WhenSQLExceptionThrownFromResultSet() throws SQLException {

        when(mockDatabaseHandler.connectDB()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("\nResultSet failed"));

        assertDoesNotThrow(() -> transactionListViewController.addTransactionsListView());

        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    public void openMenuSceneShouldNavigateToMenuPage() {

        Helper mockHelper = mock(Helper.class);
        doNothing().when(mockHelper).openNewScene(anyString(), any(ActionEvent.class));

        transactionListViewController.setHelper(mockHelper);

        transactionListViewController.openMenuScene(mock(ActionEvent.class));

        verify(mockHelper).openNewScene(eq("/Fxml/Menu.fxml"), any(ActionEvent.class));
    }
}
