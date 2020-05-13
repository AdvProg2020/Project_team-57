package model.db;

import model.existence.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public static boolean isUserApproved(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT IsApproved FROM Accounts WHERE Username = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        return preparedStatement.executeQuery().getBoolean("IsApproved");
    }

    public static void addAccount(String username, String password, String accType) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Accounts (Username, Password, AccType, IsApproved) " +
                "VALUES (?, ?, ?, ?);";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, accType);
        if (!accType.equals("Vendor")) {
            preparedStatement.setBoolean(4, true);
        } else
            preparedStatement.setBoolean(4, false);
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

    public static ArrayList<Account> getAllAccounts() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE IsApproved = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setBoolean(1, true);
        ArrayList<Account> allAccounts = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            allAccounts.add(Account.makeAccount(resultSet));
        }
        return allAccounts;
    }

    public static void deleteUserWithUsername(String username) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Accounts WHERE Username = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.execute();
    }

    public static ArrayList<Account> getAllUsers() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE (AccType = ? OR AccType = ?) AND IsApproved = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "Vendor");
        preparedStatement.setString(2, "Customer");
        preparedStatement.setBoolean(3, true);
        ArrayList<Account> allUsers = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allUsers.add(Account.makeAccount(resultSet));
        }
        return allUsers;
    }

    public static ArrayList<Account> getAllAdmins() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE AccType = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "Admin");
        ArrayList<Account> allAdmins = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            allAdmins.add(Account.makeAccount(resultSet));
        }
        return allAdmins;
    }

    public static ArrayList<Account> getAllCustomers() throws SQLException, ClassNotFoundException {
        ArrayList<Account> allCustomers = new ArrayList<>();
        String command = "SELECT * FROM Accounts WHERE AccType = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "Customer");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
            allCustomers.add(Account.makeAccount(resultSet));
        return allCustomers;
    }
}
