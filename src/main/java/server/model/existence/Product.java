package server.model.existence;

import server.model.db.CategoryTable;
import server.model.db.OffTable;

import java.sql.Date;
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
    private int scoreNum;
    private Date approvalDate;
    private int seen;

    //Off Section
    private boolean isOnSale;
    private double offPrice;
    private double offPercent;

    //Category Section
    private String categoryFeatures;

    public Product(ResultSet resultSet) throws SQLException, ClassNotFoundException {
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
        this.scoreNum = resultSet.getInt("ScoreNum");
        this.approvalDate = resultSet.getDate("ApprovalDate");
        this.seen = resultSet.getInt("Seen");

        if(OffTable.isThereProductInOff(ID)) {
            this.isOnSale = true;
            this.offPercent = OffTable.getOffByProductID(ID).getOffPercent();
            this.offPrice = (1 - offPercent / 100) * price;
        }

        this.categoryFeatures = CategoryTable.getCategoryWithName(category).getFeatures();
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

    public String getTheStatus() {
        String status = null;

        switch (this.status) {
            case 1 :
                status = "Approved";
                break;
            case 2 :
                status = "Unapproved";
                break;
            case 3 :
                status = "Editing";
                break;
            default:
                status = "What The Shit?!";
        }

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

    public int getScoreNum() {
        return scoreNum;
    }

    public void setScoreNum(int scoreNum) {
        this.scoreNum = scoreNum;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public boolean isOnSale() {
        return isOnSale;
    }

    public void setOnSale(boolean onSale) {
        isOnSale = onSale;
    }

    public double getOffPrice() {
        return offPrice;
    }

    public void setOffPrice(double offPrice) {
        this.offPrice = offPrice;
    }

    public double getOffPercent() {
        return offPercent;
    }

    public void setOffPercent(double offPercent) {
        this.offPercent = offPercent;
    }

    public String getCategoryFeatures() {
        return categoryFeatures;
    }

    public void setCategoryFeatures(String categoryFeatures) {
        this.categoryFeatures = categoryFeatures;
    }

    public static class ProductFileInfo {
        private String productID;
        private String name;
        private String creator;
        private String extension;
        private String description;

        public ProductFileInfo(String productID, String name, String creator, String extension, String description) {
            this.productID = productID;
            this.name = name;
            this.creator = creator;
            this.extension = extension;
            this.description = description;
        }

        public ProductFileInfo() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }
    }
}
