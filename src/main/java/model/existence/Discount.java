package model.existence;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Discount {
    private String ID;
    private String code;
    private Date startDate;
    private Date finishDate;
    private double discountPercent;
    private double maxDiscount;
    private int maxRepetition;
    private HashMap<String, Integer> customersWithRepetition;

    public Discount(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            this.ID = resultSet.getString("ID");
            this.code = resultSet.getString("Code");
            this.startDate = resultSet.getDate("StartDate");
            this.finishDate = resultSet.getDate("FinishDate");
            this.discountPercent = resultSet.getDouble("DiscountPercent");
            this.maxDiscount = resultSet.getDouble("MaxDiscount");
            this.maxRepetition = resultSet.getInt("MaxRepetition");
            this.customersWithRepetition.put(resultSet.getString("CustomerUsername"),
                    resultSet.getInt("Repetition"));
        }
        while (resultSet.next()){
            this.customersWithRepetition.put(resultSet.getString("CustomerUsername"),
                    resultSet.getInt("Repetition"));
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public int getMaxRepetition() {
        return maxRepetition;
    }

    public void setMaxRepetition(int maxRepetition) {
        this.maxRepetition = maxRepetition;
    }

    public HashMap<String, Integer> getCustomersWithRepetition() {
        return customersWithRepetition;
    }

    public void setCustomersWithRepetition(HashMap<String, Integer> customerWithRepetition) {
        this.customersWithRepetition = customerWithRepetition;
    }
}
