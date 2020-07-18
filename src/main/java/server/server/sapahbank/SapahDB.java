package server.server.sapahbank;

import java.io.File;
import java.sql.*;

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
                    "Auth varchar(16)," +
                    "AccountID varchar(10)," +
                    "Expired BIT," +
                    "CreatDate DATE," +
                    "primary key(Auth));");
        }
        statement.close();
        resultSet.close();
    }

    private static void initAccountsTable(Statement statement) throws SQLException {
        String command = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'Accounts'";
        ResultSet resultSet = statement.executeQuery(command);
        if (!resultSet.next()){
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

    public static void createAccount(String accountID, String firstName, String lastName, String username, String password) throws SQLException, ClassNotFoundException {
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

    public static boolean isThereAccountWithID(String accountID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE AccountID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, accountID);
        return preparedStatement.executeQuery().next();
    }

    public static boolean isThereAccountWithUsername(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE Username = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        return preparedStatement.executeQuery().next();
    }
}
