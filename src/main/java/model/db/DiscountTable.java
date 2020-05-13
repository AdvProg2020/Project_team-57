package model.db;

import model.existence.Discount;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiscountTable extends Database {
    public static void addDiscount(Discount discount) throws SQLException, ClassNotFoundException {
        String command;
        PreparedStatement preparedStatement;
        for (String customer : discount.getCustomersWithRepetition().keySet()) {
            command = "INSERT INTO Discounts(Code, StartDate, FinishDate, DiscountPercent, MaxDiscount," +
                    "Repetition, MaxRepetition, CustomerUsername, ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = getConnection().prepareStatement(command);
            preparedStatement.setString(1, discount.getCode());
            preparedStatement.setDate(2, discount.getStartDate());
            preparedStatement.setDate(3, discount.getFinishDate());
            preparedStatement.setDouble(4, discount.getDiscountPercent());
            preparedStatement.setDouble(5, discount.getMaxDiscount());
            preparedStatement.setInt(6, discount.getCustomersWithRepetition().get(customer));
            preparedStatement.setInt(7, discount.getMaxRepetition());
            preparedStatement.setString(8, customer);
            preparedStatement.setString(9, discount.getID());
            preparedStatement.execute();
        }
    }

    public static void removeDiscountCode(String discountCode) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Discounts WHERE Code = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, discountCode);
        preparedStatement.execute();
    }

    public static void editFinishDate(String discountCode, Date newFinishDate) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET FinishDate = ? WHERE Code = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDate(1, newFinishDate);
        preparedStatement.setString(2,  discountCode);
        preparedStatement.execute();
    }

    public static void editMaxRepetition(String discountCode, int newMaxRepetition) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET MaxRepetition = ? WHERE Code = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, newMaxRepetition);
        preparedStatement.setString(2, discountCode);
        preparedStatement.execute();
    }

    public static void editMaxDiscount(String discountCode, double newMaxDiscount) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET MaxDiscount = ? WHERE Code = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, newMaxDiscount);
        preparedStatement.setString(2, discountCode);
        preparedStatement.execute();
    }

    public static void editDiscountPercent(String discountCode, double newDiscountPercent) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET DiscountPrcent = ? WHERE Code = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, newDiscountPercent);
        preparedStatement.setString(2, discountCode);
        preparedStatement.execute();
    }

    public static ArrayList<Discount> getAllDiscountCodes() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT Code FROM Discounts";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Discount> allDiscountCodes = new ArrayList<>();
        while (resultSet.next()) {
            allDiscountCodes.add(new Discount(resultSet));
        }
        return allDiscountCodes;
    }
}
