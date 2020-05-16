package model.db;

import model.existence.Discount;
import org.omg.CORBA.ARG_IN;

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

    public static void removeDiscountCode(String ID) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Discounts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, ID);
        preparedStatement.execute();
    }

    public static void editCode(String ID, String code) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET Code = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, code);
        preparedStatement.setString(2,  ID);
        preparedStatement.execute();
    }

    public static void editFinishDate(String ID, Date newFinishDate) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET FinishDate = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDate(1, newFinishDate);
        preparedStatement.setString(2,  ID);
        preparedStatement.execute();
    }

    public static void editDiscountPercent(String ID, double newDiscountPercent) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET DiscountPercent = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, newDiscountPercent);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static void editMaxDiscount(String ID, double newMaxDiscount) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET MaxDiscount = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setDouble(1, newMaxDiscount);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static void editMaxRepetition(String ID, int newMaxRepetition) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET MaxRepetition = ? WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, newMaxRepetition);
        preparedStatement.setString(2, ID);
        preparedStatement.execute();
    }

    public static ArrayList<Discount> getAllDiscountCodes() throws SQLException, ClassNotFoundException {
        String command = "SELECT DISTINCT ID FROM Discounts";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Discount> allDiscountCodes = new ArrayList<>();

        String command2 = "SELECT * FROM Discounts WHERE ID = ?";
        while (resultSet.next()) {

            preparedStatement = getConnection().prepareStatement(command2);
            preparedStatement.setString(1, resultSet.getString("ID"));
            allDiscountCodes.add(new Discount(preparedStatement.executeQuery()));
        }
        return allDiscountCodes;
    }

    public static Discount getDiscountByID(String ID) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Discounts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, ID);
        return new Discount(preparedStatement.executeQuery());
    }

    public static boolean isThereDiscountWithID(String ID) throws SQLException, ClassNotFoundException {
        String sqlQuery = "SELECT * FROM Discounts WHERE ID = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(sqlQuery);
        preparedStatement.setString(1, ID);
        return preparedStatement.executeQuery().next();
    }

    public static ArrayList<Discount> getCustomerDiscountCodes(String username) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Discounts WHERE CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);

        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Discount> discounts = new ArrayList<>();
        while (resultSet.next())
        {
            discounts.add(Discount.makeCustomerDiscount(resultSet));
        }
        return discounts;
    }

    public static void updateDiscountCodesTime() throws SQLException, ClassNotFoundException {
        ArrayList<Discount> allDiscountCodes = getAllDiscountCodes();
        for (Discount discountCode : allDiscountCodes) {
            if(discountCode.getFinishDate().compareTo(new Date(System.currentTimeMillis())) != 1)
                removeDiscountCode(discountCode.getID());
        }
    }

    public static void updateDiscountCodesRep() throws SQLException, ClassNotFoundException {
        ArrayList<Discount> allDiscountCodes = getAllDiscountCodes();
        for (Discount discountCode : allDiscountCodes) {
            for (String username : discountCode.getCustomersWithRepetition().keySet()) {
                if(discountCode.getMaxRepetition() <= discountCode.getCustomersWithRepetition().get(username))
                    removeDiscountCodeForUsername(discountCode.getID(),username);
            }
        }
    }

    public static void removeDiscountCodeForUsername(String discountID, String customerUsername) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Discounts WHERE ID = ? AND CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, discountID);
        preparedStatement.setString(2, customerUsername);
        preparedStatement.execute();
    }

    public static void addRepetitionToDiscount(Discount discount, String username) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Discounts SET MaxRepetition = ? WHERE ID = ? AND CustomerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, discount.getCustomersWithRepetition().get(username) + 1);
        preparedStatement.setString(2, discount.getID());
        preparedStatement.setString(3, username);
        preparedStatement.execute();
    }
}
