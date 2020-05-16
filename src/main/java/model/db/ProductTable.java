package model.db;

import model.existence.Product;

import java.sql.Date;
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
        //System.out.println(ID);
        String queryTask = "SELECT * FROM Products WHERE ID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(queryTask);
        preparedStatement.setString(1, ID);
        //System.out.println("Hello World");
        Product product = new Product(preparedStatement.executeQuery());
        //System.out.println(product.getName() + " : " + product.getID());
        return product;
    }

    public static void removeProductByID(String ID) throws SQLException, ClassNotFoundException {
        String task = "DELETE FROM Products WHERE ID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, ID);
        preparedStatement.execute();
    }

    public static boolean isIDFree(String productId) throws SQLException, ClassNotFoundException
    {
        String command = "SELECT * FROM Products WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productId);
        return !(preparedStatement.executeQuery().next());
    }

    public static void setProductStatus(String ID, int status) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Products SET Status = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, status);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static ArrayList<Product> getAllUnApprovedProducts() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Products WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 2);
        ArrayList<Product> products = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            products.add(new Product(resultSet));
        }
        return products;
    }

    public static ArrayList<Product> getAllShowingProducts() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Products WHERE Status = ? OR Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 1);
        preparedStatement.setInt(2, 3);
        ArrayList<Product> products = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            products.add(new Product(resultSet));
        }
        return products;
    }

    public static void setProductApprovalDate(String productId) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Products SET ApprovalDate = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
        preparedStatement.setString(2, productId);
        preparedStatement.execute();
    }

    public static ArrayList<Product> getProductsWithCategory(String categoryName) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        String command = "SELECT * FROM Products WHERE Category = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, categoryName);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
            products.add(new Product(resultSet));
        return products;
    }

    public static void changeProductCategoryByID(String ID, String category) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Products SET Category = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, category);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static boolean isThereProductWithSpecificCategory(String categoryName) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Products WHERE Category = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, categoryName);
        return preparedStatement.executeQuery().next();
    }

    public static void addSeenToProductWithID(String productID) throws SQLException, ClassNotFoundException {
        int firstSeen = ProductTable.getProductByID(productID).getSeen();
        String command = "UPDATE Products SET Seen = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, (firstSeen + 1));
        preparedStatement.setString(2, productID);
        preparedStatement.execute();
        if(!EditingProductTable.isIDFree(productID))
        {
            command = "UPDATE EditingProducts SET Seen = ? WHERE ID = ?";
            preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setInt(1, (firstSeen + 1));
            preparedStatement.setString(2, productID);
            preparedStatement.execute();
        }
    }

    public static String getVendorName(String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Products WHERE ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString("SellerUsername");
    }
}
