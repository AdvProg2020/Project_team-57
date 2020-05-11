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
        String command = "UPDATE Products SET Approval Date = ? WHERE ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
        preparedStatement.setString(2, productId);
        preparedStatement.execute();
    }

    public static void tempAddProducts() throws SQLException, ClassNotFoundException {
        String task = "INSERT INTO Products (ID, Status, ProductName, IsCountable, Description) " +
                                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a1234567");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "AshkanGi");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "My Gi is so Pure");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a1234566");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "AshkanChos");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "My Chos is so Pure Too");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "s1234567");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "motherfucker");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "eifjiesfjsifjseifjisefjsiefjsifjijfifhsfuhsiufshfhishfedewfb");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a1234565");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "AshkanGooz");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "Why My Gooz is so pure");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "s2345678");
        preparedStatement.setInt(2, 2);
        preparedStatement.setString(3, "brattysisporn");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "It's a new porn from porn hub.");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a1234564");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "Ashkan An");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "oof che Ane taze ei");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "s3456789");
        preparedStatement.setInt(2, 3);
        preparedStatement.setString(3, "muslim sins");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "A shocking event");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a3456789");
        preparedStatement.setInt(2, 3);
        preparedStatement.setString(3, "ish pish");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "fuck baba");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a3423789");
        preparedStatement.setInt(2, 3);
        preparedStatement.setString(3, "kire khar");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "man mikham");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a3423722");
        preparedStatement.setInt(2, 3);
        preparedStatement.setString(3, "kire gav");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "ey baba");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "s4567891");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "lena fucking paul");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "She is a fucking ghanbar");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "a4567891");
        preparedStatement.setInt(2, 1);
        preparedStatement.setString(3, "lana rhodes");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "he he");
        preparedStatement.execute();
        preparedStatement = getConnection().prepareStatement(task);
        preparedStatement.setString(1, "s5678912");
        preparedStatement.setInt(2, 2);
        preparedStatement.setString(3, "karla kush");
        preparedStatement.setBoolean(4, true);
        preparedStatement.setString(5, "She is my favorite. She is the best. She She She ...");
        preparedStatement.execute();
    }
}
