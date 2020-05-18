package model.existence;

public class Comment {
    private String commentID;
    private String productID;
    private String customerUsername;
    private String title;
    private String content;
    private int status;

    public Comment(String commentID, String customerUsername, String title, String content, int status) {
        this.commentID = commentID;
        this.customerUsername = customerUsername;
        this.title = title;
        this.content = content;
        this.status = status;
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
}
