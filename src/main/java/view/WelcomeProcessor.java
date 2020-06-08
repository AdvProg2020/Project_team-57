package view;

import com.jfoenix.controls.JFXButton;
import controller.Control;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeProcessor implements Initializable {
    public Pane mainPane;
    public JFXButton accountMenuButton;
    public JFXButton productsMenuButton;
    public JFXButton offsMenuButton;
    private IOControl ioControl = IOControl.getController();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setBG();
    }

    private void setBG() {
        Image image = new Image("Images\\Backgrounds\\WelcomeMenuBG.jpg");
        Background background = new Background(
                new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                        new BackgroundSize(mainPane.getWidth(), mainPane.getHeight(), false, false, true, false)));
        mainPane.setBackground(background);
    }

    public void openAccountMenu(ActionEvent actionEvent) {
        if(IOControl.isLoggedIn()) {
            try {
                Parent root = null;
                Main.getStage().getIcons().remove(0);
                switch (Control.getType()) {
                    case "Admin" :
                        root = FXMLLoader.load(Main.class.getResource("AdminMenu.fxml"));
                        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Admin Icon.png")));
                        break;
                    case "Vendor" :
                        root = FXMLLoader.load(Main.class.getResource("VendorMenu.fxml"));
                        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Vendor Icon.png")));
                        break;
                    case "Customer" :
                        root = FXMLLoader.load(Main.class.getResource("CustomerProfile.fxml"));
                        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Customer Icon.png")));
                        break;
                }
                Main.setScene(Control.getUsername() + " Menu", root);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignInMenu.fxml"));
                Parent root = loader.load();
                SignInProcessor signInProcessor = loader.getController();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                signInProcessor.setMyStage(newStage);
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("Login Icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Sign In");
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void buttonOnMouse(MouseEvent mouseEvent) {
        String onMouseStyle = "-fx-background-radius: 10 10 10 10; -fx-border-radius: 10 10 10 10; -fx-background-color: #fafafa; -fx-text-fill: #b0bec5; -fx-border-color: #fafafa; -fx-border-width: 1.5; -fx-cursor: hand;";
        switch (((JFXButton)mouseEvent.getSource()).getText()) {
            case "Account Menu" :
                accountMenuButton.setStyle(onMouseStyle);
                break;
            case "Products Menu" :
                productsMenuButton.setStyle(onMouseStyle);
                break;
            case "Offs Menu" :
                offsMenuButton.setStyle(onMouseStyle);
                break;
        }
    }

    public void buttonOutMouse(MouseEvent mouseEvent) {
        String outMouseStyle = "-fx-background-radius: 10 10 10 10; -fx-border-radius: 10 10 10 10; -fx-background-color: #b0bec5; -fx-text-fill: #fafafa; -fx-border-color: #fafafa; -fx-border-width: 1.5;";
        switch (((JFXButton)mouseEvent.getSource()).getText()) {
            case "Account Menu" :
                accountMenuButton.setStyle(outMouseStyle);
                break;
            case "Products Menu" :
                productsMenuButton.setStyle(outMouseStyle);
                break;
            case "Offs Menu" :
                offsMenuButton.setStyle(outMouseStyle);
                break;
        }
    }
}
