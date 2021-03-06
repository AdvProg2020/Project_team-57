package server.model.db;

import java.io.File;
import java.sql.*;
import java.util.Calendar;

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
        initSupportTable(initConnection.createStatement());
        initProductTable(initConnection.createStatement());
        initEditingProductTable(initConnection.createStatement());
        initCartsTable(initConnection.createStatement());
        initCategoriesTable(initConnection.createStatement());
        initDiscountTable(initConnection.createStatement());
        initOffTable(initConnection.createStatement());
        initEditingOffTable(initConnection.createStatement());
        initLogTable(initConnection.createStatement());
        initScoreTable(initConnection.createStatement());
        initCommentsTable(initConnection.createStatement());
        initTimeLapseTable(initConnection.createStatement());
        initMarketCreditTable(initConnection.createStatement());
        removeTempAccountsFromCarts(initConnection);
/*        DiscountTable.removeOutDatedDiscount();
        OffTable.removeOutDatedDiscount();*/


        isDBInit = true;
        initConnection.close();
    }

    private static void initMarketCreditTable(Statement statement) throws SQLException, ClassNotFoundException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'MarketCredit'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE MarketCredit(" +
                    "Specifier varchar (100)," +
                    "Holder double," +
                    "primary key(Specifier)" +
                    ");");
            String sql = "INSERT INTO MarketCredit(Specifier, Holder) VALUES(?,?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "Minimum Wallet");
            preparedStatement.setDouble(2, 500);
            preparedStatement.execute();
            preparedStatement.setString(1, "Market Wage");
            preparedStatement.setDouble(2, 5);
            preparedStatement.execute();
        }
        statement.close();
        resultSet.close();
    }

    private static void initTimeLapseTable(Statement statement) throws SQLException, ClassNotFoundException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'TimeLapse'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE TimeLapse(" +
                    "ID varchar (16)," +
                    "StartDate DATE," +
                    "FinishDate DATE," +
                    "primary key(ID)" +
                    ");");
            String sql = "INSERT INTO TimeLapse(ID, StartDate, FinishDate) VALUES(?,?,?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
            preparedStatement.setString(1, "Ya Zahra");
            Date date = new Date(new java.util.Date(120, Calendar.MAY,21,0,0,0).getTime());
            preparedStatement.setDate(2, date);
            date = new Date(new java.util.Date(120, Calendar.MAY, 26, 0, 0, 0).getTime());
            preparedStatement.setDate(3, date);
            preparedStatement.execute();
        }
        statement.close();
        resultSet.close();
    }

    private static void initCommentsTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Comments'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE Comments(" +
                    "CommentID varchar(8)," +
                    "ProductID varchar(8)," +
                    "Title varchar(16)," +
                    "Content varchar(100)," +
                    "Status int," +
                    "CustomerUsername varchar(16));");
        }
        statement.close();
        resultSet.close();
    }

    private static void initScoreTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Scores'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE Scores(" +
                    "ProductID varchar(16)," +
                    "CustomerUsername varchar(16)," +
                    "Score int);");
        }
        statement.close();
        resultSet.close();
    }

    private static void initEditingOffTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'EditingOffs'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE EditingOffs(" +
                    "OffID varchar(16)," +
                    "OffName varchar(16)," +
                    "ProductID varchar(16)," +
                    "Status int," +
                    "StartDate DATE," +
                    "FinishDate DATE," +
                    "OffPercent double," +
                    "VendorUsername varchar(16));");
        }
        statement.close();
        resultSet.close();
    }

    private static void removeTempAccountsFromCarts(Connection initConnection) throws SQLException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ? ";
        PreparedStatement preparedStatement = initConnection.prepareStatement(command);
        preparedStatement.setString(1, "temp");
        preparedStatement.execute();
        preparedStatement.close();
    }

    private static void initLogTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Logs'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE Logs("+
                    "ProductID varchar(16)," +
                    "CustomerUsername varchar(16)," +
                    "Count int," +
                    "Amount double," +
                    "DATE DATE," +
                    "InitPrice double," +
                    "OffPrice double," +
                    "DiscountPercent double(100)," +
                    "Status int," +
                    "LogID varchar(16)," +
                    "IsCountable BIT," +
                    "VendorUsername varchar(16)" +
                    ");");
        }
        statement.close();
        resultSet.close();
    }

    private static void initOffTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Offs'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE Offs(" +
                    "OffID varchar(16)," +
                    "OffName varchar(16)," +
                    "ProductID varchar(16)," +
                    "Status int," +
                    "StartDate DATE," +
                    "FinishDate DATE," +
                    "OffPercent double," +
                    "VendorUsername varchar(16));");
        }
        statement.close();
        resultSet.close();
    }

    private static void initDiscountTable(Statement statement) throws SQLException, ClassNotFoundException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Discounts'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE Discounts(" +
                    "ID varchar (8)," +
                    "Code varchar(16)," +
                    "StartDate DATE," +
                    "FinishDate DATE," +
                    "DiscountPercent double(100)," +
                    "MaxDiscount double," +
                    "Repetition int," +
                    "MaxRepetition int," +
                    "CustomerUsername varchar(16)" +
                    ");");
        }
        statement.close();
        resultSet.close();
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
            preparedStatement.setString(1, "All Products");
            preparedStatement.setString(2, "This category contains whole products that exist in store");
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
                    "ScoreNum int," +
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
                    "ScoreNum int," +
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

    private static void initSupportTable(Statement statement) throws SQLException {
        String command = "SELECT name from sqlite_master WHERE type = 'table' AND name = 'Supporters'";
        ResultSet resultSet = statement.executeQuery(command);
        if(!resultSet.next()) {
            statement.execute("CREATE TABLE Supporters(" +
                    "Username varchar (16)," +
                    "Password varchar (16)," +
                    "FirstName varchar (25)," +
                    "LastName varchar (25)," +
                    "primary key(Username)" +
                    ");");
        }

        statement.close(); resultSet.close();
    }

}
