package server.server.bank.sapahbank;

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

    public String getRepceiptID() {
        return repceiptID;
    }

    public void setRepceiptID(String repceiptID) {
        this.repceiptID = repceiptID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
