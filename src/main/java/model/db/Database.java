package model.db;

import java.io.File;
import java.sql.*;

public class Database {
    private static boolean isDBInit = false;
    private static Connection connection = null;
    private static String localDBUrl = "jdbc:sqlite:database\\database.sqlite";

    protected static Connection getConnection() throws SQLException, ClassNotFoundException {
        if(!isDBInit)
            initDB();
        Class.forName("org.sqlite.JDBC");
        if(connection == null)
            connection = DriverManager.getConnection(localDBUrl);
        return connection;
    }

    private static void initDB() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File DBFolder = new File("database");
        DBFolder.mkdir();

        Connection
            initConnection = DriverManager.getConnection(localDBUrl);
        initAccountTable(initConnection.createStatement());
        initProductTable(initConnection.createStatement());
        initEditingProductTable(initConnection.createStatement());

        isDBInit = true;
        initConnection.close();
    }

    private static void initEditingProductTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'EditingProducts'";
        ResultSet resultSet = statement.executeQuery(command);
        if(!resultSet.next()){
            statement.execute("CREATE TABLE EditingProducts(" +
                    "ID varchar(8)," +
                    "ProductName varchar(20)," +
                    "Brand varchar(20)," +
                    "SellerUsername varchar(16)," +
                    "Num int," +
                    "Amount double," +
                    "IsCountable BIT," +
                    "Category varchar(20)," +
                    "Description varchar(100)," +
                    "Price double," +
                    "AverageScore double," +
                    "primary key(ID)" +
                    ");");
        }

        statement.close(); resultSet.close();
    }

    private static void initProductTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Products'";
        ResultSet resultSet = statement.executeQuery(command);
        if(!resultSet.next()){
            statement.execute("CREATE TABLE Products(" +
                    "ID varchar(8)," +
                    "Status int," +
                    "ProductName varchar(20)," +
                    "Brand varchar(20)," +
                    "SellerUsername varchar(16)," +
                    "Count int," +
                    "Amount double," +
                    "IsCountable BIT," +
                    "Category varchar(20)," +
                    "Description varchar(100)," +
                    "Price double," +
                    "AverageScore double," +
                    "primary key(ID)" +
                    ");");
        }

        statement.close(); resultSet.close();
    }

    private static void initAccountTable(Statement statement) throws SQLException {
        String command = "SELECT name from sqlite_master WHERE type = 'table' AND name = 'Accounts'";
        ResultSet resultSet = statement.executeQuery(command);
        if(!resultSet.next())
        {
            statement.execute("CREATE TABLE Accounts(" +
                    "Username varchar (16)," +
                    "Password varchar (16)," +
                    "AccType varchar (10)," +
                    "FirstName varchar (25)," +
                    "LastName varchar (25)," +
                    "Email varchar (35)," +
                    "Brand varchar(35)," +
                    "Credit double," +
                    "IsApproved BIT," +
                    "primary key(Username)" +
                    ");");
        }

        statement.close(); resultSet.close();
    }

}
