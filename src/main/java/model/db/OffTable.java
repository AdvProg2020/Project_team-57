package model.db;

import model.existence.Off;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OffTable extends Database{
    public static void addOff(Off off) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Offs(OffID, OffName, ProductID, Status, StartDate, FinishDate, OffPercent, " +
                "VendorUsername) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        for (String productID : off.getProductIDs()) {
            preparedStatement.setString(1, off.getOffID());
            preparedStatement.setString(2, off.getOffName());
            preparedStatement.setString(3, productID);
            preparedStatement.setInt(4, off.getStatus());
            preparedStatement.setDate(5, off.getStartDate());
            preparedStatement.setDate(6, off.getFinishDate());
            preparedStatement.setDouble(7, off.getOffPercent());
            preparedStatement.setString(8, off.getVendorUsername());
            preparedStatement.execute();
        }
    }

    public static Off getSpecificOff(String offID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        return Off.makeOffByID(preparedStatement.executeQuery());
    }

    public static ArrayList<Off> getVendorOffs(String username) throws SQLException, ClassNotFoundException {
        ArrayList<Off> offs = new ArrayList<>();
        String command = "SELECT DISTINCT OffID FROM Offs WHERE VendorUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next())
        {
            offs.add(getSpecificOff(resultSet.getString("OffID")));
        }
        return offs;
    }

    public static void editOffName(Off off, String newOffName) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Offs SET OffName = ? WHERE OffIDs = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, newOffName);
        preparedStatement.setString(2, off.getOffID());
        preparedStatement.execute();
    }

    public static Date getStartDate(String offID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getDate("StartDate");
    }

    public static Date getFinishDate(String offID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getDate("FinishDate");
    }

    public static double getOffPercentByProductID(String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT OffPercent FROM Offs WHERE ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.getDouble("OffPercent");
    }

    public static void editFinishDate(String offID, Date newFinishDate) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Offs SET FinishDate = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDate(1, newFinishDate);
        preparedStatement.setString(2, offID);
        preparedStatement.execute();
    }

    public static void editOffPercent(String offID, double percent) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Offs SET OffPercent = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, percent);
        preparedStatement.setString(2, offID);
        preparedStatement.execute();
    }

    public static boolean isThereProductInOff(String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE ProductID = ? AND Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        preparedStatement.setInt(2, 1);
        return preparedStatement.executeQuery().next();
    }

    public static boolean isThereProductInOffIgnoreStatus(String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        return preparedStatement.executeQuery().next();
    }

    public static ArrayList<Off> getAllUnApprovedOffs() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffID FROM Offs WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 2);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Off> unApprovedOffs = new ArrayList<>();
        while (resultSet.next()) {
            unApprovedOffs.add(getSpecificOff(resultSet.getString(resultSet.getString("OffID"))));
        }
        return unApprovedOffs;
    }

    public static ArrayList<String> getAllUnApprovedOffNames() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffName FROM Offs WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 2);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<String> allUnApprovedOffNames = new ArrayList<>();
        while (resultSet.next()){
            allUnApprovedOffNames.add(resultSet.getString("OffName"));
        }
        return allUnApprovedOffNames;
    }

    public static ArrayList<String> getAllUnApprovedOffIDs() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffID FROM Offs WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 2);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<String> allUnApprovedOffIDs = new ArrayList<>();
        while (resultSet.next()){
            allUnApprovedOffIDs.add(resultSet.getString("OffID"));
        }
        return allUnApprovedOffIDs;
    }

    public static void removeOffByID(String offID) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Offs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        preparedStatement.execute();
    }

    public static void approveOffByID(String offID) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Offs SET Status = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(2, offID);
        preparedStatement.setInt(1, 1);
        preparedStatement.execute();
    }

    public static boolean isThereOffWithID(String offID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        return preparedStatement.executeQuery().next();
    }

    public static boolean isThereEditingOffWithID(String offID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM EditingOffs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        return preparedStatement.executeQuery().next();
    }

    public static void editEditingOffName(String offID, String offName) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingOffs SET OffName = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offName);
        preparedStatement.setString(2, offID);
        preparedStatement.execute();
    }

    public static void changeOffStatus(String offID, int status) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Offs SET Status = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(2, offID);
        preparedStatement.setInt(1, status);
        preparedStatement.execute();
    }

    public static void addEditingOff(Off off) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO EditingOffs(OffID, OffName, ProductID, Status, StartDate, FinishDate, OffPercent, " +
                "VendorUsername) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        for (String productID : off.getProductIDs()) {
            preparedStatement.setString(1, off.getOffID());
            preparedStatement.setString(2, off.getOffName());
            preparedStatement.setString(3, productID);
            preparedStatement.setInt(4, off.getStatus());
            preparedStatement.setDate(5, off.getStartDate());
            preparedStatement.setDate(6, off.getFinishDate());
            preparedStatement.setDouble(7, off.getOffPercent());
            preparedStatement.setString(8, off.getVendorUsername());
            preparedStatement.execute();
        }
    }

    public static Off getSpecificEditingOffByID(String offID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM EditingOffs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        return Off.makeOffByID(preparedStatement.executeQuery());
    }

    public static void editEditingOffFinishDate(String offID, Date date) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingOffs SET FinishDate = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDate(1, date);
        preparedStatement.setString(2, offID);
        preparedStatement.execute();
    }

    public static void editEditingOffPercent(String offID, Double percent) throws SQLException, ClassNotFoundException {
        String command = "UPDATE EditingOffs SET OffPercent = ? WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, percent);
        preparedStatement.setString(2, offID);
        preparedStatement.execute();
    }

    public static void removeEditingOff(String offID) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM EditingOffs WHERE OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, offID);
        preparedStatement.execute();
    }

    public static ArrayList<String> getEditingOffNames() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffName FROM EditingOffs WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 3);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<String> editingOffNames = new ArrayList<>();
        while (resultSet.next()){
            editingOffNames.add(resultSet.getString("OffName"));
        }
        return editingOffNames;
    }

    public static ArrayList<String> getEditingOffIDs() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffID FROM EditingOffs WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 3);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<String> editingOffIDs = new ArrayList<>();
        while (resultSet.next()){
            editingOffIDs.add(resultSet.getString("OffID"));
        }
        return editingOffIDs;
    }

    public static Off getOffByProductID(String id) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE ProductID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, id);
        Off off = Off.makeOffByID(preparedStatement.executeQuery());
        return OffTable.getSpecificOff(off.getOffID());
    }

    public static ArrayList<Off> getAllOffs() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffID FROM Offs";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Off> allOffs = new ArrayList<>();
        while (resultSet.next()){
            allOffs.add(OffTable.getSpecificOff(resultSet.getString("OffID")));
        }
        return allOffs;
    }

    public static boolean isThereProductInSpecificOff(String offID, String productID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE ProductID = ? AND OffID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        preparedStatement.setString(2, offID);
        return preparedStatement.executeQuery().next();
    }

    public static ArrayList<Off> getAllShowingOffs() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT OffID FROM Offs WHERE Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, 1);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Off> allOffs = new ArrayList<>();
        while (resultSet.next()){
            allOffs.add(OffTable.getSpecificOff(resultSet.getString("OffID")));
        }
        return allOffs;
    }

    public static void removeProductFromOffs(String productID) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Offs WHERE ProductID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        preparedStatement.execute();
    }

    public static void removeProductFromEditingOffs(String productID) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM EditingOffs WHERE ProductID = ?;";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, productID);
        preparedStatement.execute();
    }

    public static String getOffImageFilePath(String offID) {
        String fileName = "database\\Images\\Offs\\" + offID;
        String[] validImageExtensions = {"jpg" , "jpeg" , "png", "bmp"};
        for (String validImageExtension : validImageExtensions) {
            String filePath = fileName + "." + validImageExtension;
            if(new File(filePath).exists())
                return filePath;
        }
        return null;
    }

    public static FileInputStream getOffImageInputStream(String offID) throws FileNotFoundException {
        return new FileInputStream(getOffImageFilePath(offID));
    }

    public static void setOffImage(String offID, File pictureFile) throws IOException {
        String[] splitPath = pictureFile.getPath().split("\\.");
        String fileExtension = splitPath[splitPath.length - 1];
        File saveImage = new File("database\\Images\\Users\\" + offID + "." + fileExtension);
        Files.copy(pictureFile.toPath(), saveImage.toPath());
    }

    public static void removeOffImage(String offID) {
        File file = new File(getOffImageFilePath(offID));
        file.delete();
    }
}
