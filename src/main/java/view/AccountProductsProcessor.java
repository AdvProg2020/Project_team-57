package view;

import controller.IOControl;
import controller.account.CustomerControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import model.existence.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AccountProductsProcessor implements Initializable {
    public Pane cartPane;
    public Pane vendorProductsPane;
    private CustomerControl control;
    private VendorControl vendorControl;
    public ListView<Product> list;
    public Label profileLabel;
    public Popup popup = new Popup();
    public Label totalPrice;
    public ImageView back;
    public ImageView profile;
    public ListView<Product> vendorList;
    public Label goodsNumber;
    public Button addProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.toString().contains("AccountProducts.fxml")) {
            control = CustomerControl.getController();
            initializeCart();
        } else {
            vendorControl = VendorControl.getController();
            initializeVendorProducts();
        }

    }

    private void initializeVendorProducts() {
        vendorProductsPane.setStyle("-fx-background-image: url(Images/mall-918472_1280.jpg);");
        goodsNumber.setText(vendorControl.getVendorProductIDs().size() + "");
        ObservableList<Product> products = FXCollections.observableArrayList(vendorControl.getAllProducts());
        vendorList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        vendorList.getItems().setAll(products);
        vendorList.setFixedCellSize(150);
        vendorList.setCellFactory(param -> new ListCell<Product>(){
            @Override
            protected void updateItem(final Product product, final boolean empty) {
                super.updateItem(product, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    final ImageView image = new ImageView(ProductControl.getController().getProductImageByID(product.getID(), 1));
                    image.setFitHeight(130);
                    image.setFitWidth(150);
                    Text name = new Text("\n     " + product.getName());
                    name.fontProperty().setValue(Font.font("Oswald", 20));
                    name.fillProperty().setValue(Color.valueOf("#328d97"));
                    Text category = new Text("\n   " + product.getCategory());
                    category.fontProperty().setValue(Font.font("Segoe Print", 15));
                    VBox texts = new VBox(name, category);
                    Text price;
                    if (product.getCount() == 0 && product.getAmount() == 0)
                        price = new Text("\n\n\n                          Unavailable");
                    else
                        price = new Text("\n\n\n                          " + product.getPrice() + " $");
                    price.fontProperty().setValue(Font.font(20));
                    HBox graphic = new HBox(image, texts, price);
                    setGraphic(graphic);
                }
            }
        });
    }

    private void initializeCart(){
        cartPane.setStyle("-fx-background-image: url(Images/cartPane.jpg);");
       // cartPane.setStyle("-fx-background-image: url(Images/cartImage.jpg);");
        totalPrice.setText(control.calculateCartTotalPrice() + " $");
        profileLabel.setText(IOControl.getUsername());
        ObservableList<Product> products = FXCollections.observableArrayList(control.getAllCartProducts());
        list.getItems().setAll(products);
        list.setFixedCellSize(150);
        list.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(final Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView image = new ImageView(ProductControl.getController().getProductImageByID(product.getID(), 1));
                    image.setFitHeight(130);
                    image.setFitWidth(150);
                    Text name = new Text("\n     " + product.getName());
                    name.fontProperty().setValue(Font.font("Oswald", 20));
                    name.fillProperty().setValue(Color.valueOf("#328d97"));
                    Text category = new Text("\n   " + product.getCategory());
                    category.fontProperty().setValue(Font.font("Segoe Print", 15));
                    VBox texts = new VBox(name, category);
                    Text price;
                    if (product.getCount() == 0 && product.getAmount() == 0)
                        price = new Text("\n\n\n                          Unavailable");
                    else
                        price = new Text("\n\n\n                          " + product.getPrice() + " $");
                    price.fontProperty().setValue(Font.font(20));
                    HBox graphic = new HBox(image, texts, price);
                    setGraphic(graphic);
                }
            }
        });
    }

    public void showOption(MouseEvent event) {
        if (list.getSelectionModel().getSelectedItem() == null) {
            popup.hide();
            return;
        }
        if (event.getButton() == MouseButton.PRIMARY) {
            if (event.getClickCount() == 1) {
                initPopUp(event);
            } else
                showProductInfo();
        }
    }

    private void showProductInfo() {

    }

    private void initPopUp(MouseEvent event) {
        Button inButton = new Button("Increase");
        ImageView inImage = new ImageView(new Image("Images/Icons/increase.png"));
        setButtonProperties(inButton, inImage);
        inButton.setOnAction(event1 -> {

        });
        setMouseEvent(inButton);
        ImageView deImage = new ImageView(new Image("Images/Icons/decrease.png"));
        Button deButton = new Button("Decrease");
        setMouseEvent(deButton);
        setButtonProperties(deButton, deImage);
        deButton.setOnAction(event12 -> {

        });
        ImageView reImage = new ImageView(new Image("Images/Icons/remove.png"));
        Button reButton = new Button("Remove");
        setMouseEvent(reButton);
        setButtonProperties(reButton, reImage);
        reButton.setOnAction(event13 -> {

        });
        VBox vBox = new VBox(inButton, deButton, reButton);
        popup.getContent().add(vBox);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.show(list, event.getScreenX(), event.getScreenY());
    }

    private void setMouseEvent(Button button) {
        button.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                button.setStyle("-fx-background-color: #428a85; -fx-text-alignment: left");
            }
        });

        button.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                button.setStyle("-fx-background-color: #6ca8a4; -fx-text-alignment: justify");
            }
        });
    }

    private void setButtonProperties(final Button button, final ImageView imageView) {
        imageView.setFitHeight(40);
        imageView.setFitWidth(35);
        button.setGraphic(imageView);
        button.setPrefWidth(120);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #6ca8a4; -fx-font: italic;");
    }

    public void hidePopUp(MouseEvent event) {
        popup.hide();
    }

    public void backToCustomerProfile(MouseEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("CustomerProfile.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.getStage().setTitle(IOControl.getUsername() + " Profile");
        assert root != null;
        Main.getStage().setScene(new Scene(root));
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

    public void showVendorOption(MouseEvent event) {

    }

    public void enterAddProduct(MouseEvent event) {
        addProduct.setStyle("-fx-background-color:  #d50000; -fx-translate-y: -1; -fx-translate-x: 1; -fx-opacity: 1");
    }

    public void exitAddProduct(MouseEvent event) {
        addProduct.setStyle("-fx-background-color:  #d50000; -fx-translate-y: 0; -fx-translate-x: 0; -fx-opacity: 0.7");
    }
}
