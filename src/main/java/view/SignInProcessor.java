package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInProcessor{

    public JFXTextField userNameField;
    public JFXPasswordField passwordField;

    public Stage getMyStage() {
        return myStage;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public Stage myStage;

    public void changeSignUpColor(MouseEvent mouseEvent) {
        JFXButton button = (JFXButton) mouseEvent.getSource();
        button.setTextFill(Color.valueOf("#b0bec5"));
    }

    public void loginChangeColor(MouseEvent mouseEvent) {
        JFXButton button = (JFXButton) mouseEvent.getSource();
        button.setTextFill(Color.BLACK);
    }

    public void login(MouseEvent mouseEvent) {
        //TODO
    }


    public void enterSignUpMenu(MouseEvent mouseEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignUpMenu.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO
    }
}
