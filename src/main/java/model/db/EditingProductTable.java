package model.db;

import model.existence.Product;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditingProductTable extends Database{

    public Product getEditingProductWithID(String productID) throws SQLException, ClassNotFoundException
    {
        String command = "SELECT * FROM EditingProducts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        return new Product(preparedStatement.executeQuery());
    }

    public static boolean isIDFree(String productId) throws SQLException, ClassNotFoundException
    {
        String command = "SELECT * FROM EditingProducts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productId);
        return !(preparedStatement.executeQuery().next());
    }

    public static void addProduct(Product product)

}
