package view;

import controller.Control;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.db.AccountTable;
import model.db.ProductTable;
import model.db.VendorTable;
import model.existence.Account;
import model.existence.Product;

import java.sql.Date;
import java.sql.SQLException;

public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
/*        try {
            VendorTable.addTempProducts();
            System.out.println("Products added");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //addProducts();
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("WelcomeMenu.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Boos Market");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Main Icon.png")));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setScene(String title, Parent nextScene) {
        Stage stage = Main.getStage();
        stage.setScene(new Scene(nextScene));
        stage.setResizable(false);
        stage.setTitle(title);
    }

    public static Scene getScene() {
        return stage.getScene();
    }

    public static void addProducts() throws SQLException, ClassNotFoundException {
        Product product = new Product();
        product.setID("p1234567");
        product.setName("Product1");
        product.setBrand("Products");
        product.setCount(5);
        product.setCategory("All Products");
        product.setPrice(4500);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        VendorTable.addCountableProduct(product, "sepehr");
        ProductTable.setProductStatus(product.getID(), 1);

        product = new Product();
        product.setID("p2345678");
        product.setName("Product2");
        product.setBrand("Products");
        product.setCount(5);
        product.setCategory("Technology");
        product.setPrice(5500);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        VendorTable.addCountableProduct(product, "sepehr");
        ProductTable.setProductStatus(product.getID(), 1);

        product = new Product();
        product.setID("p3456789");
        product.setName("Product3");
        product.setBrand("Products");
        product.setCount(5);
        product.setCategory("All Products");
        product.setPrice(6500);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        VendorTable.addCountableProduct(product, "sepehr");
        ProductTable.setProductStatus(product.getID(), 1);

        product = new Product();
        product.setID("p4567890");
        product.setName("Product4");
        product.setBrand("Products");
        product.setCount(5);
        product.setCategory("Technology");
        product.setPrice(7500);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        VendorTable.addCountableProduct(product, "sepehr");
        ProductTable.setProductStatus(product.getID(), 1);

        product = new Product();
        product.setID("p5678901");
        product.setName("Product5");
        product.setBrand("Products");
        product.setCount(5);
        product.setCategory("Serious Shit");
        product.setPrice(8500);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        VendorTable.addCountableProduct(product, "sepehr");
        ProductTable.setProductStatus(product.getID(), 1);

        product = new Product();
        product.setID("p6789012");
        product.setName("Product6");
        product.setBrand("Products");
        product.setCount(5);
        product.setCategory("UnSerious Shit");
        product.setPrice(9500);
        product.setApprovalDate(new Date(System.currentTimeMillis()));
        VendorTable.addCountableProduct(product, "sepehr");
        ProductTable.setProductStatus(product.getID(), 1);
    }
}
