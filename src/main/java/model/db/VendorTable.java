package model.db;

import model.existence.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VendorTable extends Database {
    public static ArrayList<Account> getUnApprovedVendors() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE AccType = ? AND IsApproved = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "Vendor");
        preparedStatement.setBoolean(2, false);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Account> accounts = new ArrayList<>();
        while (resultSet.next()) {
            accounts.add(Account.makeAccount(resultSet));
        }
        return accounts;
    }

    public static void modifyApprove(String username, int flag) throws SQLException, ClassNotFoundException {
        String accept = "UPDATE Accounts SET IsApproved = ? WHERE Username = ?";
        String decline = "DELETE FROM Accounts WHERE Username = ?";
        PreparedStatement preparedStatement;
        if (flag == 0) {
            preparedStatement = getConnection().prepareStatement(decline);
            preparedStatement.setString(1, username);
        } else {
            preparedStatement = getConnection().prepareStatement(accept);
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, username);
        }
        preparedStatement.execute();
    }

}
