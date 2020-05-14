package model.db;

import model.existence.Off;

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
        return new Off(preparedStatement.executeQuery());
    }

    public static ArrayList<String> allOffNames(String loggedInUsername) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE VendorUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, loggedInUsername);
        ArrayList<String> allOffNames = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            allOffNames.add(resultSet.getString("OffName"));
        }
        return allOffNames;
    }

    public static ArrayList<String> allOffIDs(String loggedInUsername) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Offs WHERE VendorUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, loggedInUsername);
        ArrayList<String> allOffIDs = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            allOffIDs.add(resultSet.getString("OffID"));
        }
        return allOffIDs;
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
}
