package com.example.atm_machine;

import com.example.atm_machine.Models.User;

import java.sql.*;

public class DatabaseHandler {

    private static DatabaseHandler databaseHandler;
    private Helper helper = Helper.getHelper();

    public static final String UPDATE_BALANCE = "UPDATE users SET balance = ? WHERE id = ?";
    public static final String TRANSFER_UPDATE_BALANCE = "UPDATE users SET balance = balance + ? WHERE id = ?";

    private DatabaseHandler() {
    }

    public static DatabaseHandler getDatabaseHandler() {

        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler();
        }
        return databaseHandler;
    }

    public Connection connectDB() {

        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:accounts.db");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public User getAuthUser(String loginText, String passwordText) {

        String select = "SELECT * FROM users WHERE login = ? AND password = ?";

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(select)) {

            prSt.setString(1, loginText);
            prSt.setString(2, passwordText);

            try (ResultSet rs = prSt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    int balance = rs.getInt("balance");

                    return new User(id, loginText, passwordText, name, surname, balance);
                }
            }
        } catch (SQLException e) {
            Application.logger.error("An error occurred while searching for a user in the database: " + e.getMessage());
        }
        return null;
    }

    public void updateBalance(int amount, int id, String update) {

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(update)) {

            prSt.setInt(1, amount);
            prSt.setInt(2, id);

            prSt.executeUpdate();
        } catch (SQLException e) {
            Application.logger.error("An error occurred while updating the balance in the database: " + e.getMessage());
        }
    }

    public void addTransaction(int user_id, String name, String operation, int amount) {

        String insert = "INSERT INTO transactions (user_id, name, operation, date, amount) VALUES (?,?,?,?,?)";

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(insert)){

            prSt.setInt(1, user_id);
            prSt.setString(2, name);
            prSt.setString(3, operation);
            prSt.setString(4, helper.showDateTime("dd.MM.yyyy HH:mm"));
            prSt.setInt(5, amount);

            prSt.executeUpdate();
        } catch (SQLException e) {
            Application.logger.error("An error occurred while adding a transaction to the database: " + e.getMessage());
        }
    }

    public String getNameAndSurname(String login) {

        String select = "SELECT * FROM users WHERE login = ?";

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(select)){

            prSt.setString(1, login);

            try (ResultSet rs = prSt.executeQuery()) {

                    if (rs.next()) {
                    String transferUserName = rs.getString("name");
                    String transferUserSurname = rs.getString("surname");

                    return transferUserName + " " + transferUserSurname;
                }
            }
        } catch (SQLException e) {
            Application.logger.error("An error occurred while searching for first and last name in the database: " + e.getMessage());
        }
        return null;
    }

    public void addUser(String nameText, String surnameText, String loginText, String passwordText) {

        String insert = "INSERT INTO users (login, password, name, surname, balance) VALUES (?,?,?,?,?)";

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(insert)) {

            prSt.setString(1, loginText);
            prSt.setString(2, passwordText);
            prSt.setString(3, nameText);
            prSt.setString(4, surnameText);
            prSt.setInt(5, 0);

            prSt.executeUpdate();
        } catch (SQLException e) {
            Application.logger.error("An error occurred while adding a user to the database: " + e.getMessage());
        }
    }

    public int getRecipientId(String login) {

        String select = "SELECT * FROM users WHERE login = ?";

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(select)){

            prSt.setString(1, login);

            try (ResultSet rs = prSt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            Application.logger.error("An error occurred while searching for Id in the database: " + e.getMessage());
        }
        return 0;
    }

    public String returnNewLogin() {

        String select = "SELECT * FROM users ORDER BY id DESC LIMIT 1";

        try (Connection conn = connectDB();
             PreparedStatement prSt = conn.prepareStatement(select);
             ResultSet rs = prSt.executeQuery()){

            if (rs.next()) {

                return rs.getString("login");
            }
        } catch (SQLException e) {
            Application.logger.error("An error occurred while searching for login in the database: " + e.getMessage());
        }
        return null;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
}