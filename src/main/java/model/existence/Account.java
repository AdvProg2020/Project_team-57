package model.existence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private String username;
    private String password;
    private String type;
    private String firstName;
    private String lastName;
    private String email;
    private String brand;
    private double credit;

    public static Account makeAccount(ResultSet resultSet) throws SQLException {
        resultSet.next();
        Account account = new Account(resultSet.getString("Username"), resultSet.getString("Password"));
        account.setType(resultSet.getString("AccType"));
        account.setFirstName(resultSet.getString("FirstName"));
        account.setLastName(resultSet.getString("LastName"));
        account.setEmail(resultSet.getString("Email"));
        account.setCredit(resultSet.getDouble("Credit"));
        account.setBrand(resultSet.getString("Brand"));
        return account;
    }

    public Account() {}

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

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
}
