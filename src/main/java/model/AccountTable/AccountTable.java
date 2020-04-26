package model.AccountTable;

import model.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountTable extends Database {

    public static boolean isThereUsername(String username) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Clients WHERE Username = " + username);
        if(resultSet.next())
            return true;
        resultSet = statement.executeQuery("SELECT * FROM Managers WHERE Username = " + username);
        if(resultSet.next())
            return true;
        resultSet = statement.executeQuery("SELECT * FROM Vendors WHERE Username = " + username);
        if(resultSet.next())
            return true;
        return false;
    }

    public static String getUserType(String username) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Clients WHERE Username = " + username);
        if(resultSet.next())
            return "Client";
        resultSet = statement.executeQuery("SELECT * FROM Managers WHERE Username = " + username);
        if(resultSet.next())
            return "Manager";
        resultSet = statement.executeQuery("SELECT * FROM Vendors WHERE Username = " + username);
        if(resultSet.next())
            return "Vendor";
        return null;
    }

    public static boolean isPasswordCorrect(String username, String password, String type) throws SQLException {
        String table = setTable(type);
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + " Where Username = " + username);
        resultSet.next();
        return password.equals(resultSet.getString("Password"));
    }

    private static String setTable(String type) {
        if(type.equalsIgnoreCase("Client"))
            return "Clients";
        else if(type.equalsIgnoreCase("Manager"))
            return "Managers";
        else if(type.equalsIgnoreCase("Vendor"))
            return "Vendors";
        return null;
    }
}
