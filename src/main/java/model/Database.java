package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static String localDBUrl = "jdbc:sqlite:database\\database.sqlite";
    public static Connection getConnection ()
    {
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
}
