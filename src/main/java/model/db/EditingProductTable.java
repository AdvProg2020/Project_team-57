package model.db;

import model.existence.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditingProductTable extends Database{

    public static Product getEditingProductWithID(String productID) throws SQLException, ClassNotFoundException
    {
        String command = "SELECT * FROM EditingProducts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        return new Product(preparedStatement.executeQuery());
    }

    public static boolean isIDFree(String productId) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM EditingProducts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productId);
        return !(preparedStatement.executeQuery().next());
    }

    public static void updateCountableProduct(Product product) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingProducts SET Price = ?, " +
                "ProductName = ?, " +
                "Category = ?, " +
                "Count = ?, " +
                "Brand = ?, " +
                "Description = ? " +
                "WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, product.getPrice());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setString(3, product.getCategory());
        preparedStatement.setInt(4, product.getCount());
        preparedStatement.setString(5, product.getBrand());
        preparedStatement.setString(6, product.getDescription());
        preparedStatement.setString(7, product.getID());
        preparedStatement.execute();
    }

    public static void updateUnCountableProduct(Product product) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingProducts SET Price = ?, " +
                "ProductName = ?, " +
                "Category = ?, " +
                "Amount = ?, " +
                "Brand = ?, " +
                "Description = ? " +
                "WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, product.getPrice());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setString(3, product.getCategory());
        preparedStatement.setDouble(4, product.getAmount());
        preparedStatement.setString(5, product.getBrand());
        preparedStatement.setString(6, product.getDescription());
        preparedStatement.setString(7, product.getID());
        preparedStatement.execute();
    }

    public static void editFieldWithName(String ID, String fieldName, String newValue) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingProducts SET " + fieldName + " = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, newValue);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static void changeProductCount(String ID, int newValue) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingProducts SET Count = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, newValue);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static void changeProductAmount(String ID, double newValue) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingProducts SET Amount = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, newValue);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static void changeProductPrice(String ID, double newValue) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingProducts SET Price = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, newValue);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static void addProduct(Product product) throws SQLException, ClassNotFoundException {
        String command =  "INSERT INTO EditingProducts (ID, ProductName, Brand, SellerUsername, Count, Amount, " +
                "IsCountable, Category, Description, Price, AverageScore, Status, ApprovalDate, Seen, ScoreNum) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, product.getID());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setString(3, product.getBrand());
        preparedStatement.setString(4, product.getSellerUserName());
        if (product.isCountable()){
            preparedStatement.setInt(5, product.getCount());
            preparedStatement.setDouble(6, 0.0);
            preparedStatement.setBoolean(7, true);
        } else {
            preparedStatement.setInt(5, 0);
            preparedStatement.setDouble(6, product.getAmount());
            preparedStatement.setBoolean(7, false);
        }
        preparedStatement.setString(8, product.getCategory());
        preparedStatement.setString(9, product.getDescription());
        preparedStatement.setDouble(10, product.getPrice());
        preparedStatement.setDouble(11, product.getAverageScore());
        preparedStatement.setInt(12, 3);
        preparedStatement.setDate(13, product.getApprovalDate());
        preparedStatement.setInt(14, product.getSeen());
        preparedStatement.setInt(15, product.getScoreNum());
        preparedStatement.execute();
    }

    public static ArrayList<Product> getAllEditingProducts() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM EditingProducts";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Product> allEditingProducts = new ArrayList<>();
        while (resultSet.next()){
            allEditingProducts.add(new Product(resultSet));
        }
        return allEditingProducts;
    }

    public static void removeProductById(String id) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM EditingProducts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        preparedStatement.execute();
    }

    public static String getEditingProductImageFilePath(String productID, int number) {
        String fileName = "database\\Images\\EditingProducts\\" + productID + "\\" + number;
        String[] validImageExtensions = {"jpg" , "jpeg" , "png", "bmp"};
        for (String validImageExtension : validImageExtensions) {
            String filePath = fileName + "." + validImageExtension;
            if(new File(filePath).exists())
                return filePath;
        }
        return null;
    }

    public static FileInputStream getEditingProductImageInputStream(String ID, int number) throws FileNotFoundException {
        return new FileInputStream(getEditingProductImageFilePath(ID, number));
    }

    public static void addImage(String productID, int productNumber, File pictureFile) throws IOException {
        File productFolder = new File("database\\Images\\EditingProducts\\" + productID );
        if(!productFolder.exists())
            productFolder.mkdir();
        String[] splitPath = pictureFile.getPath().split("\\.");
        String fileExtension = splitPath[splitPath.length - 1];
        File saveImage = new File("database\\Images\\EditingProducts\\" + productID + "\\" + productNumber + "." + fileExtension);
        Files.copy(pictureFile.toPath(), saveImage.toPath());
    }

    public static void deleteEditingProductImageFolder(String productID) {
        File folder = new File("database\\Images\\EditingProducts\\" + productID );
        folder.delete();
    }
}
