package client.view;

import client.api.Command;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import server.controller.account.CustomerControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import server.model.existence.*;
import notification.Notification;
import server.server.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static client.api.Command.HandleType.*;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public class ProductsProcessor extends Processor{
    private static final int PRODUCT_SCROLL_PANE_WIDTH = 1050;
    private static final double PRODUCT_FIELD_WIDTH = 262.5;
    private static final int PRODUCT_FIELD_HEIGHT = 335;
    private static final int PRODUCT_PAGES_BAR_HEIGHT = 50;
    //UserProducts
    public BorderPane vendorProductsMainBorderPane;
    public Pane userProductsOptionPane;
    public JFXButton previousProductButton;
    public CheckBox approveProductCheckBox;
    public ImageView previousPageImageView;
    public JFXToggleButton offProductToggleButton;
    public ImageView inOffImage;
    public JFXButton removeFromCartButton;
    public StackPane productImageStackPane;
    public Pane purchaseButton;
    private HashMap<String, CheckBox> productsApprovalMap;

    public static enum ProductsMenuType {
        MAIN_PRODUCTS, VENDOR_PRODUCTS, CUSTOMER_CART, ADMIN_PRODUCT_REQUESTS,
        VENDOR_ADD_OFF_PRODUCTS, ADMIN_OFF_PRODUCTS, VENDOR_OFF_PRODUCTS,
        VENDOR_OFF_PRODUCTS_UNAPPROVED, PRODUCT_COMPARING_PRODUCTS;

    }
    private ProductsMenuType menuType;

    public ScrollPane productsScrollPane;
    //ProductPane
    public Rectangle productImage;
    public Label productNameLabel;
    public Label viewLabel;
    public ImageView availableImage;
    public Label availableLabel;
    public Label oldPriceLabel;
    public Label newPriceLabel;
    public Label pageNumberLabel;
    public ImageView nextPageButton;
    public ImageView previousPageButton;
    //Product Pane
    public JFXButton viewSortButton;
    public JFXButton timeSortButton;
    public JFXButton nameSortButton;
    public JFXButton scoreSortButton;
    public JFXToggleButton descendingSortButton;
    private JFXButton selectedSort;

    //Categories Pane
    public TreeTableView<Category> categoriesTableTreeView;

    public JFXTextField searchTextField;

    //Filter Pane
    public Pane mainFilterPane;
    public VBox filteredCategoriesVBox;
    public Label filterNameLabel;
    private Category filterCategory;
    private String filterName;

    //Filter Price Pane
    public JFXTextField toPriceTextField, fromPriceTextField;

    private ArrayList<Product> allProducts;
    private int rowSize = 4;
    private int columnMinSize = 2;
    private int columnMaxSize = 3;
    private int pageSize = columnMaxSize * rowSize;
    private int pageNumber = 0;
    private int productFieldsNumber;
    private int pageLim;
    private Off selectedOff;

    //Discount Part
    public JFXToggleButton useDiscountCodeToggleButton;
    public JFXListView<Discount> discountCodesListView;
    private ListCell<Discount> selectedListCell;

    //Total Price Part
    public Label totalPriceLabel;
    public Label discountPriceLabel;
    public ImageView priceArrow;
    public Pane pricePane;

    public void initProcessor (ProductsMenuType menuType) {
        this.menuType = menuType;
        switch (menuType) {
            case MAIN_PRODUCTS:
                initMainProductsMenu();
                break;
            case VENDOR_PRODUCTS:
                initVendorProductsMenu();
                break;
            case CUSTOMER_CART:
                initProductsPage();
                initDiscountPart();
                initTotalPricePart();
                break;
            case ADMIN_PRODUCT_REQUESTS:
                initAdminProductRequestsMenu();
                break;
            case VENDOR_OFF_PRODUCTS_UNAPPROVED:
            case VENDOR_OFF_PRODUCTS:
            case ADMIN_OFF_PRODUCTS:
            case VENDOR_ADD_OFF_PRODUCTS:
                initOffProductsMenu();
                break;
            case PRODUCT_COMPARING_PRODUCTS:
                initProductsPage();
                break;
        }
    }

    private void initOffProductsMenu() {
        rowSize = 3;
        columnMinSize = 2;
        columnMaxSize = 3;
        pageSize = columnMaxSize * rowSize;
        initProductsPage();
    }

    private void initAdminProductRequestsMenu() {
        productsApprovalMap = new HashMap<>();
        initProductsPage();
    }

    private void initVendorProductsMenu() {
        Stop[] stops = new Stop[] {
                new Stop(0, Color.valueOf("#360033")),
                new Stop(1, Color.valueOf("#127183"))
        };
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
        userProductsOptionPane.setBackground(new Background(backgroundFill));
        initProductsPage();
    }

    private void initMainProductsMenu() {
        Command command = new Command("init sort and filter", Command.HandleType.GENERAL);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
        selectedSort = viewSortButton;
        selectSort();
        initCategoriesTableTreeView();
        initPriceFiltersTextFields();
    }

    private void initCategoriesTableTreeView() {
        categoriesTableTreeView.setRowFactory( tv -> {
            TreeTableRow<Category> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 /*&& (!row.isEmpty())*/ ) {
                    Category category = row.getItem();
                    addCategoryToFilters(category);
                }
            });
            row.getStylesheets().add(getClass().getResource("FilterCategoryRowTable.css").toExternalForm());
            row.getStyleClass().add("Row");
            return row ;
        });

        categoriesTableTreeView.setRoot(getCategoryTableRoot());
        categoriesTableTreeView.setShowRoot(false);
        //categoriesTableTreeView.getSelectionModel().selectFirst();
    }

    private void initPriceFiltersTextFields() {
        setPriceFields(toPriceTextField);
        setPriceFields(fromPriceTextField);
    }

    private void setPriceFields(JFXTextField priceTextField) {
        priceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                if(newValue.equals(".")) {
                    priceTextField.setText("0.");
                } else if (!newValue.matches("\\d+(.(\\d)+)?")) {
                    if(priceTextField.getText().contains(".")) {
                        priceTextField.setText(removeDots(priceTextField.getText()));
                    } else {
                        priceTextField.setText(newValue.replaceAll("[^\\d\\.]", ""));
                    }
                }

                setPriceFilter(/*priceTextField*/);
            }
        });
    }

    private void setPriceFilter(/*JFXTextField priceTextField*/) {
        double minPrice = fromPriceTextField.getText().isEmpty() ? 0 : Double.parseDouble(fromPriceTextField.getText());
        double maxPrice = toPriceTextField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(toPriceTextField.getText());
        Command<Double> command = new Command<>("set price filters", Command.HandleType.GENERAL, minPrice, maxPrice);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
        initProductsPage();
    }

    private void addCategoryToFilters(Category category) {
        if(!isThereFilteringCategoryWithName(category.getName())) {
            try {
                loadFilterPane(category, null);
            } catch (IOException e) {
                //:)
            }
        }
    }

    private boolean isThereFilteringCategoryWithName(String categoryName) {
        Command<String> command = new Command<>("is category in filter", Command.HandleType.GENERAL, categoryName);
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        return response.getDatum();
    }

    private void loadFilterPane(Category filterCategory, String filterName) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FilterCategoryPane.fxml"));
            Node node = fxmlLoader.load();
            ProductsProcessor productsProcessor = fxmlLoader.getController();
            productsProcessor.parentProcessor = this;

            if (filterCategory == null) {
                productsProcessor.filterName = filterName;
                productsProcessor.filterNameLabel.setText(" " + filterName + " ");
                addToFilterNameList(filterName);
                searchTextField.setText(null);
            } else if (filterName == null) {
                productsProcessor.filterCategory = filterCategory;
                productsProcessor.filterNameLabel.setText(" " + filterCategory.getName() + " ");
                addToFilterCategoryList(filterCategory.getName());
            }

            initProductsPage();
            filteredCategoriesVBox.getChildren().add(node);
    }

    private void addToFilterNameList(String filterName) {
        Command<String> command = new Command<>("add name to filter", Command.HandleType.GENERAL, filterName);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private void addToFilterCategoryList(String categoryName) {
        Command<String> command = new Command<>("add category to filter", Command.HandleType.GENERAL, categoryName);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    public void deleteFilterCategoryMouseClicked(MouseEvent mouseEvent) {
        if(filterCategory == null)
            removeFromFilterNameList(filterName);
        else if(filterName == null)
            removeFromFilterCategoryList(filterCategory.getName());

        ((ProductsProcessor)parentProcessor).initProductsPage();
        ((ProductsProcessor)parentProcessor).filteredCategoriesVBox.getChildren().remove(mainFilterPane);
    }

    private void removeFromFilterNameList(String filterName) {
        Command<String> command = new Command<>("remove name from filter", Command.HandleType.GENERAL, filterName);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private void removeFromFilterCategoryList(String categoryName) {
        Command<String> command = new Command<>("remove category from filter", Command.HandleType.GENERAL, categoryName);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    public void showCartProducts(ActionEvent actionEvent) {
        String type = getType();
        if(type != null && !type.equals("Customer")) {
            new Alert(Alert.AlertType.ERROR, "Sorry. Only Customers Can Use This Option Of The Market!").show();
            return;
        }
        if(canOpenSubStage(((isLoggedIn() ? getUsername() : "Your")+ " Cart"), this))
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerCartProducts.fxml"));
                Parent root = loader.load();
                ProductsProcessor processor = loader.getController();
                type = getType();
                if(!(type != null && type.equals("Customer")))
                    processor.userProductsOptionPane.getChildren().
                        removeAll(processor.discountCodesListView, processor.useDiscountCodeToggleButton);
                processor.parentProcessor = this;
                processor.initProcessor(ProductsProcessor.ProductsMenuType.CUSTOMER_CART);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(IMAGE_FOLDER_URL + "Icons\\cart (2).png"));

                stage.setTitle((isLoggedIn() ? getUsername() : "Your")+ " Cart");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                this.subStages.add(stage);
                processor.setMyStage(stage);
                stage.getIcons().add(new Image(Main.class.getResourceAsStream("cartCustomer.png")));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    //initProductsScrollPane
    public void initProductsPage() {
        switch (menuType) {
            case MAIN_PRODUCTS:
                allProducts = new ArrayList<>(client.postAndGet(new Command("get all showing products", Command.HandleType.PRODUCT), Response.class, (Class<Product>)Product.class).getData());
                break;
            case VENDOR_PRODUCTS:
                allProducts = new ArrayList<>(client.postAndGet(new Command("get vendor products", Command.HandleType.PRODUCT), Response.class, (Class<Product>)Product.class).getData());
                break;
            case CUSTOMER_CART:
                String type = getType();
                if(type != null && type.equals("Customer") && isLoggedIn())
                    allProducts = new ArrayList<>(client.postAndGet(new Command("get customer cart products", PRODUCT), Response.class, (Class<Product>)Product.class).getData());
                else
                    allProducts = new ArrayList<>(client.postAndGet(new Command("get temp cart products", PRODUCT), Response.class, (Class<Product>)Product.class).getData());
                purchaseButton.setDisable(allProducts.size() == 0);
                break;
            case ADMIN_PRODUCT_REQUESTS:
                allProducts = new ArrayList<>(client.postAndGet(new Command("get not approved products", Command.HandleType.PRODUCT), Response.class, (Class<Product>)Product.class).getData());
                break;
            case VENDOR_ADD_OFF_PRODUCTS:
                allProducts = new ArrayList<>(client.postAndGet(new Command<String>("get non off products", SALE), Response.class, (Class<Product>)Product.class).getData());
                break;
            case VENDOR_OFF_PRODUCTS_UNAPPROVED:
            case ADMIN_OFF_PRODUCTS:
                if(!isThereOff(selectedOff))
                    return;
                allProducts = new ArrayList<>(client.postAndGet(new Command<String>("get off products", SALE, selectedOff.getOffID(), "" + (selectedOff.getStatus() == 3)), Response.class, (Class<Product>)Product.class).getData());
                break;
            case VENDOR_OFF_PRODUCTS:
                if(!isThereOff(selectedOff))
                    return;
                allProducts = new ArrayList<>(client.postAndGet(new Command<String>("get non off products with exceptions", SALE, selectedOff.getOffID()), Response.class, (Class<Product>)Product.class).getData());
                break;
            case PRODUCT_COMPARING_PRODUCTS:
                allProducts = new ArrayList<>(getAllComparingProducts());
                break;

        }
        initCertainProductsPage(productsScrollPane);
    }

    private boolean isThereOff(Off selectedOff) {
        Command<String> command = new Command<>("is there off", SALE, selectedOff.getOffID());
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        if(response.getDatum()) {
            selectedOff = getOffByID(selectedOff.getOffID(), "off");
            return true;
        }
        else {
            ((TableViewProcessor)((SaleProcessor)parentProcessor).parentProcessor).updateTable();
            ((TableViewProcessor)((SaleProcessor)parentProcessor).parentProcessor).updateSelectedItem();
            ((SaleProcessor)parentProcessor).myStage.close();
            return false;
        }
    }

    private void initCertainProductsPage(ScrollPane scrollPane) {
        try {
            BorderPane borderPane = new BorderPane();
            pageLim = (allProducts.size() - (pageNumber * pageSize) < pageSize ? (allProducts.size() -(pageNumber * pageSize)) : pageSize);
            productFieldsNumber = (pageLim <= (columnMinSize * rowSize) ? (columnMinSize * rowSize) : (columnMaxSize * rowSize));
            double borderPaneHeight = ((productFieldsNumber/rowSize) * PRODUCT_FIELD_HEIGHT) + PRODUCT_PAGES_BAR_HEIGHT;
            double borderPaneWidth = PRODUCT_FIELD_WIDTH * rowSize;
            borderPane.setPrefSize(borderPaneWidth - 50, borderPaneHeight);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            Pane root = setPageNumberBar();
            borderPane.setBottom(root);
            GridPane gridPane = new GridPane();
            gridPane.getChildren().addAll(getProductsPanes());
            gridPane.setMinWidth(Control.USE_COMPUTED_SIZE); gridPane.setMaxWidth(Control.USE_COMPUTED_SIZE); gridPane.setPrefWidth(Control.USE_COMPUTED_SIZE);
            gridPane.setMinHeight(Control.USE_COMPUTED_SIZE); gridPane.setMaxHeight(Control.USE_COMPUTED_SIZE); gridPane.setPrefHeight(Control.USE_COMPUTED_SIZE);
            borderPane.setCenter(gridPane);
            scrollPane.setContent(borderPane);
            scrollPane.setVvalue(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pane setPageNumberBar() throws IOException {
        FXMLLoader loader;
        if(rowSize == 4) {
            loader = new FXMLLoader(Main.class.getResource("ProductsPagesBar.fxml"));
        }  else {
            loader = new FXMLLoader(Main.class.getResource("ProductsPagesBarEnhancedEdition.fxml"));
        }
        Pane root = loader.load();
        ProductsProcessor pagesBarProcessor = loader.getController();
        pagesBarProcessor.setParentProcessor(this);
        if((int)Math.ceil(((double)allProducts.size())/pageSize) != 0)
            pagesBarProcessor.pageNumberLabel.setText("Page " + (pageNumber + 1) + " of " + (int)Math.ceil(((double)allProducts.size())/pageSize));
        else {
            pagesBarProcessor.pageNumberLabel.setText("Page " + (pageNumber + 1) + " of " + 1);
            pagesBarProcessor.nextPageButton.setDisable(true);
            pagesBarProcessor.nextPageButton.setOpacity(0.3);
            pagesBarProcessor.previousPageButton.setDisable(true);
            pagesBarProcessor.previousPageButton.setOpacity(0.3);
        }
        if(pageNumber == 0) {
            pagesBarProcessor.previousPageButton.setDisable(true);
            pagesBarProcessor.previousPageButton.setOpacity(0.3);
        }
        if(pageNumber == (((int)Math.ceil(((double)allProducts.size())/pageSize) - 1))) {
            pagesBarProcessor.nextPageButton.setDisable(true);
            pagesBarProcessor.nextPageButton.setOpacity(0.3);
        }
        return root;
    }

    private ArrayList<HBox> getProductsPanes() throws IOException {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        for(int y = 0; y < productFieldsNumber/rowSize; ++y) {
            for(int x = 0; x < rowSize; ++ x) {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                GridPane.setConstraints(hBox, x, y);
                hBox.setMinWidth(255); hBox.setMaxWidth(255); hBox.setPrefWidth(255);
                hBox.setMinHeight(PRODUCT_FIELD_HEIGHT); hBox.setMaxHeight(PRODUCT_FIELD_HEIGHT); hBox.setPrefHeight(PRODUCT_FIELD_HEIGHT);
                hBoxes.add(hBox);
            }
        }
        if(menuType == ProductsMenuType.CUSTOMER_CART) {
            for(int i = 0; i < pageLim; ++i) {
                hBoxes.get(i).getChildren().add(getCustomerCartProductPane(i));
            }
        } else if(menuType == ProductsMenuType.ADMIN_PRODUCT_REQUESTS) {
            for(int i = 0; i < pageLim; ++i) {
                hBoxes.get(i).getChildren().add(getAdminProductRequestsProductPane(i));
            }

        } else if(menuType == ProductsMenuType.VENDOR_ADD_OFF_PRODUCTS || menuType == ProductsMenuType.VENDOR_OFF_PRODUCTS) {
            for(int i = 0; i < pageLim; ++i) {
                hBoxes.get(i).getChildren().add(getVendorOffProductPane(i));
            }
        } else {
            for(int i = 0; i < pageLim; ++i) {
                hBoxes.get(i).getChildren().add(getCommonProductPane(i));
            }
        }

        return hBoxes;
    }

    private Pane getCustomerCartProductPane(int productNumberInPage) throws IOException {
        Product product = allProducts.get(pageNumber * pageSize + productNumberInPage);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductPaneCustomerCart.fxml"));
        Pane productPane = loader.load();
        ProductsProcessor paneProcessor = loader.getController();
        paneProcessor.setParentProcessor(this);
        paneProcessor.removeFromCartButton.setOnAction(event -> {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do You Really Want To Delete This Great Product From You Cart?", YES, NO).showAndWait();
            if(buttonType.get() == YES) {
                removeProductFromCartByID(product.getID()).getAlert().show();
                initProductsPage();
                initTotalPricePart();
            }
        });
        setProductStatusIcon(paneProcessor, product);
        setProductPaneImage(productPane, paneProcessor, product);
        paneProcessor.productNameLabel.setText(product.getName());
        setProductPanePrice(productPane, paneProcessor, product);
        setProductPaneOnMouseClick(productPane, product, this);
        return productPane;
    }

    private Notification removeProductFromCartByID(String productID) {
        Command<String> command = new Command<>("remove product from cart", PRODUCT, productID);
        Response response = client.postAndGet(command, Response.class, (Class<Object>)Object.class);
        return response.getMessage();
    }

    private Pane getVendorOffProductPane(int productNumberInPage) throws IOException {
        Product product = allProducts.get(pageNumber * pageSize + productNumberInPage);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductPaneOff.fxml"));
        Pane productPane = loader.load();
        ProductsProcessor paneProcessor = loader.getController();
        paneProcessor.setParentProcessor(this);
        paneProcessor.offProductToggleButton.setOnAction(event -> {
            if(((JFXToggleButton)event.getSource()).isSelected()) {
                ((SaleProcessor)parentProcessor).addProductToOff(product.getID());
            } else {
                ((SaleProcessor)parentProcessor).deleteProductFromOff(product.getID());
            }
        });
        paneProcessor.offProductToggleButton.setSelected(((SaleProcessor)parentProcessor).isProductInOff(product.getID()));
        setProductStatusIcon(paneProcessor, product);
        setProductPaneImage(productPane, paneProcessor, product);
        paneProcessor.productNameLabel.setText(product.getName());
        setProductPanePrice(productPane, paneProcessor, product);
        setProductPaneOnMouseClick(productPane, product, this);
        return productPane;
    }

    private Pane getAdminProductRequestsProductPane(int productNumberInPage) throws IOException {
        Product product = allProducts.get(pageNumber * pageSize + productNumberInPage);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductPaneAdmin.fxml"));
        Pane productPane = loader.load();
        ProductsProcessor paneProcessor = loader.getController();
        paneProcessor.setParentProcessor(this);
        if(product.getStatus() == 3) {
            paneProcessor.previousProductButton.setOnAction(event -> {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ProductMenu.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    ProductProcessor processor = fxmlLoader.getController();
                    processor.setParentProcessor(paneProcessor.parentProcessor);
                    processor.setNonEdited(true);
                    processor.initProcessor(getProductByID(product.getID(), "product"), ProductProcessor.ProductMenuType.ADMIN);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(product.getName() + " Previous Menu");
                    processor.setMyStage(stage);
                    parentProcessor.addSubStage(stage);
                    stage.setResizable(false);
                    stage.getIcons().add(new Image(Main.class.getResourceAsStream("Product Icon.png")));
                    stage.show();
                } catch (IOException e) {
                    //:)
                }
            });
        } else {
            productPane.getChildren().remove(paneProcessor.previousProductButton);
            productPane.getChildren().remove(paneProcessor.previousPageImageView);
        }
        paneProcessor.approveProductCheckBox.setOnMouseClicked(event -> {
            if(productsApprovalMap.containsKey(product.getID())) {
                if(!paneProcessor.approveProductCheckBox.isSelected())
                    productsApprovalMap.remove(product.getID());
                else
                    productsApprovalMap.replace(product.getID(), paneProcessor.approveProductCheckBox);
            } else
                productsApprovalMap.put(product.getID(), paneProcessor.approveProductCheckBox);
        });
        paneProcessor.availableImage.setImage(new Image(IMAGE_FOLDER_URL + "Icons\\ProductsMenu\\unavailable.png"));
        paneProcessor.availableLabel.setText(product.getTheStatus());
        setProductPaneImage(productPane, paneProcessor, product);
        setProductPanePrice(productPane, paneProcessor, product);
        paneProcessor.productNameLabel.setText(product.getName());
        setProductPaneOnMouseClick(productPane, product, this);
        return productPane;
    }

    private Pane getCommonProductPane(int productNumberInPage) throws IOException {
        Product product = allProducts.get(pageNumber * pageSize + productNumberInPage);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductPane.fxml"));
        Pane productPane = loader.load();
        ProductsProcessor paneProcessor = loader.getController();
        setProductPaneImage(productPane, paneProcessor, product);
        paneProcessor.productNameLabel.setText(product.getName());
        setProductPanePrice(productPane, paneProcessor, product);
        if(product.getSeen() < 100)
            paneProcessor.viewLabel.setText("" + product.getSeen());
        else
            paneProcessor.viewLabel.setText("99+");
        setProductStatusIcon(paneProcessor, product);
        setProductPaneOnMouseClick(productPane, product, this);
        return productPane;
    }

    private void setProductStatusIcon(ProductsProcessor paneProcessor, Product product) {
        if (!(product.getStatus() == 1 && (product.getCount() > 0 || product.getAmount() > 0))) {
            paneProcessor.availableImage.setImage(new Image(IMAGE_FOLDER_URL + "Icons\\ProductsMenu\\unavailable.png"));
            if(product.getStatus() != 1)
                paneProcessor.availableLabel.setText(product.getTheStatus());
            else
                paneProcessor.availableLabel.setText("Out Of Stock");
        }
    }

    private void setProductPaneImage(Pane productPane, ProductsProcessor paneProcessor, Product product) {
        Command<String> productImageCommand = new Command<>("get product image-1", Command.HandleType.FILE_GET, product.getID());
        Command<String> editingProductImageCommand = new Command<>("get edit product image-1", Command.HandleType.FILE_GET, product.getID());
        if(doesProductHaveFile(product.getID())) {
            ImageView imageView = new ImageView(new Image(IMAGE_FOLDER_URL + "Icons\\ProductsMenu\\File2.png"));
            imageView.setFitWidth(paneProcessor.productImage.getWidth()/3);
            imageView.setFitHeight(paneProcessor.productImage.getHeight()/3);
            imageView.setLayoutX(paneProcessor.productImageStackPane.getLayoutX() + 5); imageView.setLayoutY(paneProcessor.productImageStackPane.getLayoutY());
            productPane.getChildren().add(imageView);
        }
        switch (menuType) {
            case VENDOR_PRODUCTS:
            case ADMIN_PRODUCT_REQUESTS:
            case VENDOR_ADD_OFF_PRODUCTS:
            case VENDOR_OFF_PRODUCTS:
            case VENDOR_OFF_PRODUCTS_UNAPPROVED:
                if(product.getStatus() != 3) {
                    paneProcessor.productImage.setFill(new ImagePattern(client.getImage(productImageCommand)));
                } else {
                    paneProcessor.productImage.setFill(new ImagePattern(client.getImage(editingProductImageCommand)));
                }
                break;
            default:
                paneProcessor.productImage.setFill(new ImagePattern(client.getImage(productImageCommand)));
                if(!(product.getCount() > 0 || product.getAmount() > 0)) {
                    ImageView imageView = new ImageView(new Image(IMAGE_FOLDER_URL + "Icons\\ProductsMenu\\sold out.png"));
                    imageView.setFitWidth(paneProcessor.productImage.getWidth());
                    imageView.setFitHeight(paneProcessor.productImage.getHeight());
                    imageView.setLayoutX(paneProcessor.productImageStackPane.getLayoutX()); imageView.setLayoutY(paneProcessor.productImageStackPane.getLayoutY());
                    productPane.getChildren().add(imageView);
                }
                break;
        }
    }

    private void setProductPanePrice(Pane productPane, ProductsProcessor paneProcessor, Product product) {
        if (product.isOnSale()) {
            paneProcessor.oldPriceLabel.setText(getSmoothDoubleFormat(product.getPrice()) + "$");
            paneProcessor.oldPriceLabel.getStylesheets().addAll(Main.class.getResource(
                    "Strikethrough.css"
            ).toExternalForm());
            //paneProcessor.oldPriceLabel.setStyle("-fx-strikethrough: true;");
            paneProcessor.newPriceLabel.setText
                    (getSmoothDoubleFormat(product.getOffPrice()) + "$");
        } else {
            productPane.getChildren().removeAll(paneProcessor.newPriceLabel, paneProcessor.inOffImage);
            paneProcessor.oldPriceLabel.setText(getSmoothDoubleFormat(product.getPrice()) + "$");
        }
    }

    private void setProductPaneOnMouseClick(Pane productPane, Product product, ProductsProcessor parentProcessor) {
        productPane.setOnMouseClicked(event -> {
            ProductProcessor.ProductMenuType productMenuType = null;
            switch (menuType) {
                case VENDOR_OFF_PRODUCTS:
                case VENDOR_ADD_OFF_PRODUCTS:
                case VENDOR_PRODUCTS:
                    if(product.getStatus() != 2)
                        productMenuType = ProductProcessor.ProductMenuType.VENDOR_EDIT;
                    else
                        productMenuType = ProductProcessor.ProductMenuType.VENDOR_EDIT_UNAPPROVED;
                    break;
                case ADMIN_OFF_PRODUCTS:
                case MAIN_PRODUCTS:
                    String type = getType();
                    if(type != null && type.equals("Admin")){
                        productMenuType = ProductProcessor.ProductMenuType.ADMIN;
                    } else if(type != null && type.equals("Customer")) {
                        productMenuType = ProductProcessor.ProductMenuType.PRODUCTS_CUSTOMER;
                    } else if(type != null && type.equals("Vendor"))
                        productMenuType = ProductProcessor.ProductMenuType.PRODUCTS_VENDOR;
                    else
                        productMenuType = ProductProcessor.ProductMenuType.PRODUCTS;
                    break;
                case CUSTOMER_CART:
                    productMenuType = ProductProcessor.ProductMenuType.CART;
                    break;
                case ADMIN_PRODUCT_REQUESTS:
                    productMenuType = ProductProcessor.ProductMenuType.ADMIN;
                    break;
                case VENDOR_OFF_PRODUCTS_UNAPPROVED:
                    return;
                case PRODUCT_COMPARING_PRODUCTS:
                    startComparing(product, parentProcessor);
                    return;
                //TODO(MORE)
            }
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenu.fxml"));
            try {
                if(menuType == ProductsMenuType.MAIN_PRODUCTS) {
                    addSeenToProduct(product.getID());
                }
                Parent root = loader.load();
                ProductProcessor processor = loader.getController();
                processor.setParentProcessor(parentProcessor);
                processor.initProcessor(product, productMenuType);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(product.getName() + " Menu");
                processor.setMyStage(stage);
                if(parentProcessor.parentProcessor != null)
                    parentProcessor.parentProcessor.addSubStage(stage);
                stage.setResizable(false);
                stage.getIcons().add(new Image(Main.class.getResourceAsStream("Product Icon.png")));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void startComparing(Product product, ProductsProcessor parentProcessor) {
        try {
            setComparingProduct(product.getID(), 2);
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ComparingProductsMenu.fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            Product firstComparingProduct = getComparingProduct(1);
            Product secondComparingProduct = getComparingProduct(2);
            processor.initComparingProcessor(firstComparingProduct, secondComparingProduct);
            myStage.setTitle("Compare " + firstComparingProduct.getName() + " And " + secondComparingProduct.getName());
            myStage.setScene(new Scene(root));
            processor.setMyStage(myStage);
            processor.setParentProcessor(parentProcessor.parentProcessor);
        } catch (IOException e) {
            //:)
        }
    }

    private Product getComparingProduct(int number) {
        Command<Integer> command = new Command<>("get comparing product", PRODUCT, number);
        Response<Product> response = client.postAndGet(command, Response.class, (Class<Product>) Product.class);
        return response.getDatum();
    }

    public void changePage(MouseEvent mouseEvent) {
        ImageView button = (ImageView) mouseEvent.getSource();
        if(button == nextPageButton) {
            ((ProductsProcessor)parentProcessor).pageNumber += 1;
        } else {
            ((ProductsProcessor)parentProcessor).pageNumber -= 1;
        }
        ((ProductsProcessor)parentProcessor).initProductsPage();
    }

    public void changePageOnMouse(MouseEvent mouseEvent) {
        String style = "-fx-opacity: 1; -fx-cursor: hand;";
        ((ImageView)mouseEvent.getSource()).setStyle(style);
    }

    public void changePageOutMouse(MouseEvent mouseEvent) {
        String style = "-fx-opacity: 0.7;";
        ((ImageView)mouseEvent.getSource()).setStyle(style);
    }

    public void sortButtonOnMouse(MouseEvent mouseEvent) {
        if ((JFXButton) mouseEvent.getSource() != selectedSort) {
            String style ="-fx-background-radius: 10 10 10 10; -fx-background-color: #90a4ae; -fx-cursor: hand;";
            ((JFXButton)mouseEvent.getSource()).setStyle(style);
        }
    }

    public void sortButtonOutMouse(MouseEvent mouseEvent) {
        if ((JFXButton) mouseEvent.getSource() != selectedSort) {
            String style ="-fx-background-radius: 10 10 10 10;";
            ((JFXButton)mouseEvent.getSource()).setStyle(style);
        }
    }

    public void changeSort(ActionEvent actionEvent) {
        String style ="-fx-background-radius: 10 10 10 10;";
        selectedSort.setStyle(style);
        selectedSort = (JFXButton) actionEvent.getSource();
        selectSort();
    }

    private void selectSort() {
        String style ="-fx-background-radius: 10 10 10 10; -fx-background-color: #607d8b;";
        selectedSort.setStyle(style);
        setSort();
    }

    public void setSort() {
        Command<String> command = new Command<>("set sort", Command.HandleType.GENERAL, selectedSort.getText(), "" + !descendingSortButton.isSelected());
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
        initProductsPage();
    }

    public void openAccountMenu(ActionEvent actionEvent) {
        new WelcomeProcessor().openAccountMenu();
    }

    public void filterByNameKeyTyped(KeyEvent keyEvent) {
        if((int) keyEvent.getCharacter().charAt(0) == 13) {
            filterByNameMouseClicked();
        }
    }

    public void filterByNameMouseClicked() {
        String searchFieldText = searchTextField.getText();

        if(searchFieldText == null || searchFieldText.isEmpty()) {
            searchTextField.requestFocus();
        } else if(!isThereFilteringNameWithName(searchFieldText)) {
            try {
                loadFilterPane(null, searchTextField.getText());
            } catch (IOException e) {
                //:)
            }
        }

    }

    private boolean isThereFilteringNameWithName(String filterName) {
        Command<String> command = new Command<>("is name in filter", Command.HandleType.GENERAL, filterName);
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        return response.getDatum();
    }

    public void addNewProduct(MouseEvent mouseEvent) {
        if(canOpenSubStage("Add New Product", this)) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenu.fxml"));
            try {
                Parent root = loader.load();
                ProductProcessor processor = loader.getController();
                processor.initProcessor(new Product(), ProductProcessor.ProductMenuType.VENDOR_ADD);
                processor.setParentProcessor(this);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Add New Product");
                addSubStage(stage);
                processor.setMyStage(stage);
                stage.setResizable(false);
                stage.getIcons().add(new Image(Main.class.getResourceAsStream("Product Icon.png")));
                stage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

    public void addNewOff(MouseEvent mouseEvent) {
        if(canOpenSubStage("Add New Off", parentProcessor)) {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffMenu.fxml"));
                Parent root = loader.load();
                SaleProcessor processor = loader.getController();
                processor.setParentProcessor(this.parentProcessor);
                processor.offInfoPaneMouseClick(null);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Add New Off");
                newStage.setResizable(false);
                parentProcessor.addSubStage(newStage);
                processor.setMyStage(newStage);
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("Offs Icon.png")));
                newStage.show();
            } catch (IOException e) {
                //:);
            }
        }
    }

    public void saveChangesForAdminProductRequests(MouseEvent mouseEvent) {
        ArrayList<Notification> results = new ArrayList<>();
        for (String productID : productsApprovalMap.keySet()) {
            Command<String> statusCommand = new Command<>("get product status", Command.HandleType.PRODUCT, productID);
            Response<Integer> statusResponse = client.postAndGet(statusCommand, Response.class, (Class<Integer>)Integer.class);
            if(statusResponse.getDatum().intValue() == 3) {
                Command<String> command = new Command<>("modify editing product approve", Command.HandleType.PRODUCT, productID, "" + productsApprovalMap.get(productID).isSelected());
                results.add(client.postAndGet(command, Response.class, (Class<Object>)Object.class).getMessage());
            } else {
                Command<String> command = new Command<>("modify product approve", Command.HandleType.PRODUCT, productID, "" + productsApprovalMap.get(productID).isSelected());
                results.add(client.postAndGet(command, Response.class, (Class<Object>)Object.class).getMessage());
            }
        }
        showManageProductRequestsResult(results);
        initProductsPage();
    }

    private void showManageProductRequestsResult(ArrayList<Notification> results) {
        int errorCount = 0;
        int addProductAcceptCount = 0;
        int editProductAcceptCount = 0;
        int addProductDeclineCount = 0;
        int editProductDeclineCount = 0;
        for (Notification result : results) {
            switch (result) {
                case UNKNOWN_ERROR:
                    errorCount++;
                    break;
                case ACCEPT_EDITING_PRODUCT:
                    editProductAcceptCount++;
                    break;
                case ACCEPT_ADDING_PRODUCT:
                    addProductAcceptCount++;
                    break;
                case DECLINE_EDITING_PRODUCT:
                    editProductDeclineCount++;
                    break;
                case REMOVE_PRODUCT_SUCCESSFULLY:
                    addProductDeclineCount++;
                    break;
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Process Executed. Results:\n" +
                "Errors: " + errorCount + "\n" +
                "Accepted New Products: " + addProductAcceptCount +"\n" +
                "Accepted Edited Products: " + editProductAcceptCount +"\n" +
                "Declined New Products: " + addProductDeclineCount +"\n" +
                "Declined Edited Products: " + editProductDeclineCount +"\n" +
                "").show();
    }

    public void backToMainMenu(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource("WelcomeMenu.fxml"));
        } catch (IOException e) {
            //:)
        }
        Main.getStage().getIcons().remove(0);
        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Main Icon.png")));
        Main.setScene("Boos Market", root);
    }

    public void setSelectedOff(Off selectedOff) {
        this.selectedOff = selectedOff;
    }

    //Purchase Part
    public void purchaseProducts(MouseEvent mouseEvent) {
        try {
            String type = getType();
            if(isLoggedIn() && type != null && type.equals("Customer"))
                purchase();
            else
                openSignInMenu();
        } catch (IOException e) {
            //:)
        }

    }

    private void openSignInMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignInMenu.fxml"));
        Parent root = loader.load();
        WelcomeProcessor signInProcessor = loader.getController();
        signInProcessor.setParentProcessor(this);
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        signInProcessor.setMyStage(newStage);
        newStage.getIcons().add(new Image(getClass().getResourceAsStream("Login Icon.png")));
        newStage.setResizable(false);
        newStage.setTitle("Sign In");
        newStage.show();
    }

    private void purchase() throws IOException {
        CustomerControl customerControl = CustomerControl.getController();

        setHasDiscount(selectedListCell != null);
        setDiscount((selectedListCell == null ? null : selectedListCell.getItem().getID()));
        if(canPurchase()) {
            if (getCartSize() == 0) {
                new Alert(Alert.AlertType.ERROR, "Your Cart Is Empty. At Some Products First Dude :/").show();
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Purchase.fxml"));
            Parent root = fxmlLoader.load();
            CustomerProfileProcessor customerProfileProcessor = fxmlLoader.getController();
            customerProfileProcessor.parentProcessor = parentProcessor;
            customerProfileProcessor.setMyStage(myStage);
            myStage.setScene(new Scene(root));
            myStage.setTitle("Purchase Menu");
        } else {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ProfileMenu.fxml"));
            Parent root = fxmlLoader.load();
            ProfileProcessor profileProcessor = fxmlLoader.getController();
            profileProcessor.init(getAccountByUsername(getUsername()), "ProfileMenu");
            profileProcessor.changeButtonBackGroundColor(profileProcessor.profileCreditButton);
            profileProcessor.changeCenterPane("profileCreditMenu");
            stage.setScene(new Scene(root));
            stage.setTitle("add money for purchase");
            addSubStage(stage);
            stage.show();
        }
    }

    private boolean canPurchase() {
        Command command = new Command("can purchase", ACCOUNT);
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        return response.getDatum();
    }

    private void setDiscount(String discountID) {
        Command<String> command = new Command<>("set discount for purchase", GENERAL, discountID);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private void setHasDiscount(boolean hasDiscount) {
        Command<Boolean> command = new Command<>("set has discount", GENERAL, hasDiscount);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private int getCartSize() {
        Response<Integer> response = client.postAndGet(new Command("get cart size", PRODUCT), Response.class, (Class<Integer>)Integer.class);
        return response.getDatum().intValue();
    }

    //Discount Part
    private void initDiscountPart() {
        setDiscountsListViewSpecifications();
        useDiscountCodeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> discountToggleButtonChanged());
    }

    //Todo Check
    private void setDiscountsListViewSpecifications() {
        discountCodesListView.setCellFactory(param -> {
            ListCell<Discount> listCell = new ListCell<Discount>() {
                @Override
                protected void updateItem(Discount discount, boolean empty) {
                    super.updateItem(discount, empty);

                    if(!empty)
                        setText(discount.getCode() + " : " + discount.getDiscountPercent() + "%");
                }
            };
            listCell.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY) && listCell.getItem() != null) {
                    if(selectedListCell != null)
                        selectedListCell.setStyle("");
                    listCell.setStyle("-fx-background-color: #10A0DC;");
                    selectedListCell = listCell;
                    showDiscountEffect(listCell.getItem());
                }
            });
            listCell.getStyleClass().add("Discounts_ListView_Cell");
            return listCell;
        });
    }

    private void discountToggleButtonChanged() {
        if(useDiscountCodeToggleButton.isSelected()) {
            setDiscountListViewCells();
        } else {
            removeDiscountListViewCells();
            removeDiscountEffect();
        }
    }

    private void setDiscountListViewCells() {
        //Todo Check
        discountCodesListView.setDisable(false);
        ObservableList<Discount> customerDiscounts = discountCodesListView.getItems();
        customerDiscounts.addAll(getAllAvailableCustomerDisCounts());
    }

    private ArrayList<Discount> getAllAvailableCustomerDisCounts() {
        Command command = new Command("get all customer available discounts", SALE);
        Response<Discount> response = client.postAndGet(command, Response.class, (Class<Discount>)Discount.class);
        return new ArrayList<>(response.getData());
    }

    private void removeDiscountListViewCells() {
        discountCodesListView.getItems().removeAll(discountCodesListView.getItems());
        discountCodesListView.setDisable(true);

        if(selectedListCell != null) {
            selectedListCell.setStyle("");
            selectedListCell = null;
        }
    }

    private void showDiscountEffect(Discount discount) {
        if(totalPriceLabel.getPrefWidth() == 157) {
            setPriceWithDiscountLabel(calculatePriceWithDiscount(discount));
            setDiscountPriceArrow();
        } else {
            discountPriceLabel.setText(calculatePriceWithDiscount(discount) + " $");
        }
    }

    private void removeDiscountEffect() {
        if(totalPriceLabel.getPrefWidth() != 157) {
            pricePane.getChildren().removeAll(discountPriceLabel, priceArrow);
            totalPriceLabel.setPrefWidth(157.0);
        }
    }

    private void setDiscountPriceArrow() {
        try {
            FileInputStream imageFileInputStream = new FileInputStream("src\\main\\resources\\" + IMAGE_FOLDER_URL + "Icons\\ProductsMenu\\Arrow Cart.png");
            ImageView priceArrow = new ImageView(new Image(imageFileInputStream));
            priceArrow.setFitWidth(20.0);
            priceArrow.setFitHeight(20.0);

            priceArrow.setLayoutX(totalPriceLabel.getLayoutX() + totalPriceLabel.getPrefWidth() + 5.0);
            priceArrow.setLayoutY(totalPriceLabel.getLayoutY());

            pricePane.getChildren().add(priceArrow);
            this.priceArrow = priceArrow;
            imageFileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPriceWithDiscountLabel(double discountPrice) {
        Label discountPriceLabel = new Label(getSmoothDoubleFormat(discountPrice) + " $");

        discountPriceLabel.setPrefHeight(totalPriceLabel.getPrefHeight());
        discountPriceLabel.setPrefWidth((totalPriceLabel.getPrefWidth() - 30.0) / 2);
        totalPriceLabel.setPrefWidth(discountPriceLabel.getPrefWidth());

        discountPriceLabel.setLayoutY(totalPriceLabel.getLayoutY());
        discountPriceLabel.setLayoutX(totalPriceLabel.getLayoutX() + totalPriceLabel.getPrefWidth() + 30.0);
        pricePane.getChildren().add(discountPriceLabel);
        this.discountPriceLabel = discountPriceLabel;
    }

    private double calculatePriceWithDiscount(Discount discount) {
        double price1 = Double.parseDouble(totalPriceLabel.getText().replace(" $", ""));
        return price1 - Math.min(price1 * discount.getDiscountPercent() / 100, discount.getMaxDiscount());
    }

    //Price Part
    protected void initTotalPricePart() {
        Command command = new Command("get total price without discount", PRODUCT);
        Response<Double> response = client.postAndGet(command, Response.class, (Class<Double>)Double.class);
//        double totalPrice = CustomerControl.getController().getTotalPriceWithoutDiscount();
        totalPriceLabel.setText(getSmoothDoubleFormat(response.getDatum()));
        if(selectedListCell != null) {
            showDiscountEffect(selectedListCell.getItem());
        }
    }

}
