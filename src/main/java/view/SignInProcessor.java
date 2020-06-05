package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.IOControl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.IOException;
import java.util.Optional;

public class SignInProcessor{
    private static IOControl ioControl = IOControl.getController();

    public JFXTextField userNameField;
    public JFXPasswordField passwordField;

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public Stage myStage;

    public void changeSignUpColorWhenEntered(MouseEvent mouseEvent) {
        JFXButton button = (JFXButton) mouseEvent.getSource();
        button.setTextFill(Color.valueOf("#b0bec5"));
    }

    public void changeSignUpColorWhenExited(MouseEvent mouseEvent) {
        JFXButton button = (JFXButton) mouseEvent.getSource();
        button.setTextFill(Color.BLACK);
    }

    public void loginChangeColorWhenEntered(MouseEvent mouseEvent) {
        JFXButton button = (JFXButton) mouseEvent.getSource();
        button.setTextFill(Color.BLACK);
    }

    public void loginChangeColorWhenExited(MouseEvent mouseEvent) {
        JFXButton button = (JFXButton) mouseEvent.getSource();
        button.setTextFill(Color.WHITE);
    }

    public void login(MouseEvent mouseEvent) {
        Alert alert = ioControl.login(new Account(userNameField.getText(), passwordField.getText()));

        if(alert.getHeaderText().equals("Login Successful")) {
            Optional<ButtonType> optionalButtonType = alert.showAndWait();
            if(optionalButtonType.get() == ButtonType.OK) {
                myStage.hide();
            }
        }

        alert.show();
    }


    public void enterSignUpMenu(MouseEvent mouseEvent) {
        Parent root1 = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            root1 = loader.load();
            SignUpProcessor signUpProcessor = loader.getController();
            signUpProcessor.setMyStage(myStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setTitle("Sign Up");
        myStage.setScene(new Scene(root1));
    }
}
