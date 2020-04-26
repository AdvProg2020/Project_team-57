package model;

import java.sql.*;

public class Database {
    private static boolean isInit = false;
    public static String localDBUrl = "jdbc:sqlite:database\\database.sqlite";
    public static Connection getConnection ()
    {
        if(!isInit)
            Database.initDb();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(localDBUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void initDb() {
        isInit = true;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(localDBUrl);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name from sqlite master WHERE name = 'Clients'");
            if (!resultSet.next())
            {
                statement.execute("CREATE TABLE Clients (" +
                        "ID int," +
                        "Username String," +
                        "Password String" +
                        "primary key(Username)" +
                        ");");
            }
            resultSet = statement.executeQuery("SELECT name from sqlite master WHERE name = 'Managers'");
            if(!resultSet.next())
            {
                statement.execute("CREATE TABLE Managers(" +
                        "ID int," +
                        "Username String," +
                        "Password String" +
                        "primary key(Username)" +
                        ");");
            }
            resultSet = statement.executeQuery("SELECT name from sqlite master WHERE name = 'Vendors'");
            if(!resultSet.next())
            {
                statement.execute("CREATE TABLE Vendors(" +
                        "ID int," +
                        "Username String," +
                        "Password String" +
                        "primary key(Username)" +
                        ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
