package model.AccountTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientTable extends AccountTable {

    public static void addNewClient(String username, String password) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Clients value (?, ?, ?)");
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, password);
        preparedStatement.execute();
    }

}
