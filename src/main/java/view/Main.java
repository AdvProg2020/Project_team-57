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
import model.existence.Discount;
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

    /*public static void addDiscount() throws SQLException, ClassNotFoundException {
        Discount discount = new Discount();
        discount.setCode("Discount Code1");
        discount.setStartDate(new Date(System.currentTimeMillis()));
        discount.setFinishDate(new Date(System.currentTimeMillis() + 500000000));

    }*/
}
