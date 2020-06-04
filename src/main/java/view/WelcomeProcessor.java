package view;

import com.jfoenix.controls.JFXButton;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
            System.out.println("Logged In Before");
        } else {
            System.out.println("Not Logged In");
        }
    }

    public void buttonOnMouse(MouseEvent mouseEvent) {
        String onMouseStyle = "-fx-background-radius: 10 10 10 10; -fx-border-radius: 10 10 10 10; -fx-background-color: #fafafa; -fx-text-fill: #b0bec5; -fx-border-color: #fafafa; -fx-border-width: 1.5;";
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
