package server.server.sapahbank;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Receipt {
    private String repceiptID;
    private String type;
    private String description;
    private String source;
    private String destination;
    private double money;
    private boolean isPaid;

    public Receipt(ResultSet resultSet) throws SQLException {
        this.repceiptID = resultSet.getString("ReceiptID");
        this.type = resultSet.getString("ReceiptType");
        this.description = resultSet.getString("Description");
        this.source = resultSet.getString("SourceID");
        this.destination = resultSet.getString("DestID");
        this.money = resultSet.getDouble("Money");
        this.isPaid = resultSet.getBoolean("Paid");

    }

}
