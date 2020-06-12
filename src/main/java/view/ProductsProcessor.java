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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import model.existence.Product;

import javax.swing.text.Style;
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
    public Label pageNumberLabel;
    public ImageView nextPageButton;
    public ImageView previousPageButton;
    //Product Pane
    private ArrayList<Product> allProducts;
    private int pageSize = 12;
    private int pageNumber = 0;
    private int productFieldsNumber;
    private int pageLim;
    private ProductsProcessor parentProcessor;
    private ProductControl productControl = ProductControl.getController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.toString().contains("ProductsMenu")) {
            productControl.initSort(); productControl.initFilter();
            getAllProducts();
            initProductsPage();
        }
    }

    private void initProductsPage() {
        try {
            BorderPane borderPane = new BorderPane();
            pageLim = (allProducts.size() -(pageNumber * pageSize) < 12 ? (allProducts.size() -(pageNumber * pageSize)) : 12);
            productFieldsNumber = (pageLim < 9 ? 8 : 12);
            double borderPaneHeight = ((productFieldsNumber/4) * PRODUCT_FIELD_HEIGHT) + PRODUCT_PAGES_BAR_HEIGHT;
            borderPane.setPrefSize(PRODUCT_SCROLL_PANE_WIDTH - 50, borderPaneHeight);
            productsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            Pane root = setPageNumberBar();
            borderPane.setBottom(root);
            GridPane gridPane = new GridPane();
            gridPane.getChildren().addAll(getProductsPanes());
            gridPane.setMinWidth(Control.USE_COMPUTED_SIZE); gridPane.setMaxWidth(Control.USE_COMPUTED_SIZE); gridPane.setPrefWidth(Control.USE_COMPUTED_SIZE);
            gridPane.setMinHeight(Control.USE_COMPUTED_SIZE); gridPane.setMaxHeight(Control.USE_COMPUTED_SIZE); gridPane.setPrefHeight(Control.USE_COMPUTED_SIZE);
            borderPane.setCenter(gridPane);
            productsScrollPane.setContent(borderPane);
            productsScrollPane.setVvalue(0);
        } catch (IOException e) { e.printStackTrace(); }

    }

    private Pane setPageNumberBar() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductsPagesBar.fxml"));
        Pane root = loader.load();
        ProductsProcessor pagesBarProcessor = loader.getController();
        pagesBarProcessor.setParentProcessor(this);
        pagesBarProcessor.pageNumberLabel.setText("Page " + (pageNumber + 1) + " of " + (int)Math.ceil(allProducts.size()/12.0));
        if(pageNumber == 0) {
            pagesBarProcessor.previousPageButton.setDisable(true);
            pagesBarProcessor.previousPageButton.setOpacity(0.3);
        }
        if(pageNumber == Math.ceil(allProducts.size()/12.0) - 1) {
            pagesBarProcessor.nextPageButton.setDisable(true);
        }
        return root;
    }

    private ArrayList<HBox> getProductsPanes() throws IOException {
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
        return hBoxes;
    }

    private Pane getProductPane(int productNumberInPage) throws IOException {
        Product product = allProducts.get(pageNumber * pageSize + productNumberInPage);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductPane.fxml"));
        Pane root = loader.load();
        ProductsProcessor productsProcessor = loader.getController();
        productsProcessor.productImage.setImage(productControl.getProductImageByID(product.getID()));
        productsProcessor.productNameLabel.setText(product.getName());
        if(productControl.isThereProductInOff(product.getID())) {
            System.out.println("Product In Off");
        } else {
            root.getChildren().remove(productsProcessor.newPriceLabel);
            productsProcessor.oldPriceLabel.setText(product.getPrice() +"$");
        }
        productsProcessor.viewLabel.setText("" + product.getSeen());
        if(!(product.getStatus() == 1 && (product.getCount() > 0 || product.getAmount() > 0))) {
            productsProcessor.availabelImage.setImage(new Image("Images\\Icons\\ProductsMenu\\unavailable.png"));
            productsProcessor.availableLable.setText((product.getStatus() != 1 ? "Editing" : "Out Of Stock"));
        }
        return root;
    }

    private void getAllProducts() {
        allProducts = productControl.getAllShowingProducts();
    }

    public void setParentProcessor(ProductsProcessor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }

    public void changePage(MouseEvent mouseEvent) {
        ImageView button = (ImageView) mouseEvent.getSource();
        if(button == nextPageButton) {
            parentProcessor.pageNumber += 1;
        } else {
            parentProcessor.pageNumber -= 1;
        }
        parentProcessor.initProductsPage();
    }

    public void changePageOnMouse(MouseEvent mouseEvent) {
        String style = "-fx-opacity: 1; -fx-cursor: hand;";
        ((ImageView)mouseEvent.getSource()).setStyle(style);
    }

    public void changePageOutMouse(MouseEvent mouseEvent) {
        String style = "-fx-opacity: 0.7;";
        ((ImageView)mouseEvent.getSource()).setStyle(style);
    }
}
