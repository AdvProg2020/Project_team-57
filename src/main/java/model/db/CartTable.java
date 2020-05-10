package model.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CartTable extends Database{
    public static void addToCartCountable(String username, String id, int count) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Carts (ID, Username, Count) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, username);
        preparedStatement.setInt(3, count);
        preparedStatement.execute();
    }

    public static void addToCartUnCountable(String username, String id, double amount) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Carts (ID, Username, Amount) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, username);
        preparedStatement.setDouble(3, amount);
        preparedStatement.execute();
    }

}
