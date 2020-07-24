package client.view;

import client.api.Command;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import server.controller.account.CustomerControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import server.model.existence.Account;
import server.model.existence.Log;
import notification.Notification;
import server.model.existence.Product;
import server.server.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
    public Pane discountPane;
    public Pane historyPane;
    public Pane cartPane;
    public ImageView back;
    private Stage myStage;
    public Pane pain;
    public JFXButton profileButton;

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
             loggedInAccount = getLoggedInAccount();
            if (loggedInAccount.getFirstName() != null && loggedInAccount.getLastName() != null)
                name.setText(loggedInAccount.getFirstName() + " " + loggedInAccount.getLastName());
        } else if(location.toString().contains("CustomerMenu.fxml")){
            loggedInAccount = getLoggedInAccount();
            initAudios();
            initMusicPlayer();
            profileButton.setText(loggedInAccount.getUsername());
        }
    }

    public Stage getMyStage() {
        return myStage;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public void showCart(MouseEvent event) {
        if(canOpenSubStage(loggedInAccount.getUsername() + " Cart", this))
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerCartProducts.fxml"));
            Parent root = loader.load();
            ProductsProcessor processor = loader.getController();
            processor.parentProcessor = this;
            processor.initProcessor(ProductsProcessor.ProductsMenuType.CUSTOMER_CART);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(IMAGE_FOLDER_URL + "Icons/cart (2).png"));
            stage.setTitle(loggedInAccount.getUsername() + " Cart");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            this.subStages.add(stage);
            processor.setMyStage(stage);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("cartCustomer.png")));
            stage.show();
        } catch (IOException e) {
            //:)
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
        Notification notification = purchaseAndGetResult()/*CustomerControl.getController().purchase()*/;
        Optional<ButtonType> buttonType = notification.getAlert().showAndWait();

        if(buttonType.get() == ButtonType.OK)
            backToCart(null);
    }

    private Notification purchaseAndGetResult() {
        Command command = new Command("purchase", Command.HandleType.PRODUCT);
        return client.postAndGet(command, Response.class, (Class<Object>)Object.class).getMessage();
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
//            myStage.getIcons().remove(0);
//            myStage.getIcons().add(new Image(""));
            productsProcessor.setMyStage(myStage);
            myStage.setScene(new Scene(root));
            myStage.setResizable(false);
            myStage.setTitle(loggedInAccount.getUsername() + " Cart");
        } catch (IOException e) {
            //:)
        }
    }

    public void showCustomerDiscountCodes(MouseEvent mouseEvent) {
        if (canOpenSubStage(loggedInAccount.getUsername() + " Discount Codes", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(this);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.CUSTOMER_DISCOUNTS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("discount menu customer.png")));
                newStage.setResizable(false);
                newStage.setTitle(loggedInAccount.getUsername() + " Discount Codes");
                this.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

    public void openLadiesMenu(MouseEvent mouseEvent) {
        if (canOpenSubStage("Buy History", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerLogsMenu.fxml"));
                Parent root = loader.load();
                CustomerProfileProcessor processor = loader.getController();
                processor.setParentProcessor(this);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("buyHistoryCustomer.png")));
                newStage.setResizable(false);
                newStage.setTitle("Buy History");
                this.addSubStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

    public void showBuyLogs(MouseEvent mouseEvent) {
        if (canOpenSubStage("Invoices", parentProcessor)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Log> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(parentProcessor);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.LOGS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("customer invoice.png")));
                newStage.setResizable(false);
                newStage.setTitle("Invoices");
                parentProcessor.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

    public void showPurchasedFiles(MouseEvent mouseEvent) {
        if (canOpenSubStage("Purchased Files", parentProcessor)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Product.ProductFileInfo> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(parentProcessor);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.FILES);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("customer invoice.png")));
                newStage.setResizable(false);
                newStage.setTitle("Purchased Files");
                parentProcessor.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

    public void showSupporters(ActionEvent actionEvent) {
        if (canOpenSubStage("Available Supporter", this)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(this);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.AVAILABLE_SUPPORTERS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("admin supporters icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Available Supporter");
                this.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

}
