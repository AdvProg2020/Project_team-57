package model.db;

import model.existence.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LogTable extends Database {
        public static Log getLogByID(String logID) throws SQLException, ClassNotFoundException {
                String command = "SELECT * FROM Logs WHERE LogID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, logID);
                return new Log(preparedStatement.executeQuery());
        }

        public static void addLog(Log log) throws SQLException, ClassNotFoundException {
                String command = "INSERT INTO Logs(ProductId, CustomerUsername, Count, Amount, InitPrice, OffPrice," +
                        "Date, DiscountPercent, Status, LogID, IsCountable) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                for (Log.ProductOfLog product : log.getAllProducts()) {
                        preparedStatement.setString(1, product.getProductID());
                        preparedStatement.setString(2, log.getCustomerName());
                        preparedStatement.setInt(3, product.getCount());
                        preparedStatement.setDouble(4, product.getAmount());
                        preparedStatement.setDouble(5, product.getInitPrice());
                        preparedStatement.setDouble(6, product.getOffPrice());
                        preparedStatement.setDate(7, log.getDate());
                        preparedStatement.setDouble(8, log.getDiscountPercent());
                        preparedStatement.setInt(9, log.getStatus());
                        preparedStatement.setString(10, log.getLogID());
                        preparedStatement.setBoolean(11, product.isCountable());
                        preparedStatement.execute();
                }
        }

        public static ArrayList<Log> getAllCustomerLogs(String username) throws SQLException, ClassNotFoundException {
                String command = "SELECT DISTINCT LogID FROM Logs WHERE CustomerUsername = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<Log> allLogs = new ArrayList<>();
                while (resultSet.next()) {
                        allLogs.add(getLogByID(resultSet.getString("LogID")));
                }
                return allLogs;
        }

        public static ArrayList<Log> getAllVendorLogs(String vendorName) throws SQLException, ClassNotFoundException {
                String command = "SELECT DISTINCT LogID FROM Logs";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<Log.ProductOfLog> allProducts = new ArrayList<>();
                ArrayList<Log> allLogs = new ArrayList<>();
                while (resultSet.next()) {
                        allProducts.clear();
                        for (Log.ProductOfLog product : getLogByID(resultSet.getString("LogID")).getAllProducts()) {
                                if (vendorName.equals(ProductTable.getVendorName(product.getProductID())))
                                        allProducts.add(product);
                        }
                        if (allProducts.size() != 0)
                                allLogs.add(new Log(allProducts, getLogByID(resultSet.getString("LogID"))));
                        allProducts.clear();
                }
                return allLogs;
        }

        public static boolean isThereLogWithID(String logID) throws SQLException, ClassNotFoundException {
                String command = "SELECT * FROM Logs WHERE LogID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, logID);
                return preparedStatement.executeQuery().next();
        }
}
