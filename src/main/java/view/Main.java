package view;

import com.sun.corba.se.spi.orbutil.fsm.FSM;
import controller.Control;
import controller.account.AdminControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.db.AccountTable;
import model.db.ProductTable;
import model.db.VendorTable;
import model.existence.Account;
import model.existence.Discount;
import model.existence.Product;

import java.io.File;
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
/*        File file = new File("G:\\College\\2nd semester\\second semester PROJECT\\First Phase\\Codes\\First Version\\database\\Images\\Products\\p3456712.jpg");
        System.out.println(file.delete());*/
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
/*        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProductMenu.fxml"));
        Parent root = fxmlLoader.load();
        ProductProcessor processor = fxmlLoader.getController();
        Control.setType("Vendor");
        Control.setUsername("sepehr");*/
//        Product product = ProductTable.getProductByID("p2345678");
//        processor.initProcessor(new Product(), ProductProcessor.ProductMenuType.VENDOR_ADD);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WelcomeMenu.fxml"));
        Parent root = fxmlLoader.load();
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
}
