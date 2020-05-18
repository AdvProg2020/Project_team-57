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

    public static void reduceProductAmount(String productID, double amount) throws SQLException, ClassNotFoundException {
        double firstAmount = ProductTable.getProductByID(productID).getAmount();
        String command = "UPDATE Products SET Amount = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, firstAmount - amount);
        preparedStatement.setString(2, productID);
        preparedStatement.execute();
    }

    public static void reduceProductCount(String productID, int count) throws SQLException, ClassNotFoundException {
        int firstCount = ProductTable.getProductByID(productID).getCount();
        String command = "UPDATE Products SET Count = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, firstCount - count);
        preparedStatement.setString(2, productID);
        preparedStatement.execute();
    }

    public static ArrayList<Integer> getAllScores(String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Scores WHERE ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Integer> allScores = new ArrayList<>();
        while (resultSet.next()) {
            allScores.add(resultSet.getInt("Score"));
        }
        return allScores;
    }

    public static boolean didScore(String username, String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Scores WHERE CustomerUsername = ? AND ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, productID);
        return preparedStatement.executeQuery().next();
    }

    public static int getScore(String username, String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Scores WHERE CustomerUsername = ? AND ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, productID);
        return preparedStatement.executeQuery().getInt("Score");
    }

    public static void updateScore(String username, String productID, int score) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Scores SET Score = ? WHERE CustomerUsername = ? AND ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, score);
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, productID);
        preparedStatement.execute();
    }

    public static void setScore(String username, String productID, int score) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Scores(ProductID, CustomerUsername, Score) VALUES(?,?,?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        preparedStatement.setString(2, username);
        preparedStatement.setInt(3, score);
        preparedStatement.execute();
    }

    public static void updateProductsAvgScore(String productID) throws SQLException, ClassNotFoundException {
        double avgSc = 0;
        int size = 0;
        for (Integer score : getAllScores(productID)) {
            ++size;
            avgSc += score;
        }
        avgSc /= size;
        String command = "UPDATE Products SET AverageScore = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, avgSc);
        preparedStatement.setString(2, productID);
        preparedStatement.execute();
    }

}

