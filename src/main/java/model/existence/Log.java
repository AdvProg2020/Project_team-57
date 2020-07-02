package model.existence;

import model.db.OffTable;
import model.db.ProductTable;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Log {
    private String logID;
    private String customerUsername;
    private double discountPercent;
    private int status;
    private String statStr;
    private Date date;
    private ArrayList<ProductOfLog> allProducts = new ArrayList<>();
    private int productsCount;

    public Log() {
    }

    public Log(ResultSet resultSet) throws SQLException {
        if (resultSet.next()){
            this.logID = resultSet.getString("LogID");
            this.customerUsername = resultSet.getString("customerUsername");
            this.date = resultSet.getDate("Date");
            this.discountPercent = resultSet.getDouble("DiscountPercent");
            this.status = resultSet.getInt("Status");
            allProducts.add(new ProductOfLog(resultSet.getString("ProductID"),resultSet.getBoolean("IsCountable"),
                    resultSet.getInt("Count"), resultSet.getDouble("Amount"),
                    resultSet.getDouble("InitPrice"), resultSet.getDouble("OffPrice"), resultSet.getString("VendorUsername")));
        }
        while (resultSet.next()){
            allProducts.add(new ProductOfLog(resultSet.getString("ProductID"),resultSet.getBoolean("IsCountable"),
                    resultSet.getInt("Count"), resultSet.getDouble("Amount"),
                    resultSet.getDouble("InitPrice"), resultSet.getDouble("OffPrice"), resultSet.getString("VendorUsername")));
        }
        this.statStr = (this.status == 1 ? "Delivering" : "Delivered");
        this.productsCount = this.allProducts.size();
    }

    public Log(ArrayList<Log.ProductOfLog> allProducts, Log log) {
        this.logID = log.getLogID();
        this.status = log.getStatus();
        this.date = log.getDate();
        this.customerUsername = log.getCustomerUsername();
        this.allProducts.addAll(allProducts);
        this.statStr = (this.status == 1 ? "Delivering" : "Delivered");
        this.productsCount = this.allProducts.size();
    }

    public String getStatStr() {
        return statStr;
    }

    public int getProductsCount() {
        return productsCount;
    }

    //Start Inner Class
    public static class ProductOfLog{
        private String productID;
        private String productName;
        private String vendorUsername;
        private int count;
        private double amount;
        private double initPrice;
        private double offPrice;
        private boolean isCountable;

        public ProductOfLog(String productID,boolean isCountable, int count, double amount, double initPrice, double offPrice, String vendorUsername) {
            this.productID = productID;
            this.isCountable = isCountable;
            this.count = count;
            this.amount = amount;
            this.initPrice = initPrice;
            this.offPrice = offPrice;
            this.vendorUsername = vendorUsername;
            try {
                this.productName = ProductTable.getProductByID(productID).getName();
            } catch (SQLException e) {
                //:)
            } catch (ClassNotFoundException e) {
                //:)
            }
        }

        public ProductOfLog(Product product) throws SQLException, ClassNotFoundException {
            this.productID = product.getID();
            this.isCountable = product.isCountable();
            this.count = product.getCount();
            this.amount = product.getAmount();
            this.initPrice = product.getPrice();
            this.vendorUsername = product.getSellerUserName();
            if(OffTable.isThereProductInOff(product.getID())) {
                this.offPrice = (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100)) * product.getPrice();
            } else {
                this.offPrice = product.getPrice();
            }
            try {
                this.productName = ProductTable.getProductByID(productID).getName();
            } catch (SQLException e) {
                //:)
            } catch (ClassNotFoundException e) {
                //:)
            }
        }
        public ProductOfLog() {

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

        public String getVendorUsername() {
            return vendorUsername;
        }

        public void setVendorUsername(String vendorUsername) {
            this.vendorUsername = vendorUsername;
        }

        public String getProductName() {
            return productName;
        }
    }
    //End Inner Class

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

    public double getCustomerFinalPrice(){
        double finalPrice = 0;
        for (ProductOfLog productOfLog : allProducts) {
            finalPrice += productOfLog.getOffPrice() * productOfLog.getCount();
            finalPrice += productOfLog.getOffPrice() * productOfLog.getAmount();
        }
        return (finalPrice * (100 - this.discountPercent))/100;
    }

    public double getVendorFinalPrice() {
        double finalPrice = 0;
        for (ProductOfLog productOfLog : allProducts) {
            finalPrice += productOfLog.getOffPrice() * productOfLog.getAmount();
            finalPrice += productOfLog.getOffPrice() * productOfLog.getCount();
        }
        return finalPrice;
    }

    public double getInitialPrice() {
        double price = 0;
        for (ProductOfLog productOfLog : allProducts) {
            price += productOfLog.getInitPrice() * productOfLog.getAmount();
            price += productOfLog.getInitPrice() * productOfLog.getCount();
        }
        return price;
    }
}
