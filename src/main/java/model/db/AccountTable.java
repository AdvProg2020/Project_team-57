package model.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountTable extends Database {
    public static boolean isUsernameFree(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT * From Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        return !preparedStatement.executeQuery().next();
    }

    public static boolean isPasswordCorrect(String username, String password) throws SQLException, ClassNotFoundException {
        String command = "SELECT Password From Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return password.equals(resultSet.getString("Password"));
    }

    public static void addAccount(String username, String password, String accType) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Accounts (Username, Password, AccType) " +
                                "VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username); preparedStatement.setString(2, password);
        preparedStatement.setString(3, accType);
        preparedStatement.execute();
    }
}
