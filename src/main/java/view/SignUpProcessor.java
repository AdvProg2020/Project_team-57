package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SignUpProcessor implements Initializable {
    private static boolean isNormal = true;
    public Button signUp;
    public JFXTextField username;
    public JFXPasswordField password;
    public JFXTextField name;
    public JFXTextField lastName;
    public ImageView back;
    private final IOControl ioControl = IOControl.getController();
    public ImageView backImage;
    public JFXComboBox<String> accountTypeComboBox;
    public ImageView imageOfSignUp;
    public AnchorPane pane;
    private Stage myStage;

    public static void setIsNormal(boolean isNormal) {
        SignUpProcessor.isNormal = isNormal;
    }

    public void register(ActionEvent event) {
        if (!isTextFieldEmpty()) {
            final Account account = new Account(username.getText(), password.getText());
            account.setFirstName(name.getText());
            account.setLastName(lastName.getText());
            account.setType(getAccountType());
            Alert alert = ioControl.register(account).getAlert();
            if (alert.getTitle().equals("Successful")) {
                alert.show();
                signUp.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5))));
                if(isNormal)
                    backToSignInMenu(null);
                else {
                    username.deleteText(0, username.getText().length());
                    password.deleteText(0, password.getText().length());
                    name.deleteText(0, name.getText().length());
                    lastName.deleteText(0, lastName.getText().length());
                }
                return;
            }
            showError(alert);
        }
    }

    private void showError(Alert alert) {
        if (alert.getHeaderText().equals("Username Length Not Valid") ||
            alert.getHeaderText().equals("Username Format Not Valid") ||
            alert.getHeaderText().equals("Full Username")) {
            username.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else if (alert.getHeaderText().equals("Password Length Not Valid") ||
                   alert.getHeaderText().equals("Password Format Not Valid")) {
            password.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else if (alert.getHeaderText().equals("Invalid FirstName")) {
            name.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else if (alert.getHeaderText().equals("Invalid LastName")) {
            lastName.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else
            alert.show();
    }

    private String getAccountType() {
        if(isNormal) {
            if(!ioControl.isThereAdmin()) {
                if(accountTypeComboBox.getSelectionModel().isSelected(0))
                    return "Admin";
                else if(accountTypeComboBox.getSelectionModel().isSelected(1))
                    return "Vendor";
                else
                    return "Customer";
            } else
                return (accountTypeComboBox.getSelectionModel().isSelected(0) ? "Vendor" : "Customer");
        }
        return "Admin";
    }

    private boolean isTextFieldEmpty() {
        lastName.setBorder(null);
        name.setBorder(null);
        password.setBorder(null);
        username.setBorder(null);

        if(username.getText().isEmpty() || password.getText().isEmpty()
                || name.getText().isEmpty() || lastName.getText().isEmpty()) {
            if(username.getText().isEmpty()) {
                username.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(password.getText().isEmpty()) {
                password.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(name.getText().isEmpty()) {
                name.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(lastName.getText().isEmpty()) {
                lastName.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            return true;
        }
        return false;
    }

    public void backToSignInMenu(MouseEvent event) {
        Parent root1 = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignInMenu.fxml"));
            root1 = loader.load();
            SignInProcessor signInProcessor = loader.getController();
            signInProcessor.setMyStage(myStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setTitle("Sign In");
        myStage.setScene(new Scene(root1));
    }

    public void onMouse(MouseEvent event) {
        backImage.setOpacity(0.7);
        password.setStyle("-fx-prompt-text-fill: #607d8b");
    }

    public void outMouse(MouseEvent event) {
        backImage.setOpacity(0.4);
        password.setStyle(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAccTypeComboBox();
    }

    private void initAccTypeComboBox() {
        if(isNormal) {
            if(!ioControl.isThereAdmin()) {
                accountTypeComboBox.getItems().add("Admin");
            }
            accountTypeComboBox.getItems().add("Vendor");
            accountTypeComboBox.getItems().add("Customer");
            accountTypeComboBox.getSelectionModel().selectFirst();
        } else {
            imageOfSignUp.setImage(new Image("Images\\Backgrounds\\steve jobs.png"));
            pane.getChildren().remove(backImage);
            accountTypeComboBox.getItems().add("Admin");
            accountTypeComboBox.getSelectionModel().selectFirst();
        }

    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;

    }

    public void textFieldMouseClicked(MouseEvent mouseEvent) {
        ((JFXTextField) mouseEvent.getSource()).setBorder(null);
    }

    public void passwordFieldMouseClicked(MouseEvent mouseEvent) {
        ((JFXPasswordField) mouseEvent.getSource()).setBorder(null);
    }
}
