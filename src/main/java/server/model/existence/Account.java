package server.model.existence;

import com.jfoenix.controls.JFXCheckBox;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {

    public boolean isApproved() {
        return isApproved.equals("Approved");
    }

    public static enum AccountType {
        ADMIN, CUSTOMER, VENDOR;
    }

    private String username;
    private String password;
    private String type;
    private String firstName;
    private String lastName;
    private String email;
    private String brand;
    private String isApproved;
    private double credit;
    //private JFXCheckBox checkBox = new JFXCheckBox("");

    public static Account makeAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account(resultSet.getString("Username"), resultSet.getString("Password"));
        account.setType(resultSet.getString("AccType"));
        account.setFirstName(resultSet.getString("FirstName"));
        account.setLastName(resultSet.getString("LastName"));
        account.setEmail(resultSet.getString("Email"));
        account.setCredit(resultSet.getDouble("Credit"));
        account.setBrand(resultSet.getString("Brand"));
        account.setIsApproved(resultSet.getBoolean("IsApproved") ? "Approved" : "Unapproved");
        return account;
    }

    public Account() {}

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    //Inner Class Start
    public static class AccountOfBuyer {
        private String userName;
        private String firstName;
        private String lastName;
        private String Email;

        public AccountOfBuyer(Account account) {
            this.userName = account.getUsername();
            this.firstName = account.getFirstName();
            this.lastName = account.getLastName();
            this.Email = account.getEmail();
        }

        public AccountOfBuyer() {
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }
    }
    //Inner Class End

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getBrand() {
        return brand;
    }

    public double getCredit() {
        return credit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

/*    public JFXCheckBox getCheckBox() {
        return checkBox;
    }
    public void setCheckBox(JFXCheckBox checkBox) {
        this.checkBox = checkBox;
    }*/
}
