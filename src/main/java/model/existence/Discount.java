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
    private HashMap<String, Integer> customersWithRepetition = new HashMap<>();

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

    public Discount(String ID, String code, Date startDate, Date finishDate, double discountPercent, double maxDiscount, int maxRepetition) {
        this.ID = ID;
        this.code = code;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.discountPercent = discountPercent;
        this.maxDiscount = maxDiscount;
        this.maxRepetition = maxRepetition;
    }

    public static Discount makeCustomerDiscount(ResultSet resultSet) throws SQLException {
        Discount discount = new Discount();
        discount.setID(resultSet.getString("ID"));
        discount.setCode(resultSet.getString("Code"));
        discount.setStartDate(resultSet.getDate("StartDate"));
        discount.setFinishDate(resultSet.getDate("FinishDate"));
        discount.setDiscountPercent(resultSet.getDouble("DiscountPercent"));
        discount.setMaxDiscount(resultSet.getDouble("MaxDiscount"));
        discount.setMaxRepetition(resultSet.getInt("MaxRepetition"));
        HashMap <String, Integer> customersWithRepetition = new HashMap<>();
        customersWithRepetition.put(resultSet.getString("CustomerUsername"),
                resultSet.getInt("Repetition"));
        discount.setCustomersWithRepetition(customersWithRepetition);
        return discount;
    }

    public Discount() {
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

    public void addCustomerWithRepetition(String username, int repetition) {
        this.customersWithRepetition.put(username, repetition);
    }

    public void removeCustomer(String username) {
        this.customersWithRepetition.remove(username);
    }
}
