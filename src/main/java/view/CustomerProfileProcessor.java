package view;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.account.AccountControl;
import controller.account.CustomerControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import model.existence.Log;
import notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerProfileProcessor extends AccountProcessor implements Initializable {
    AccountControl accountControl = AccountControl.getController();

    public ImageView infoImage;
    public Label infoLabel;
    public ImageView historyImage;
    public Label historyLabel;
    public ImageView cartImage;
    public Label cartLabel;
    public ImageView profile;
    public ImageView logout;
    public Pane infoPane;
    public Pane historyPane;
    public Pane cartPane;
    public Pane profilePane;
    public ImageView back;
    public Label profileLabel;
    private Stage myStage;
    public Pane pain;

    /**
     * Purchase.fxml
     */
    public ImageView backToCart;
    public JFXTextField postalCode;
    public JFXTextArea address;
    public JFXTextField phoneNumber;
    public JFXTextField name;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.toString().contains("Purchase.fxml")) {
            if (accountControl.getAccount().getFirstName() != null && accountControl.getAccount().getLastName() != null)
                name.setText(accountControl.getAccount().getFirstName() + " " + accountControl.getAccount().getLastName());
        } else {
            Stop[] stops = new Stop[]{
                    new Stop(0.5, Color.valueOf("#42878f")),
                    new Stop(1, Color.valueOf("#345f63"))
            };
            LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
            BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
            pain.setBackground(new Background(backgroundFill));
            DropShadow dropShadow = new DropShadow();
            dropShadow.setBlurType(BlurType.ONE_PASS_BOX);
            dropShadow.setColor(Color.valueOf("#4787bf"));
            dropShadow.setHeight(5);
            dropShadow.setWidth(5);
            dropShadow.setRadius(5);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(1);
            infoLabel.setEffect(dropShadow);
            historyLabel.setEffect(dropShadow);
            cartLabel.setEffect(dropShadow);
            profileLabel.setText(Control.getUsername());
        }
    }

    public Stage getMyStage() {
        return myStage;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public void enterInfo() {
        infoImage.setOpacity(1);
        infoLabel.setOpacity(1);
        infoPane.setStyle("-fx-background-color:#79dbf0; -fx-background-radius: 10");
    }

    public void exitInfo(MouseEvent event) {
        infoPane.setOpacity(0.7);
        infoLabel.setOpacity(0.7);
        infoPane.setStyle("-fx-background-color: #9ce7f0; -fx-background-radius: 10");
    }

    public void enterHistory(MouseEvent event) {
        historyImage.setOpacity(1);
        historyLabel.setOpacity(1);
        historyPane.setStyle("-fx-background-color:#79dbf0; -fx-background-radius: 10");
    }

    public void exitHistory(MouseEvent event) {
        historyImage.setOpacity(0.7);
        historyLabel.setOpacity(0.7);
        historyPane.setStyle("-fx-background-color: #9ce7f0; -fx-background-radius: 10");
    }

    public void enterCart(MouseEvent event) {
        cartImage.setOpacity(1);
        cartLabel.setOpacity(1);
        cartPane.setStyle("-fx-background-color:#79dbf0; -fx-background-radius: 10");

    }

    public void exitCart(MouseEvent event) {
        cartImage.setOpacity(0.7);
        cartLabel.setOpacity(0.7);
        cartPane.setStyle("-fx-background-color: #9ce7f0; -fx-background-radius: 10");
    }

    public void logoutEnter(MouseEvent event) {
        logout.setOpacity(1);
    }

    public void logoutExit(MouseEvent event) {
        logout.setOpacity(0.7);
    }

    public void enterProfile(MouseEvent event) {
        profile.setOpacity(1);
    }

    public void exitProfile(MouseEvent event) {
        profile.setOpacity(0.7);
    }

    public void enterBack(MouseEvent event) {
        back.setOpacity(1);
    }

    public void exitBack(MouseEvent event) {
        back.setOpacity(0.7);
    }

    public void showCart(MouseEvent event) {
        if(canOpenSubStage(Control.getUsername() + " Cart", this))
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerCartProducts.fxml"));
            Parent root = loader.load();
            ProductsProcessor processor = loader.getController();
            processor.parentProcessor = this;
            processor.initProcessor(ProductsProcessor.ProductsMenuType.CUSTOMER_CART);
            Stage stage = new Stage();
            stage.getIcons().add(new Image("Images/Icons/cart (2).png"));
            stage.setTitle(Control.getUsername() + " Cart");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            this.subStages.add(stage);
            processor.setMyStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBuyLogs(MouseEvent mouseEvent) {
        if (canOpenSubStage("Show Buy Logs", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Log> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(this);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.LOGS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                //newStage.getIcons().add(new Image(getClass().getResourceAsStream("view accounts icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Show Buy Logs");
                this.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void purchase(ActionEvent event) {
        name.setBorder(null);
        phoneNumber.setBorder(null);
        address.setBorder(null);
        postalCode.setBorder(null);
        if (!name.getText().isEmpty() &&
                !phoneNumber.getText().isEmpty() &&
                !address.getText().isEmpty() &&
                !postalCode.getText().isEmpty()) {
                if (areFieldsOk()) {
                    doThePurchase();
                }
        } else {
            if(name.getText().isEmpty()) {
                name.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(phoneNumber.getText().isEmpty()) {
                phoneNumber.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(address.getText().isEmpty()) {
                address.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(postalCode.getText().isEmpty()) {
                postalCode.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
        }
    }

    private void doThePurchase() {
        Notification notification = CustomerControl.getController().purchase();
        Optional<ButtonType> buttonType = notification.getAlert().showAndWait();

        if(buttonType.get() == ButtonType.OK)
            backToCart(null);
    }

    private boolean areFieldsOk() {
        if (!phoneNumber.getText().matches("\\d+")) {
            phoneNumber.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            return false;
        }
        if (!postalCode.getText().matches("\\d+")) {
            postalCode.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            return false;
        }
        if (phoneNumber.getText().length() != 11) {
            phoneNumber.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            return false;
        }
        if (postalCode.getText().length() < 5) {
            postalCode.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            return false;
        }
        return true;
    }

    public void backToCart(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomerCartProducts.fxml"));
            Parent root = fxmlLoader.load();
            ProductsProcessor productsProcessor = fxmlLoader.getController();
            productsProcessor.parentProcessor = parentProcessor;
            productsProcessor.initProcessor(ProductsProcessor.ProductsMenuType.CUSTOMER_CART);
            myStage.getIcons().remove(0);
//            myStage.getIcons().add(new Image(""));
            productsProcessor.setMyStage(myStage);
            myStage.setScene(new Scene(root));
            myStage.setResizable(false);
            myStage.setTitle(Control.getUsername() + " Cart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
