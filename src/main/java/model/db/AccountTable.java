package model.db;

import model.existence.Account;

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

    public static String getTypeByUsername(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT AccType From Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString("AccType");
    }

    public static boolean isThereAdmin() throws SQLException, ClassNotFoundException {
        String command = "SELECT * From Accounts WHERE AccType = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "Admin");
        return preparedStatement.executeQuery().next();
    }

    public static Account getAccountByUsername(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT * From Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        return Account.makeAccount(preparedStatement.executeQuery());
    }

    public static void editField(String username, String fieldName, String value) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Accounts SET " + fieldName + " = ? WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, username);
        preparedStatement.execute();
    }

    public static String getValueByField(String username, String fieldName) throws SQLException, ClassNotFoundException {
        String command = "SELECT " + fieldName + " From Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString(fieldName);
    }

    public static void changeCredit(String username, double money) throws SQLException, ClassNotFoundException {
        double credit = getCredit(username);
        credit += money;
        String command = "UPDATE Accounts SET Credit = ? WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, credit);
        preparedStatement.setString(2, username);
        preparedStatement.execute();
    }

    public static double getCredit(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT Credit From Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getDouble("Credit");
    }
}
