package model.existence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Comment {
    private String commentID;
    private String productID;
    private String customerUsername;
    private String title;
    private String content;
    private int status;
    private String statStr;
    private int score;

    public Comment(ResultSet resultSet) throws SQLException {
        this.commentID = resultSet.getString("CommentID");
        this.customerUsername = resultSet.getString("CustomerUsername");
        this.title = resultSet.getString("Title");
        this.content = resultSet.getString("Content");
        this.status = resultSet.getInt("Status");
        this.productID = resultSet.getString("ProductID");
        switch (this.status) {
            case 1:
                this.statStr = "Approved";
                break;
            case 2 :
                this.statStr = "UnChecked";
                break;
            case 3 :
                this.statStr = "Deleted";
                break;
        }
    }

    public Comment() {
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatStr() {
        return statStr;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Comment : " +
                "\nID : " + commentID +
                "\nUserName : " + customerUsername +
                "\nTitle : " + title +
                "\nContent : " + content +
                "\nStatus : " + status +
                "\nScore : " + score;
    }
}
