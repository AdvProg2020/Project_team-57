package model.existence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
    private String ID;
    private int status;
    private String name;
    private String brand;
    private String sellerUserName;
    private int count;
    private double amount;
    private boolean isCountable;
    private String category;
    private String description;
    private double price;
    private double averageScore;

    public Product(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getString("ID");
        this.status = resultSet.getInt("Status");
        this.name = resultSet.getString("ProductName");
        this.brand = resultSet.getString("Brand");
        this.sellerUserName = resultSet.getString("SellerUserName");
        this.count = resultSet.getInt("Count");
        this.amount = resultSet.getDouble("Amount");
        this.isCountable = resultSet.getBoolean("IsCountable");
        this.category = resultSet.getString("Category");
        this.description = resultSet.getString("Description");
        this.price = resultSet.getDouble("Price");
        this.averageScore = resultSet.getDouble("AverageScore");
    }

    public Product() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public void setSellerUserName(String sellerUserName) {
        this.sellerUserName = sellerUserName;
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

    public boolean isCountable() {
        return isCountable;
    }

    public void setCountable(boolean countable) {
        isCountable = countable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}
