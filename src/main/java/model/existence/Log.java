package model.existence;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Log {
    private String logID;
    private String customerUsername;
    private double discountPercent;
    private int status;
    private Date date;
    private ArrayList<ProductOfLog> allProducts = new ArrayList<>();


    public Log(ResultSet resultSet) throws SQLException {
        if (resultSet.next()){
            this.logID = resultSet.getString("LogID");
            this.customerUsername = resultSet.getString("customerUsername");
            this.date = resultSet.getDate("Date");
            this.discountPercent = resultSet.getDouble("DiscountPercent");
            allProducts.add(new ProductOfLog(resultSet.getString("ProductID"),resultSet.getBoolean("IsCountable"),
                    resultSet.getInt("Count"), resultSet.getDouble("Amount"),
                    resultSet.getDouble("InitPrice"), resultSet.getDouble("OffPrice")));
        }

        while (resultSet.next()){
            allProducts.add(new ProductOfLog(resultSet.getString("ProductID"),resultSet.getBoolean("IsCountable"),
                    resultSet.getInt("Count"), resultSet.getDouble("Amount"),
                    resultSet.getDouble("InitPrice"), resultSet.getDouble("OffPrice")));
        }
    }

    public Log(ArrayList<Log.ProductOfLog> allProducts, Log log) {
        this.logID = log.getLogID();
        this.status = log.getStatus();
        this.date = log.getDate();
        this.customerUsername = log.getCustomerUsername();
        this.allProducts.addAll(allProducts);
    }

    public static class ProductOfLog{
        private String productID;
        private int count;
        private double amount;
        private double initPrice;
        private double offPrice;
        private boolean isCountable;

        public ProductOfLog(String productID,boolean isCountable, int count, double amount, double initPrice, double offPrice) {
            this.productID = productID;
            this.isCountable = isCountable;
            this.count = count;
            this.amount = amount;
            this.initPrice = initPrice;
            this.offPrice = offPrice;
        }

        public boolean isCountable() {
            return isCountable;
        }

        public void setCountable(boolean countable) {
            isCountable = countable;
        }

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getInitPrice() {
            return initPrice;
        }

        public void setInitPrice(double initPrice) {
            this.initPrice = initPrice;
        }

        public double getOffPrice() {
            return offPrice;
        }

        public void setOffPrice(double offPrice) {
            this.offPrice = offPrice;
        }
    }

    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public String getCustomerName() {
        return customerUsername;
    }

    public void setCustomerName(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<ProductOfLog> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(ArrayList<ProductOfLog> allProducts) {
        this.allProducts = allProducts;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
