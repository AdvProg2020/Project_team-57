import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        //System.out.println(getClass().getResource("WelcomeMenu.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("WelcomeMenu.fxml"));
        primaryStage.setScene(new Scene(root));
        //primaryStage.setResizable(false);
        primaryStage.setTitle("Boos Shop");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Images\\Icons\\Main Icon.png")));
        primaryStage.setResizable(false);
        //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Space Invaders.png")));
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
