package view;

import controller.Control;
import controller.account.AdminControl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import model.existence.Comment;
import model.existence.Off;
import model.existence.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VendorProcessor extends AccountProcessor implements Initializable {
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
        label.setText(Control.getUsername());
    }

    public void showProducts(){
        if(canOpenSubStage(Control.getUsername() + " Products", this)) {
            Stage stage = new Stage();
            stage.setTitle(Control.getUsername() + " Products");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VendorAddProducts.fxml"));
            Parent parent = null;
            try {
                parent = loader.load();
                ProductsProcessor processor = loader.getController();
                processor.setParentProcessor(this);
                processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_PRODUCTS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            addSubStage(stage);
            stage.show();
        }

    }

    public void addOff(MouseEvent mouseEvent) {
        if (canOpenSubStage("Manage Offs", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Off> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(this);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.VENDOR_OFFS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                //newStage.getIcons().add(new Image(getClass().getResourceAsStream("view accounts icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Manage Offs");
                this.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
