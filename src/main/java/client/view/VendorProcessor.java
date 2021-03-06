package client.view;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
import server.model.existence.Log;
import server.model.existence.Off;

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


        loggedInAccount = getLoggedInAccount();
        Pane pane = (Pane) mainPane.getChildren().get(1);
        Label label = (Label) pane.getChildren().get(1);
        label.setText(loggedInAccount.getUsername());
        initAudios();
        initMusicPlayer();
    }

    public void showProducts() {
        if(canOpenSubStage(loggedInAccount.getUsername() + " Products", this)) {
            Stage stage = new Stage();
            stage.setTitle(loggedInAccount.getUsername() + " Products");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VendorAddProducts.fxml"));
            Parent parent = null;
            try {
                parent = loader.load();
                ProductsProcessor processor = loader.getController();
                processor.setParentProcessor(this);
                processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_PRODUCTS);
            } catch (IOException e) {
                //:)
            }
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            addSubStage(stage);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("product.png")));
            stage.show();
        }

    }

    public void manageOffs(MouseEvent mouseEvent) {
        if (canOpenSubStage("Manage Offs", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Off> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(this);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.VENDOR_OFFS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("Offs Icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Manage Offs");
                this.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }


    public void showSellLogs(MouseEvent mouseEvent) {
        if (canOpenSubStage("Show Sell Logs", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Log> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(this);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.LOGS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("checklist.png")));
                newStage.setResizable(false);
                newStage.setTitle("Show Sell Logs");
                this.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }
}
