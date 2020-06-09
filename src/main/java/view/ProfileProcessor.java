package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.existence.Account;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileProcessor implements Initializable {
    private static Account account;

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account account) {
        ProfileProcessor.account = account;
    }

    public BorderPane mainPane;
    public Pane profileMenusPane;
    public Pane profileInfoPane, profilePasswordPane, profileCreditPane;
    public Pane profileInfoButton, profilePasswordButton, profileCreditButton;
    public HBox optionsHBox;

    public JFXTextField usernameField, firstNameField, lastNameField, emailField, creditField, brandField;
    public Label creditLabel, brandLabel;
    public JFXButton saveChangesButton;

    public Circle imageFieldCircle;
    public Rectangle rightLine, rightLine1;

    public JFXTextField currentCreditField, additionCreditField;
    public JFXButton subButton, addButton;

    public JFXPasswordField oldPasswordField, newPasswordField;
    public JFXButton changePasswordButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String locationFile = location.getFile();

        if(locationFile.contains("ProfileMenu")) {
            setStyleSheets();
            adjustMainPanesForAccounts();
        } else if(locationFile.contains("profileInfoMenu")) {
            setProfileInfoFields();
        } else if(locationFile.contains("profileCreditMenu")) {
            setProfileCreditFields();
        } else if(locationFile.contains("profilePasswordMenu")) {
            setProfilePasswordFields();
        }

    }

    private void adjustMainPanesForAccounts() {
        changeCenterPane("profileInfoMenu");
        changeButtonBackGroundColor(profileInfoButton);

        if(account.getType().equals("Admin")) {
            optionsHBox.getChildren().remove(profileCreditButton);
        }
        if(!account.getUsername().equals(Control.getUsername())) {
            optionsHBox.getChildren().remove(profilePasswordButton);
        }
    }

    private void setProfileInfoFields() {
        usernameField.setText(account.getUsername());
        firstNameField.setText(account.getFirstName());
        lastNameField.setText(account.getLastName());
        emailField.setText(account.getEmail());

        if(account.getType().equals("Vendor"))
            brandField.setText(account.getBrand());
        else {
            profileInfoPane.getChildren().remove(brandField);
            profileInfoPane.getChildren().remove(brandLabel);
        }

        if(account.getType().equals("Admin")) {
            profileInfoPane.getChildren().remove(creditField);
            profileInfoPane.getChildren().remove(creditLabel);
        } else {
            creditField.setText(Double.toString(account.getCredit()));
        }

        if(!account.getUsername().equals(Control.getUsername())) {
            profileInfoPane.getChildren().remove(saveChangesButton);
        }
    }

    private void setProfileCreditFields() {
        currentCreditField.setText(Double.toString(account.getCredit()));

        if(!account.getUsername().equals(Control.getUsername())) {
            profileCreditPane.getChildren().remove(addButton);
            profileCreditPane.getChildren().remove(subButton);
        }
    }

    private void setProfilePasswordFields() {
        //Todo
    }

    public void setStyleSheets() {
        //Todo
        String accountType = Control.getType(), styleSheet;
        ObservableList<String> styleSheets = mainPane.getStylesheets();
        styleSheets.removeAll(styleSheets);

        if(accountType.equals("Admin")) {
            styleSheet = "AdminProfileMenu.css";
        } else if(accountType.equals("Vendor")) {
            styleSheet = "VendorProfileMenu.css";
        } else {
            styleSheet = "CustomerProfileMenu.css";
        }
        styleSheets.add(getClass().getResource(styleSheet).toExternalForm());
    }

    public void upPaneMouseClicked(MouseEvent mouseEvent) {
        changeButtonBackGroundColor((Pane) mouseEvent.getSource());
        changeCenterPane(getCenterPaneName(((Pane) mouseEvent.getSource()).getId()));
        //Todo
    }

    public String getCenterPaneName(String paneId) {
        String centerPaneName1 = paneId.substring(0, paneId.length() - 6).concat("Menu");
        return centerPaneName1;
    }

    public void changeCenterPane(String centerPaneName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(centerPaneName + ".fxml"));
            Parent subRoot = loader.load();
            loader.setController(this);
            mainPane.setBottom(subRoot);
            Pane pane = (Pane) mainPane.getBottom();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearButtonBackGrounds() {
        profileInfoButton.setBackground(Background.EMPTY);
        profileCreditButton.setBackground(Background.EMPTY);
        profilePasswordButton.setBackground(Background.EMPTY);
    }

    public void changeButtonBackGroundColor(Pane pane) {
        clearButtonBackGrounds();
        BackgroundFill backgroundFill = new BackgroundFill(getButtonBackGroundColor(), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        pane.setBackground(background);
    }

    private Color getButtonBackGroundColor() {
        Color color = null;

        switch (Control.getType()) {
            case "Admin":
                color = Color.valueOf("#80CBC4");
                break;
            case "Vendor":
                color = Color.valueOf("#233C5E");
                break;
            case "Customer":
                color = Color.valueOf("#56B6BF");
                break;
        }

        return color;
    }

    public void editPersonalInfoMouseClicked(MouseEvent mouseEvent) {

    }

    private void editField(String fieldName, String newValue) {

    }
}
