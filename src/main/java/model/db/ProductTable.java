package model.db;

import model.existence.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductTable extends Database {
    public static ArrayList<Product> getAllProducts() throws SQLException, ClassNotFoundException {
        String queryTask = "SELECT * FROM Products;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(queryTask);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Product> allProducts = new ArrayList<Product>();

        while (resultSet.next())
            allProducts.add(new Product(resultSet));

        return allProducts;
    }

    public static Product getProductByID(String ID) throws SQLException, ClassNotFoundException {
        String queryTask = "SELECT * FROM Products WHERE ID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(queryTask);
        preparedStatement.setString(1, ID);
        return new Product(preparedStatement.executeQuery());
    }

    public static void removeProductByID(String ID) throws SQLException, ClassNotFoundException {
        String task = "DELETE FROM Products WHERE ID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, ID);
        preparedStatement.execute();
    }
}
