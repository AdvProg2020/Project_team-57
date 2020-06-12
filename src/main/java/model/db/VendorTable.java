package model.db;

import model.existence.Account;
import model.existence.Product;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VendorTable extends Database {
    public static ArrayList<Account> getUnApprovedVendors() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Accounts WHERE AccType = ? AND IsApproved = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, "Vendor");
        preparedStatement.setBoolean(2, false);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Account> accounts = new ArrayList<>();
        while (resultSet.next()) {
            accounts.add(Account.makeAccount(resultSet));
        }
        return accounts;
    }

    public static void modifyApprove(String username, int flag) throws SQLException, ClassNotFoundException {
        String accept = "UPDATE Accounts SET IsApproved = ? WHERE Username = ?";
        String decline = "DELETE FROM Accounts WHERE Username = ?";
        PreparedStatement preparedStatement;
        if (flag == 0) {
            preparedStatement = getConnection().prepareStatement(decline);
            preparedStatement.setString(1, username);
        } else {
            preparedStatement = getConnection().prepareStatement(accept);
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, username);
        }
        preparedStatement.execute();
    }

    public static ArrayList<Product> getProductsWithUsername(String username) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        products.addAll(getApprovedProductsWithUsername(username));
        products.addAll(getUnApprovedProductsWithUsername(username));
        products.addAll(getEditingProductsWithUsername(username));
        return products;
    }

    public static ArrayList<Product> getApprovedProductsWithUsername(String username) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        String command = "SELECT * FROM Products WHERE SellerUsername = ? AND Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setInt(2, 1);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            products.add(new Product(resultSet));
        }
        return products;
    }

    public static ArrayList<Product> getUnApprovedProductsWithUsername(String username) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        String command = "SELECT * FROM Products WHERE SellerUsername = ? AND Status = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        preparedStatement.setInt(2, 2);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            products.add(new Product(resultSet));
        }
        return products;
    }

    public static ArrayList<Product> getEditingProductsWithUsername(String username) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        String command = "SELECT * FROM EditingProducts WHERE SellerUsername = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            products.add(new Product(resultSet));
        }
        return products;
    }

    public static void addCountableProduct(Product product, String username) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Products (ID, ProductName, Brand, SellerUsername, Count, IsCountable, Category," +
                "Description, Price, AverageScore, Status, Seen, ScoreNum, ApprovalDate)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, product.getID());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setString(3, product.getBrand());
        preparedStatement.setString(4, username);
        preparedStatement.setInt(5, product.getCount());
        preparedStatement.setBoolean(6, true);
        preparedStatement.setString(7, product.getCategory());
        preparedStatement.setString(8, product.getDescription());
        preparedStatement.setDouble(9, product.getPrice());
        preparedStatement.setDouble(10, product.getAverageScore());
        preparedStatement.setInt(11, 2);
        preparedStatement.setInt(12, 0);
        preparedStatement.setInt(13, 0);
        preparedStatement.setDate(14, product.getApprovalDate());
        preparedStatement.execute();
    }

    public static void addUnCountableProduct(Product product, String username) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Products (ID, ProductName, Brand, SellerUsername, Amount, IsCountable, Category," +
                "Description, Price, AverageScore, Status, Seen, ScoreNum, ApprovalDate)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, product.getID());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setString(3, product.getBrand());
        preparedStatement.setString(4, username);
        preparedStatement.setDouble(5, product.getAmount());
        preparedStatement.setBoolean(6, false);
        preparedStatement.setString(7, product.getCategory());
        preparedStatement.setString(8, product.getDescription());
        preparedStatement.setDouble(9, product.getPrice());
        preparedStatement.setDouble(10, product.getAverageScore());
        preparedStatement.setInt(11, 2);
        preparedStatement.setInt(12, 0);
        preparedStatement.setInt(13, 0);
        preparedStatement.setDate(14, product.getApprovalDate());
        preparedStatement.execute();
    }

    public static void addTempProducts() throws SQLException, ClassNotFoundException {
        Product product = new Product();
        product.setID("p1234567");
        product.setName("Yennefer");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(2500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1234568");
        product.setName("Triss");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(1500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p2345678");
        product.setName("Shani");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(1500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1234569");
        product.setName("Syanna");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(2000);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1212122");
        product.setName("Keira");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(1000);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
    }

    public static void addAnotherTempProducts() throws SQLException, ClassNotFoundException {
        Product product = new Product();
        product.setID("p1231231");
        product.setName("Ciri");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(5000);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1231244");
        product.setName("Iris");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1211111");
        product.setName("Priscilla");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1211111");
        product.setName("Priscilla");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p1213133");
        product.setName("philippa");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(1200.59);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p3456781");
        product.setName("Product1");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(3500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p3456712");
        product.setName("Product2");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(4500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p3456712");
        product.setName("Product2");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(4500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p3456634");
        product.setName("Product3");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(5500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p3456656");
        product.setName("Product4");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(5500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);
        product = new Product();
        product.setID("p3456678");
        product.setName("Product5");
        product.setCount(1);
        product.setCategory("All Products");
        product.setPrice(6500);
        product.setStatus(1);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        addCountableProduct(product, "Geralt Of Rivia");
        ProductTable.setProductStatus(product.getID(), 1);

    }
}