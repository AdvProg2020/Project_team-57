package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection ()
    {
        Connection connection = null;
        try {
            String localUrl = "jdbc:sqlite:database.sqlite";
            connection = DriverManager.getConnection(localUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
}
