package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.account.AccountControl;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.existence.Account;
import notification.Notification;

import javax.swing.text.html.ImageView;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileProcessor implements Initializable {
    private static AccountControl accountControl = AccountControl.getController();
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

    public StackPane profilePictureStackPane;
    public Circle pictureCircle;
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
            optionsHBox.getChildren().remove(profileCreditButton);
        }
    }

    private void setProfileInfoFields() {
        usernameField.setText(account.getUsername());
        firstNameField.setText(account.getFirstName());
        lastNameField.setText(account.getLastName());
        emailField.setText(account.getEmail());

        ImagePattern imagePattern = new ImagePattern(accountControl.getProfileImageByUsername(account.getUsername()));
        pictureCircle.setFill(imagePattern);

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

            firstNameField.setEditable(false);
            firstNameField.setDisable(true);

            lastNameField.setEditable(false);
            lastNameField.setDisable(true);

            emailField.setEditable(false);
            emailField.setDisable(true);

            brandField.setEditable(false);
            brandField.setDisable(true);

            pictureCircle.setOnMouseClicked(null);
            pictureCircle.setOnMouseEntered(null);
            pictureCircle.setOnMouseExited(null);
        }
    }

    private void setProfileCreditFields() {
        currentCreditField.setText(Double.toString(account.getCredit()));

        if(!account.getUsername().equals(Control.getUsername())) {
            profileCreditPane.getChildren().remove(addButton);
            profileCreditPane.getChildren().remove(subButton);
        }

        additionCreditField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                if(newValue.equals(".")) {
                    additionCreditField.setText("0.");
                } else if (!newValue.matches("\\d+(.(\\d)+)?")) {
                    if(additionCreditField.getText().contains(".")) {
                        additionCreditField.setText(removeDots(additionCreditField.getText()));
                    } else {
                        additionCreditField.setText(newValue.replaceAll("[^\\d\\.]", ""));
                    }
                }
            }
        });
    }

    private String removeDots(String text) {
        StringBuilder stringBuilder = new StringBuilder(text);
        boolean foundDot = false;
        int textSize = text.length();

        for (int i = 0; i < textSize; i++) {
            if(text.charAt(i) < 48 || text.charAt(i) > 57) {
                if(text.charAt(i) == '.') {
                    if(foundDot) {
                        stringBuilder.deleteCharAt(i);
                        textSize--;
                    }
                    foundDot = true;
                } else {
                    stringBuilder.deleteCharAt(i);
                    textSize--;
                }
            }
        }

        return stringBuilder.toString();
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
        //Todo
        Alert alert = null;
        alert = editField("FirstName", firstNameField, alert);
        alert = editField("LastName", lastNameField, alert);
        alert = editField("Email", emailField, alert);

        if(account.getType().equals("Vendor"))
            alert = editField("Brand", brandField, alert);

        if(alert.getTitle().equals("Edit Successful")) {
            account = accountControl.getAccountByUsername(account.getUsername());
            firstNameField.setText(account.getFirstName());
            lastNameField.setText(account.getLastName());
            lastNameField.setText(account.getLastName());
            emailField.setText(account.getEmail());
            brandField.setText(account.getBrand());
        }

        alert.show();
    }

    private Alert editField(String fieldName, JFXTextField textField, Alert previousAlert) {
        Alert alert = accountControl.editField(fieldName, textField.getText()).getAlert();

        if(!alert.getTitle().equals("Edit Successful"))
            textField.setStyle("-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;");

        if(previousAlert == null || previousAlert.getTitle().equals("Edit Successful"))
            return alert;
        else
            return previousAlert;
    }

    public void changePasswordMouseClicked(MouseEvent mouseEvent) {
        Alert alert = accountControl.changePassword(oldPasswordField.getText(), newPasswordField.getText()).getAlert();

        String style = "-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;";
        if(alert.getHeaderText().contains("New") || alert.getHeaderText().equals("Duplicate Password"))
            newPasswordField.setStyle(style);
        if(alert.getHeaderText().contains("Old"))
            oldPasswordField.setStyle(style);

        alert.show();
    }

    public void addMoneyMouseClicked(MouseEvent mouseEvent) {
        Notification notification = accountControl.addMoney(additionCreditField.getText());

        if(notification.equals(Notification.RISE_MONEY_SUCCESSFULLY)) {
            account = accountControl.getAccountByUsername(account.getUsername());
            currentCreditField.setText(Double.toString(account.getCredit()));
        } else {
            additionCreditField.setStyle("-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;");
        }

        notification.getAlert().show();
    }

    public void subtractMoneyMouseClicked(MouseEvent mouseEvent) {
        Notification notification = accountControl.getMoney(additionCreditField.getText());

        if(notification.equals(Notification.GET_MONEY_SUCCESSFULLY)) {
            account = accountControl.getAccountByUsername(account.getUsername());
            currentCreditField.setText(Double.toString(account.getCredit()));
        } else {
            additionCreditField.setStyle("-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;");
        }

        notification.getAlert().show();
    }

    public void textFieldMouseClicked(MouseEvent mouseEvent) {
        JFXTextField textField = (JFXTextField) mouseEvent.getSource();
        textField.setStyle("");
    }

    public void passwordFieldMouseClicked(MouseEvent mouseEvent) {
        JFXPasswordField passwordField = (JFXPasswordField) mouseEvent.getSource();
        passwordField.setStyle("");
    }

    public void chooseAccountPictureMouseClicked(MouseEvent mouseEvent) throws FileNotFoundException {
        FileChooser pictureChooser = new FileChooser();

        FileChooser.ExtensionFilter jpgExtensionFilter = new FileChooser.ExtensionFilter("JPG Files", "*.JPG");
        FileChooser.ExtensionFilter jpegExtensionFilter = new FileChooser.ExtensionFilter("JPEG Files", "*.JPEG");
        FileChooser.ExtensionFilter pngExtensionFilter = new FileChooser.ExtensionFilter("PNG Files", "*.PNG");
        FileChooser.ExtensionFilter bmpExtensionFilter = new FileChooser.ExtensionFilter("BMP Files", "*.BMP");

        pictureChooser.getExtensionFilters().add(jpgExtensionFilter);
        pictureChooser.getExtensionFilters().add(jpegExtensionFilter);
        pictureChooser.getExtensionFilters().add(pngExtensionFilter);
        pictureChooser.getExtensionFilters().add(bmpExtensionFilter);

        File pictureFile = pictureChooser.showOpenDialog(null);

        if(pictureFile != null) {
            FileInputStream fileInputStream = new FileInputStream(pictureFile);
            Image image = new Image(fileInputStream);

            //Todo Sending Image To Controller
            //Todo Showing Image

            accountControl.setAccountPicture(account.getUsername(), pictureFile);

            pictureCircle.setFill(new ImagePattern(image));
        }

    }

    public void profilePictureMouseEntered(MouseEvent mouseEvent) throws FileNotFoundException {
        Circle circle = new Circle(45);

        FileInputStream fileInputStream = new FileInputStream("src\\main\\resources\\Images\\ProfileInfoMenu - Camera.png");
        Image image = new Image(fileInputStream);
        ImagePattern imagePattern = new ImagePattern(image);
        circle.setFill(imagePattern);

        profilePictureStackPane.getChildren().add(circle);
        circle.setDisable(true);

        circle.translateYProperty().set(20);
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(circle.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

    }

    public void profilePictureMouseExited(MouseEvent mouseEvent) {
        //Todo

        Circle circle = (Circle) profilePictureStackPane.getChildren().get(1);

        Timeline timeline = new Timeline();
        circle.translateYProperty().set(0);
        KeyValue keyValue = new KeyValue(circle.translateYProperty(), 20, Interpolator.EASE_OUT);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(event -> profilePictureStackPane.getChildren().remove(circle));
        timeline.play();

    }
}
