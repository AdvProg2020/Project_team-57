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

        isDBInit = true;
        initConnection.close();
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
                    "primary key(Username)" +
                    ");");
        }

        statement.close(); resultSet.close();
    }

}
