package view;

import controller.product.ProductControl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.existence.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProductsProcessor implements Initializable {
    private static final int PRODUCT_SCROLL_PANE_WIDTH = 1050;
    private static final int PRODUCT_FIELD_HEIGHT = 335;
    private static final int PRODUCT_PAGES_BAR_HEIGHT = 50;
    public ScrollPane productsScrollPane;
    //ProductPane
    public ImageView productImage;
    public Label productNameLabel;
    public Label viewLabel;
    public ImageView availabelImage;
    public Label availableLable;
    public Label oldPriceLabel;
    public Label newPriceLabel;
    //Product Pane
    private ArrayList<Product> allProducts;
    private int pageSize = 12;
    private int pageNumber = 0;
    private int productFieldsNumber;
    private int pageLim;
    private ProductsProcessor parentProcessor;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.toString().contains("ProductsMenu")) {
            getAllProducts();
            initProductsPage();
        }
    }

    private void initProductsPage() {
        try {
            BorderPane borderPane = new BorderPane();
            pageLim = allProducts.size() -(pageNumber * pageSize);
            productFieldsNumber = (pageLim < 9 ? 8 : 12);
            double borderPaneHeight = ((productFieldsNumber/4) * PRODUCT_FIELD_HEIGHT) + PRODUCT_PAGES_BAR_HEIGHT;
            System.out.println(borderPaneHeight);
            borderPane.setPrefSize(PRODUCT_SCROLL_PANE_WIDTH - 50, borderPaneHeight);
            productsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductsPagesBar.fxml"));
            Parent root = loader.load();
            ((ProductsProcessor)loader.getController()).setParentProcessor(this);
            borderPane.setBottom(root);
            GridPane gridPane = new GridPane();
            gridPane.getChildren().addAll(getProductsPanes());
            gridPane.setMinWidth(Control.USE_COMPUTED_SIZE); gridPane.setMaxWidth(Control.USE_COMPUTED_SIZE); gridPane.setPrefWidth(Control.USE_COMPUTED_SIZE);
            gridPane.setMinHeight(Control.USE_COMPUTED_SIZE); gridPane.setMaxHeight(Control.USE_COMPUTED_SIZE); gridPane.setPrefHeight(Control.USE_COMPUTED_SIZE);
            borderPane.setCenter(gridPane);
            productsScrollPane.setContent(borderPane);
        } catch (IOException e) { e.printStackTrace(); }

    }

    private ArrayList<HBox> getProductsPanes() throws IOException {
/*        for (Product product : allProducts) {
            System.out.println(product.getName());
        }*/
        ArrayList<HBox> hBoxes = new ArrayList<>();
        for(int y = 0; y < productFieldsNumber/4; ++y) {
            for(int x = 0; x < 4; ++ x) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                GridPane.setConstraints(hBox, x, y);
                hBox.setMinWidth(257); hBox.setMaxWidth(257); hBox.setPrefWidth(257);
                hBox.setMinHeight(PRODUCT_FIELD_HEIGHT); hBox.setMaxHeight(PRODUCT_FIELD_HEIGHT); hBox.setPrefHeight(PRODUCT_FIELD_HEIGHT);
                hBoxes.add(hBox);
            }
        }
        for(int i = 0; i < pageLim; ++i) {
            hBoxes.get(i).getChildren().add(getProductPane(i));
        }
        //System.out.println(hBoxes.size());
        return hBoxes;
    }

    private Pane getProductPane(int productNumberInPage) throws IOException {
        Product product = allProducts.get(pageNumber * pageSize + productNumberInPage);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductPane.fxml"));
        Pane root = loader.load();
        ProductsProcessor productsProcessor = loader.getController();
        productsProcessor.productImage.setImage(ProductControl.getController().getProductImageByID(product.getID()));
        productsProcessor.productNameLabel.setText(product.getName());
        if(ProductControl.getController().isThereProductInOff(product.getID())) {
            System.out.println("Product In Off");
        } else {
            root.getChildren().remove(productsProcessor.newPriceLabel);
            productsProcessor.oldPriceLabel.setText(product.getPrice() +"$");
        }
        productsProcessor.viewLabel.setText("" + product.getSeen());
        if(!(product.getStatus() == 1 && product.getCount() > 0)) {
            productsProcessor.availabelImage.setImage(new Image("Images\\Icons\\ProductsMenu\\unavailable.png"));
            productsProcessor.availableLable.setText((product.getStatus() != 1 ? "Editing" : "Out Of Stock"));
        }
        return root;
    }

    private void getAllProducts() {
        allProducts = ProductControl.getController().getAllShowingProducts();
        //System.out.println(allProducts.size());
    }

    public void setParentProcessor(ProductsProcessor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }
}
