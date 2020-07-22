package server.server.bank.sapahbank;

import server.controller.Lock;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class SapahDB {
    private static boolean isDBInit = false;
    private static Connection connection = null;
    private static final String localDBUrl = "jdbc:sqlite:database\\bankdatabase.sqlite";

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
        initAccountsTable(initConnection.createStatement());
        initAuthTokens(initConnection.createStatement());
        initReceipts(initConnection.createStatement());
        isDBInit = true;
        initConnection.close();
    }

    private static void initReceipts(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Receipts'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE Receipts(" +
                    "ReceiptID varchar(10)," +
                    "ReceiptType varchar(25)," +
                    "Description varchar(100)," +
                    "Money double," +
                    "SourceID varchar(10)," +
                    "DestID varchar (10)," +
                    "Paid BIT," +
                    "primary key(ReceiptID));");
        }
        statement.close();
        resultSet.close();
    }

    private static void initAuthTokens(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'AuthTokens'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
            statement.execute("CREATE TABLE AuthTokens(" +
                    "Auth varchar(20)," +
                    "AccountID varchar(10)," +
                    "CreateDate DATE," +
                    "primary key(Auth));");
        }
        statement.close();
        resultSet.close();
    }

    private static void initAccountsTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Accounts'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()) {
            statement.execute("CREATE TABLE Accounts(" +
                    "AccountID varchar(10)," +
                    "Username varchar(16)," +
                    "Password varchar(16)," +
                    "FirstName varchar(25)," +
                    "LastName varchar(25)," +
                    "Balance double," +
                    "primary key(AccountID));");
        }
        statement.close();
        resultSet.close();
    }
    //Finish Init

    public synchronized static void createAccount(String accountID, String firstName, String lastName, String username, String password) throws SQLException, ClassNotFoundException {
        synchronized (Lock.ACCOUNT_LOCK) {
            String command = "INSERT INTO Accounts (AccountID, Username, Password, FirstName, LastName, Balance) Values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, accountID);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, firstName);
            preparedStatement.setString(5, lastName);
            preparedStatement.setDouble(6, 0);
            preparedStatement.execute();
        }
    }

    public static boolean isThereAccountWithID(String accountID) throws SQLException, ClassNotFoundException {
        synchronized (Lock.ACCOUNT_LOCK) {
            String command = "SELECT * FROM Accounts WHERE AccountID = ?;";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, accountID);
            return preparedStatement.executeQuery().next();
        }
    }

    public static boolean isThereAccountWithUsername(String username) throws SQLException, ClassNotFoundException {
        synchronized (Lock.ACCOUNT_LOCK) {
            String command = "SELECT * FROM Accounts WHERE Username = ?;";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, username);
            return preparedStatement.executeQuery().next();
        }
    }

    public static String getPasswordWithUsername(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT Password FROM Accounts WHERE Username = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        return preparedStatement.executeQuery().getString("Password");
    }

    public static String getAccountIDWithUsername(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT AccountID FROM Accounts WHERE Username = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        return preparedStatement.executeQuery().getString("AccountID");
    }

    public static boolean isThereAuth(String auth) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM AuthTokens WHERE Auth = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, auth);
        return preparedStatement.executeQuery().next();
    }

    public static void addAuth(String authToken, String accountID) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO AuthTokens (Auth, AccountID, CreateDate) Values (?, ?, ?);";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, authToken);
        preparedStatement.setString(2, accountID);
        preparedStatement.setDate(3, new Date(System.currentTimeMillis()));
        preparedStatement.execute();
    }

    public static Date getAuthDateWithAuth(String auth) throws SQLException, ClassNotFoundException {
        String command = "SELECT CreateDate FROM AuthTokens WHERE Auth = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, auth);
        return preparedStatement.executeQuery().getDate("CreateDate");
    }

    public static boolean isAccountIDValid(String accountID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE AccountID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, accountID);
        return preparedStatement.executeQuery().next();
    }

    public static String getAccountIDWithAuth(String auth) throws SQLException, ClassNotFoundException {
        String command = "SELECT AccountID FROM AuthTokens WHERE Auth = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, auth);
        return preparedStatement.executeQuery().getString("AccountID");
    }

    public static void createReceipt(String receiptID, String receiptType, String money, String sourceID, String destID, String description) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Receipts (ReceiptID, ReceiptType, Description, Money, SourceID, DestID, Paid) Values (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, receiptID);
        preparedStatement.setString(2, receiptType);
        preparedStatement.setString(3, description);
        preparedStatement.setDouble(4, Double.parseDouble(money));
        preparedStatement.setString(5, sourceID);
        preparedStatement.setString(6, destID);
        preparedStatement.setBoolean(7, false);
        preparedStatement.execute();
    }

    public static boolean isThereReceiptWithID(String receiptID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Receipts WHERE ReceiptID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, receiptID);
        return preparedStatement.executeQuery().next();
    }

    public static Receipt getReceiptWithID(String receiptID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Receipts WHERE ReceiptID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, receiptID);
        return new Receipt(preparedStatement.executeQuery());
    }

    public static ArrayList<Receipt> getDestIDReceipts(String accountID) throws SQLException, ClassNotFoundException {
        ArrayList<Receipt> receipts = new ArrayList<>();
        String command = "SELECT * FROM Receipts WHERE DestID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, accountID);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            receipts.add(new Receipt(resultSet));
        }

        return receipts;
    }

    public static ArrayList<Receipt> getSourceIDReceipts(String accountID) throws SQLException, ClassNotFoundException {
        ArrayList<Receipt> receipts = new ArrayList<>();
        String command = "SELECT * FROM Receipts WHERE SourceID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, accountID);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            receipts.add(new Receipt(resultSet));
        }

        return receipts;
    }

    public static boolean isReceiptPaid(String receiptID) throws SQLException, ClassNotFoundException {
        String command = "SELECT Paid FROM Receipts WHERE ReceiptID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, receiptID);
        return preparedStatement.executeQuery().getBoolean("Paid");
    }

    public static double getMoneyWithAccountID(String accountID) throws SQLException, ClassNotFoundException {
        synchronized (Lock.TRANSACTION_LOCK) {
            String command = "SELECT Balance FROM Accounts WHERE AccountID = ?;";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, accountID);
            return preparedStatement.executeQuery().getDouble("Balance");
        }
    }

    public static void addMoney(String accountID, double money) throws SQLException, ClassNotFoundException {
        synchronized (Lock.TRANSACTION_LOCK) {
            String command = "UPDATE Accounts SET Balance = ? WHERE AccountID = ?";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setDouble(1, getMoneyWithAccountID(accountID) + money);
            preparedStatement.setString(2, accountID);
            preparedStatement.execute();
        }
    }

    public static void subtractMoney(String accountID, double money) throws SQLException, ClassNotFoundException {
        synchronized (Lock.TRANSACTION_LOCK) {
            String command = "UPDATE Accounts SET Balance = ? WHERE AccountID = ?";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setDouble(1, getMoneyWithAccountID(accountID) - money);
            preparedStatement.setString(2, accountID);
            preparedStatement.execute();
        }
    }
}
