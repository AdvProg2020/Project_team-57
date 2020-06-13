package view;

import controller.Control;
import controller.IOControl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerProfileProcessor extends AccountProcessor implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    public void returnSignInMenu(MouseEvent event) {
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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AccountProducts.fxml"));
            AccountProductsProcessor processor = new AccountProductsProcessor();
            Stage stage = new Stage();
            stage.getIcons().add(new Image("Images/Icons/cart (2).png"));
            stage.setTitle("Cart");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
