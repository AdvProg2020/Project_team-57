package model.db;

import model.existence.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartTable extends Database{
    public static void addToCartCountable(String username, String id, int count) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Carts (ID, CustomerUsername, Count) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, username);
        preparedStatement.setInt(3, count);
        preparedStatement.execute();
    }

    public static void addToCartUnCountable(String username, String id, double amount) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Carts (ID, CustomerUsername, Amount) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, username);
        preparedStatement.setDouble(3, amount);
        preparedStatement.execute();
    }

    public static void addTempToUsername(String customerUsername) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, customerUsername);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
        {
            String nextCommand = "INSERT INTO Carts (ID, CustomerUsername, Count, Amount) VALUES (?, ?, ?, ?)";
            preparedStatement = getConnection().prepareStatement(nextCommand);
            preparedStatement.setString(1, resultSet.getString("ID"));
            preparedStatement.setString(2, resultSet.getString("CustomerUsername"));
            preparedStatement.setInt(3, resultSet.getInt("Count"));
            preparedStatement.setDouble(4, resultSet.getDouble("Amount"));
            preparedStatement.execute();
        }
    }

    public static void removeTemp() throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "temp");
        preparedStatement.execute();
    }

    public static ArrayList<Product> getAllCartWithUsername(String customerUsername) throws SQLException, ClassNotFoundException {
        ArrayList<Product> cartProducts = new ArrayList<>();
        String command = "SELECT * FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, customerUsername);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
        {
            Product product = ProductTable.getProductByID(resultSet.getString("ID"));
            if(product.isCountable())
                product.setCount(resultSet.getInt("Count"));
            else
                product.setAmount(resultSet.getDouble("Amount"));
            cartProducts.add(product);
        }
        return cartProducts;
    }

    public static Product getCartProductByID(String customerUsername, String ID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Carts WHERE CustomerUsername = ? AND ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, customerUsername);
        preparedStatement.setString(2, ID);
        ResultSet resultSet = preparedStatement.executeQuery();
        Product product = ProductTable.getProductByID(ID);
        if(product.isCountable())
            product.setCount(resultSet.getInt("Count"));
        else
            product.setAmount(resultSet.getDouble("Amount"));
        return product;
    }
}
