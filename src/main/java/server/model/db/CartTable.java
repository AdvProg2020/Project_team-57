package server.model.db;

import server.model.existence.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartTable extends Database {
    public static CartTable getInstance() {
        return new CartTable();
    }

    public void addToCartCountable(String username, String id, int count) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Carts (ID, CustomerUsername, Count) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, username);
        preparedStatement.setInt(3, count);
        preparedStatement.execute();
    }

    public void addToCartUnCountable(String username, String id, double amount) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Carts (ID, CustomerUsername, Amount) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, username);
        preparedStatement.setDouble(3, amount);
        preparedStatement.execute();
    }

    public void addTempToUsername(String customerUsername) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "temp");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
        {
            if(!isThereCartProductForUsername(customerUsername, resultSet.getString("ID")))
            {
                String nextCommand = "INSERT INTO Carts (ID, CustomerUsername, Count, Amount) VALUES (?, ?, ?, ?)";
                preparedStatement = getConnection().prepareStatement(nextCommand);
                preparedStatement.setString(1, resultSet.getString("ID"));
                preparedStatement.setString(2, customerUsername);
                preparedStatement.setInt(3, resultSet.getInt("Count"));
                preparedStatement.setDouble(4, resultSet.getDouble("Amount"));
                preparedStatement.execute();
            }
        }
    }

    public void removeTemp() throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "temp");
        preparedStatement.execute();
    }

    public ArrayList<Product> getAllCartWithUsername(String customerUsername) throws SQLException, ClassNotFoundException {
        ArrayList<Product> cartProducts = new ArrayList<>();
        String command = "SELECT * FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, customerUsername);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
        {
            Product product = ProductTable.getInstance().getProductByID(resultSet.getString("ID"));
            if(product.isCountable())
                product.setCount(resultSet.getInt("Count"));
            else
                product.setAmount(resultSet.getDouble("Amount"));
            cartProducts.add(product);
        }
        return cartProducts;
    }

    public Product getCartProductByID(String customerUsername, String ID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Carts WHERE CustomerUsername = ? AND ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, customerUsername);
        preparedStatement.setString(2, ID);
        ResultSet resultSet = preparedStatement.executeQuery();
        Product product = ProductTable.getInstance().getProductByID(ID);
        if(product.isCountable())
            product.setCount(resultSet.getInt("Count"));
        else
            product.setAmount(resultSet.getDouble("Amount"));
        return product;
    }

    public void modifyCartProductCounts(String username, String productID, int input) throws SQLException, ClassNotFoundException {
        int firstCount = getCartProductByID(username, productID).getCount();
        String command = "UPDATE Carts SET Count = ? Where CustomerUsername = ? AND ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, (firstCount + input));
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, productID);
        preparedStatement.execute();
    }

    public void modifyCartProductAmount(String username, String productID, double input) throws SQLException, ClassNotFoundException {
        double firstAmount = getCartProductByID(username, productID).getAmount();
        String command = "UPDATE Carts SET Amount = ? Where CustomerUsername = ? AND ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, (firstAmount + input));
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, productID);
        preparedStatement.execute();
    }

    public boolean isThereCartProductForUsername(String username, String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Carts WHERE CustomerUserName = ? And ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, productID);
        return preparedStatement.executeQuery().next();
    }

    public void deleteCartProduct(String username, String id) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ? And ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, id);
        preparedStatement.execute();
    }

    public void deleteCustomerCart(String username) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.execute();
    }

    public void deleteProductFromCarts(String productId) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Carts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productId);
        preparedStatement.execute();
    }

    public void removeAllCustomerCartProducts(String username) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Carts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.execute();
    }
}
