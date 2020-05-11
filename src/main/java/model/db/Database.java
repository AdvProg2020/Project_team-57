package model.db;

import java.io.File;
import java.sql.*;

public class Database {
    private static boolean isDBInit = false;
    private static Connection connection = null;
    private static final String localDBUrl = "jdbc:sqlite:database\\database.sqlite";

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
        initCartsTable(initConnection.createStatement());
        initCategoriesTable(initConnection.createStatement());
        removeTempAccountsFromCarts(initConnection);

        isDBInit = true;
        initConnection.close();
    }

    private static void removeTempAccountsFromCarts(Connection initConnection) throws SQLException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ? ";
        PreparedStatement preparedStatement = initConnection.prepareStatement(command);
        preparedStatement.setString(1, "temp");
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static void initCartsTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Carts'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()) {
            statement.execute("CREATE TABLE Carts(" +
                    "ID varchar(8)," +
                    "CustomerUsername varchar(16)," +
                    "Count int," +
                    "Amount double);");
        }
        statement.close();
        resultSet.close();
    }

    private static void initCategoriesTable(Statement statement) throws SQLException, ClassNotFoundException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Categories'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()) {
            statement.execute("CREATE TABLE Categories(" +
                    "Name varchar(16)," +
                    "Features varchar(100)," +
                    "ParentCategory varchar(100));");
            String SQL = "INSERT INTO Categories(Name, Features, ParentCategory) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(SQL);
            preparedStatement.setString(1, "AllProducts");
            preparedStatement.setString(2, "This category contains all of product that exist in store");
            preparedStatement.setString(3, null);
            preparedStatement.execute();
        }
        statement.close();
        resultSet.close();
    }

    private static void initEditingProductTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'EditingProducts'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()) {
            statement.execute("CREATE TABLE EditingProducts(" +
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
                    "ApprovalDate DATE, " +
                    "Seen int," +
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
                    "ApprovalDate DATE, " +
                    "Seen int," +
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
