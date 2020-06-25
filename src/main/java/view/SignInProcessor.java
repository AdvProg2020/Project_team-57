package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.IOException;
import java.util.Optional;

public class SignInProcessor extends Processor {
    private static IOControl ioControl = IOControl.getController();

    public JFXTextField userNameField;
    public JFXPasswordField passwordField;

    public void login(ActionEvent actionEvent) {
        if(userNameField.getText().equals("") || passwordField.getText().equals("")) {
          if(userNameField.getText().equals(""))
              userNameField.setStyle(errorTextFieldStyle);
          if(passwordField.getText().equals(""))
              passwordField.setStyle(errorTextFieldStyle);
        } else {
            Alert alert = ioControl.login(new Account(userNameField.getText(), passwordField.getText())).getAlert();
            Optional<ButtonType> optionalButtonType = alert.showAndWait();
            if(optionalButtonType.get() == ButtonType.OK) {
                if(alert.getHeaderText().equals("Congratulations"))
                    myStage.hide();
                if(alert.getHeaderText().contains("Username"))
                    userNameField.setStyle(errorTextFieldStyle);
                if(alert.getHeaderText().contains("Password"))
                    passwordField.setStyle(errorTextFieldStyle);
            }
        }

    }

    public void enterSignUpMenu(MouseEvent mouseEvent) {
        SignUpProcessor.setIsNormal(true);
        Parent root1 = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpMenu.fxml"));
            root1 = loader.load();
            SignUpProcessor signUpProcessor = loader.getController();
            signUpProcessor.setMyStage(myStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setTitle("Sign Up Menu");
        myStage.setScene(new Scene(root1));
    }

    public void signInTextFieldMouseClicked(Event event) {
        ((TextField) event.getSource()).setStyle("");
    }

}
