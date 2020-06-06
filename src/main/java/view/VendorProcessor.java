package view;

import controller.Control;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.net.URL;
import java.util.ResourceBundle;

public class VendorProcessor implements Initializable {
    public Pane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stop[] stops = new Stop[] {
                /*new Stop(0, Color.DARKSLATEBLUE),
                new Stop(1, Color.DARKRED)*/
                new Stop(0, Color.valueOf("#360033")),
                new Stop(1, Color.valueOf("#127183"))
        };
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
        mainPane.setBackground(new Background(backgroundFill));
        Pane pane = (Pane) mainPane.getChildren().get(1);
        Label label = (Label) pane.getChildren().get(1);
        label.setText("Sepehr");
    }

    public void showProfile(MouseEvent mouseEvent) {
        //TODO
    }

    public void logout(MouseEvent mouseEvent) {
        Control.setLoggedIn(false);
        Control.setUsername(null);
        Control.setType(null);
        //TODO
    }
}
