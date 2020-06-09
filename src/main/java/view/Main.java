package view;

import controller.Control;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.db.AccountTable;
import model.db.VendorTable;
import model.existence.Account;

import java.sql.SQLException;

public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
}
