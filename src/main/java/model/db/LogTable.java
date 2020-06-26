package model.db;

import model.existence.Account;
import model.existence.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LogTable extends Database {
        public static Log getCustomerLogByID(String logID) throws SQLException, ClassNotFoundException {
                String command = "SELECT * FROM Logs WHERE LogID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, logID);
                return new Log(preparedStatement.executeQuery());
        }

        public static void addLog(Log log) throws SQLException, ClassNotFoundException {
                String command = "INSERT INTO Logs(ProductId, CustomerUsername, Count, Amount, InitPrice, OffPrice," +
                        "Date, DiscountPercent, Status, LogID, IsCountable, VendorUsername) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
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
                        preparedStatement.setString(12, product.getVendorUsername());
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
                        allLogs.add(getCustomerLogByID(resultSet.getString("LogID")));
                }
                return allLogs;
        }

        public static ArrayList<Log> getAllVendorLogs(String username) throws SQLException, ClassNotFoundException {
                String command = "SELECT DISTINCT LogID FROM Logs WHERE VendorUsername = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<Log> allLogs = new ArrayList<>();
                while (resultSet.next()) {
                        allLogs.add(getVendorLogByID(resultSet.getString("LogID"), username));
                }
                return allLogs;
        }

        public static Log getVendorLogByID(String logID, String vendorUsername) throws SQLException, ClassNotFoundException {
                String command = "SELECT * FROM Logs WHERE LogID = ? AND VendorUsername = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, logID);
                preparedStatement.setString(2, vendorUsername);
                return new Log(preparedStatement.executeQuery());
        }

        public static boolean isThereLogWithID(String logID) throws SQLException, ClassNotFoundException {
                String command = "SELECT * FROM Logs WHERE LogID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, logID);
                return preparedStatement.executeQuery().next();
        }

    public static boolean isProductPurchasedByCustomer(String productID, String customerUsername) throws SQLException, ClassNotFoundException {
            String command = "SELECT * FROM Logs WHERE CustomerUsername = ? And ProductID = ?";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, customerUsername);
            preparedStatement.setString(2, productID);
            return preparedStatement.executeQuery().next();
    }

    public static ArrayList<String> getAllCustomerUsernamesForProduct(String currentProduct) throws SQLException, ClassNotFoundException {
                String command = "SELECT DISTINCT CustomerUsername FROM Logs WHERE ProductID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, currentProduct);
                ResultSet resultSet = preparedStatement.executeQuery();
                ArrayList<String> accounts = new ArrayList<>();
                while (resultSet.next()) {
                        accounts.add(resultSet.getString("CustomerUsername"));
                }
                return accounts;
        }

    public static double getMaxSaleByID(String productID) throws SQLException, ClassNotFoundException {
            String command = "SELECT * FROM Logs WHERE ProductID = ?";
            PreparedStatement preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, productID);
            ResultSet resultSet = preparedStatement.executeQuery();

            double maxSale = 0;
            while (resultSet.next()) {
                    maxSale += resultSet.getDouble("OffPrice") * resultSet.getInt("Count");
                    maxSale += resultSet.getDouble("OffPrice") * resultSet.getDouble("Amount");
            }

            return maxSale;
    }

        public static int getMaxCountOfSaleByProductID(String productID) throws SQLException, ClassNotFoundException {
                String command = "SELECT Count FROM Logs WHERE ProductID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, productID);
                ResultSet resultSet = preparedStatement.executeQuery();

                int maxCount = 0;
                while (resultSet.next())
                        maxCount += resultSet.getDouble("Count");

                return maxCount;
        }

        public static double getMaxAmountOfSaleByProductID(String productID) throws SQLException, ClassNotFoundException {
                String command = "SELECT Amount FROM Logs WHERE ProductID = ?";
                PreparedStatement preparedStatement = getConnection().prepareStatement(command);
                preparedStatement.setString(1, productID);
                ResultSet resultSet = preparedStatement.executeQuery();

                double maxAmount = 0;
                while (resultSet.next())
                        maxAmount += resultSet.getDouble("Amount");

                return maxAmount;
        }
}
