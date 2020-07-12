package server.model.db;

import server.model.existence.Product;

import java.io.*;
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

    public static boolean deleteEditingProductImageFolder(String productID) {
        File folder = new File("database\\Images\\EditingProducts\\" + productID );
        return folder.delete();
    }

    public static void deleteImage(String productID, int imageNumber) throws IOException {
        File deletingImage = new File(String.valueOf(getEditingProductImageFilePath(productID, imageNumber)));
        deletingImage.delete();
    }

    public static void renameImage(String productID, int number, File file) {
        String[] splitPath = file.getPath().split("\\.");
        String fileExtension = splitPath[splitPath.length - 1];
        File renamedFile = new File("database\\Images\\EditingProducts\\" + productID + "\\" + number + "." + fileExtension);
        file.renameTo(renamedFile);
    }

    public static ArrayList<File> copyEditingProductNewImagesInTemp(String productID, ArrayList<File> productImageFiles) throws IOException {
        ArrayList<File> pictureFiles = new ArrayList<>();
        File productOriginFolder = new File("database\\Images\\EditingProducts\\" + productID);
        productOriginFolder.mkdir();
        File productFolder = new File("database\\Images\\EditingProducts\\" + productID + "\\Jesus");
        if(!productFolder.exists())
            productFolder.mkdir();
        int i = 1;
        for (File pictureFile : productImageFiles) {
            String[] splitPath = pictureFile.getPath().split("\\.");
            String fileExtension = splitPath[splitPath.length - 1];
            File saveImage = new File("database\\Images\\EditingProducts\\" + productID + "\\Jesus\\" + (i++) + "." + fileExtension);
            pictureFiles.add(new File(String.valueOf(Files.copy(pictureFile.toPath(), saveImage.toPath()))));
        }
        return pictureFiles;
    }

    public static void removeEditingProductTempImages(String productID) {
        File folder = new File("database\\Images\\EditingProducts\\" + productID);
        folder.mkdir();
        folder = new File("database\\Images\\EditingProducts\\" + productID + "\\Jesus");
        folder.mkdir();
        File tempFolder = new File("database\\Images\\EditingProducts\\" + productID + "\\Jesus");
        String[] entries = tempFolder.list();
        for(String s: entries){
            File currentFile = new File(tempFolder.getPath(),s);
            currentFile.delete();
        }
    }

    public static void transferEditingImages(String productID) throws IOException {
        ProductTable.removeAllProductImages(productID);
        File tempFolder = new File("database\\Images\\EditingProducts\\" + productID);
        String[] entries = tempFolder.list();
        int i = 0;
        for(String s: entries){
            File currentFile = new File(tempFolder.getPath(),s);
            if(!currentFile.getName().equals("Jesus")) {
                ProductTable.addImage(productID, (++i), currentFile);
            }
        }
        EditingProductTable.removeAllEditingProductImages(productID);
    }

    public static void removeAllEditingProductImages(String productID) {
        File file = new File("database\\Images\\EditingProducts\\" + productID);
        file.mkdir();
        File tempFolder = new File("database\\Images\\EditingProducts\\" + productID);
        String[] entries = tempFolder.list();
        for(String s: entries){
            File currentFile = new File(tempFolder.getPath(),s);
            currentFile.delete();
        }
    }

    public static FileOutputStream getEditingProductImageOutputStream(String productID, String fileExtension, int number) throws IOException {
        String fileName = "database\\Images\\EditingProducts\\" + productID + "\\" + number + "." + fileExtension;
        File pictureFile = new File(fileName);
        if(!pictureFile.exists()) {
            pictureFile.createNewFile();
            return new FileOutputStream(pictureFile);
        } else {
            System.out.println("Error IN #getEditingProductImageOutputStream");
            return null;
        }
    }
}
