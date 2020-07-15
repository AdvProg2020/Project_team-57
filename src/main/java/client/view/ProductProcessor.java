package client.view;

import client.api.Command;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import server.controller.account.CustomerControl;
import server.controller.account.VendorControl;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.controller.product.ProductControl;
import server.model.existence.Account;
import server.model.existence.Comment;
import server.model.existence.Product;
import notification.Notification;
import org.controlsfx.control.Rating;
import server.server.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class ProductProcessor extends Processor {

    public void setMenuType(ProductMenuType menuType) {
        this.menuType = menuType;
    }

//    public void similarProductsMouseClicked(MouseEvent mouseEvent) {
//        similarProductsPane.setCollapsible(true);
//        similarProductsPane.setExpanded(!similarProductsPane.isExpanded());
//        similarProductsPane.setCollapsible(false);
//
//        downPartScrollPane.setVvalue(1.0);
//    }

    public static enum ProductMenuType {
        CART, VENDOR_ADD, VENDOR_EDIT, VENDOR_EDIT_UNAPPROVED,
        ADMIN, PRODUCTS_CUSTOMER, PRODUCTS, PRODUCTS_VENDOR,
        COMPARING_PRODUCTS;
    }

    public static enum CommentType {
        ADD, SHOW;
    }

    ProductControl productControl = ProductControl.getController();
    private static VendorControl vendorControl = VendorControl.getController();
    private static CustomerControl customerControl = CustomerControl.getController();

    protected ArrayList<ProductProcessor> subProcessors;

    ///Single Product Menu///

    //MainPane
    public BorderPane mainPane;
    private Product product;
    private ArrayList<Product> similarProducts;
    private AnimationTimer mainTimer;
    private long changePictureTimer = -1;
    private final long CHANGE_PICTURE_PERIOD = 8_000_000_000L;
    public BorderPane upBorderPane;
    public BorderPane downBorderPane;
    private ProductMenuType menuType;

    ///Single Product Menu///


    //Comparing Products Menu///

    //MainPart
    private Product firstProduct;
    private Product secondProduct;

    public BorderPane mainBorderPane;
    public BorderPane imageBorderPane;
    public BorderPane generalBorderPane;

    private Pane firstProductGeneralInfoPane;
    private Pane secondProductGeneralInfoPane;

    //GeneralPart
    public Pane firstGeneralPane;
    public Pane secondGeneralPane;

    //Comparing Products Menu///

    //ProductImagePane
    public Rectangle productImageRectangle;
    public ImageView removeImageButton;
    public ImageView addImageButton;
    public ImageView mediaButton;
    public Label imageNumberLabel;
    public ImageView nextImageButton;
    public ImageView previousImageButton;
    public Pane imagePane;
    public JFXToggleButton slideShowToggleButton;
    public ImageView editImageButton;
    private ArrayList<File> productImageFiles;
    private boolean isNonEdited = false;
    private Product imagePanelProduct;

    //ProductMediaPane
    public ImageView deleteMediaButton;
    public ImageView addMediaButton;
    public ImageView ImageButton;
    public ImageView nextVideo;
    public Label mediaNumberLabel;
    public ImageView PreviousVideo;


    //Sepehr's Section

    //GeneralPane
    public Pane generalPane;
    public JFXTextField nameTextField;
    public JFXTextField categoryTextField;
    public JFXTextArea categoryFeaturesTextArea;
    public JFXToggleButton countableToggleButton;
    public Label countLabel;
    public JFXTextField countTextField;
    public JFXTextField brandTextField;
    public JFXTextArea descriptionTextArea;
    public JFXTextField priceTextField;

    //CommentsPane
    public VBox commentsVBox;
    public Rating averageScore;
    public JFXTextField viewsNum;

    //CommentPane
    private Comment comment;
    public Pane commentPane;
    public JFXTextField userNameComment;
    public JFXTextField commentStatus;
    public JFXTextField commentTitle;
    public JFXTextArea commentContent;
    public JFXButton addComment;
    public Rating commentScore;

    //SimilarPart
    public TitledPane similarProductsPane;
    private static final double PRODUCT_FIELD_WIDTH = 220;
    private static final double PRODUCT_FIELD_HEIGHT = 255;
    public HBox similarProductsHBox;
    public ScrollPane downPartScrollPane;

    //SimilarProductsPane
    private ArrayList<HBox> similarProductHBoxes;
    private static final double HBOX_WIDTH = 885;
    private static final double HBOX_HEIGHT = 265;
    private int pagesNumber;
    private int currentPageNumber;

    //SimilarProductPane
    public Pane similarProductPane;
    public Label productNameLabel;
    public Label oldPriceLabel;
    public Label newPriceLabel;
    public Rectangle productImage;
    public ImageView inOffImage;

    //SpecialPane
    public Pane specialPane;
    public HBox specialImages;
    public ImageView tickImage;
    public ImageView buyersImage;
    public ImageView removeImage;
    public ImageView compareImage;
    public Pane pricePane;
    public JFXTextField price;
    public ImageView offArrow;
    public JFXTextField offPrice;
    public Pane sellerPane;
    public ImageView sellerIcon;
    public JFXTextField seller;
    public Pane statusPane;
    public ImageView statusIcon;
    public JFXTextField status;
    public JFXTextField cartCount;
    public ImageView plusButton;
    public ImageView minusButton;
    public JFXButton addToCart;


    public void initProcessor(Product product, ProductMenuType productMenuType) {
        this.menuType = productMenuType;
        this.product = product;
        subProcessors = new ArrayList<>();
        initImagePanel();
        //sep
        downBorderPane.setLeft(getInitGeneralInfoPane(product));
        initCommentsPane();
        initSpecialPane();

        //Showing Similar Products Page
        switch (menuType) {
            case PRODUCTS_CUSTOMER:
            case PRODUCTS_VENDOR:
            case ADMIN:
            case PRODUCTS:
                setComparingProduct(product.getID(), 1);
                similarProducts = new ArrayList<>(getAllComparingProducts());
                if(similarProducts.isEmpty()) {
                    downBorderPane.getChildren().remove(similarProductsPane);
                } else {
                    //Creating The Similar Pane
                    initSimilarProductsPane();

                    currentPageNumber = 0;
                    similarProductsPane.setContent(similarProductHBoxes.get(currentPageNumber));
                }
                break;
            default:
                downBorderPane.getChildren().remove(similarProductsPane);
        }

    }

    public void initComparingProcessor(Product firstProduct, Product secondProduct) {
        menuType = ProductMenuType.COMPARING_PRODUCTS;
        this.subProcessors = new ArrayList<>();
        this.firstProduct = firstProduct;
        this.secondProduct = secondProduct;

        initComparingImagePanels(true);
        initComparingImagePanels(false);
        initGeneralInfoPane();
    }

    private void initComparingImagePanels(boolean right) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenuImages.fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            subProcessors.add(processor);
            processor.imageNumberLabel.setText("1");
            processor.changePictureTimer = -1;
            processor.mainTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if(processor.changePictureTimer == -1) {
                        processor.changePictureTimer = now;
                    }
                    if(now - processor.changePictureTimer > CHANGE_PICTURE_PERIOD) {
                        processor.nextImage(null);
                        processor.changePictureTimer = now;
                    }
                }
            };
            processor.imagePane.getChildren().removeAll(processor.addImageButton, processor.editImageButton, processor.removeImageButton);
            processor.productImageFiles = new ArrayList<>();
            processor.imagePanelProduct = (!right ? firstProduct : secondProduct);
            processor.getImages();
            if(right)
                imageBorderPane.setRight(root);
            else
                imageBorderPane.setLeft(root);
        } catch (IOException e) {
            //:)
        }
    }

    private void initSimilarProductPane(Product product) {
        this.product = product;
        productImage.setFill(new ImagePattern(getProductImageByID(product.getID(), 1, "product")));
        productNameLabel.setText(product.getName());

        if(product.isOnSale()) {
            oldPriceLabel.setText(getSmoothDoubleFormat(product.getPrice()));
            oldPriceLabel.getStylesheets().addAll(Main.class.getResource("Strikethrough.css").toExternalForm());
            newPriceLabel.setText(getSmoothDoubleFormat(product.getOffPrice()));
        } else {
            similarProductPane.getChildren().removeAll(inOffImage, newPriceLabel);
        }

        similarProductPane.setOnMouseClicked(event -> ((ProductProcessor)parentProcessor).openProductMenu(product));
    }

    public void openProductMenu(Product product) {
        FXMLLoader fxmlLoader = loadThePane("ProductMenu");
        ProductProcessor productProcessor = fxmlLoader.getController();
        addSeenToProduct(product.getID());
        productProcessor.initProcessor(product, menuType);
        myStage.setScene(new Scene(fxmlLoader.getRoot()));
        myStage.setTitle(product.getName() + " Menu");
        productProcessor.setMyStage(myStage);

    }

    private void initGeneralInfoPane() {
        //Todo Empty Panes Dimensions
        generalBorderPane.setLeft(getScrollPaneWithTheseNodesAndDimensions(600, 500, getInitGeneralInfoPane(firstProduct)));
        generalBorderPane.setRight(getScrollPaneWithTheseNodesAndDimensions(600, 500, getInitGeneralInfoPane(secondProduct)));

    }

    private Pane getPaneWithTheseLayouts(double width, double height) {
        Pane pane = new Pane();
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        return pane;
    }

    private ScrollPane getScrollPaneWithTheseNodesAndDimensions(double width, double height, Node node) {
        ScrollPane scrollPane = new ScrollPane(node);
        scrollPane.setPrefWidth(width);
        scrollPane.setPrefHeight(height);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    //ImagePane
    private void initImagePanel() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenuImages.fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            subProcessors.add(processor);
            processor.imageNumberLabel.setText("1");
            processor.changePictureTimer = -1;
            processor.mainTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if(processor.changePictureTimer == -1) {
                        processor.changePictureTimer = now;
                    }
                    if(now - processor.changePictureTimer > CHANGE_PICTURE_PERIOD) {
                        processor.nextImage(null);
                        processor.changePictureTimer = now;
                    }
                }
            };
            if(menuType != ProductMenuType.VENDOR_EDIT && menuType != ProductMenuType.VENDOR_ADD) {
                processor.imagePane.getChildren().removeAll(processor.addImageButton, processor.editImageButton, processor.removeImageButton);
            }
            processor.productImageFiles = new ArrayList<>();
            processor.imagePanelProduct = product;
            processor.getImages();
            upBorderPane.setLeft(root);
        } catch (IOException e) {
            //:)
        }
    }

    private void getImages() {
        switch (((ProductProcessor)parentProcessor).menuType) {
            case VENDOR_EDIT:
                productImageFiles = getProductImageFiles(imagePanelProduct);
                break;
            case ADMIN:
                if(((ProductProcessor)parentProcessor).isNonEdited)
                    productImageFiles = getProductNonEditedImageFiles(imagePanelProduct);
                else
                    productImageFiles = getProductImageFiles(imagePanelProduct);
                break;
            default:
                productImageFiles = getProductNonEditedImageFiles(imagePanelProduct);
        }
        updateImages();
    }

    public ArrayList<File> getProductImageFiles(Product product) {
        if(product.getStatus() == 1)
            return getProductNonEditedImageFiles(product);
        ArrayList<File> imageFiles = new ArrayList<>();
        Command<String> integerCommand = new Command<>("get edit product image count", Command.HandleType.PRODUCT, product.getID());
        Response<Integer> integerResponse = client.postAndGet(integerCommand, Response.class, (Class<Integer>)Integer.class);
        int bound = integerResponse.getDatum();
        for (int i = 0; i < bound; i++) {
            Command<String> command = new Command<>("get edit product image-" + (i + 1), Command.HandleType.PICTURE_GET, product.getID());
            imageFiles.add(client.getFile(command));
        }
        return imageFiles;
    }

    public ArrayList<File> getProductNonEditedImageFiles(Product product) {
        ArrayList<File> imageFiles = new ArrayList<>();
        Command<String> integerCommand = new Command<>("get product image count", Command.HandleType.PRODUCT, product.getID());
        Response<Integer> integerResponse = client.postAndGet(integerCommand, Response.class, (Class<Integer>)Integer.class);
        int bound = integerResponse.getDatum();
        for (int i = 0; i < bound; i++) {
            Command<String> command = new Command<>("get product image-" + (i + 1), Command.HandleType.PICTURE_GET, product.getID());
            imageFiles.add(client.getFile(command));
        }
        return imageFiles;
    }

    private void updateImages() {
        if(productImageFiles.size() != 0) {
            if(productImageFiles.size() > 1) {
                modifyChangeButtons(true);
                modifyVendorButtons(true);
            }
            else {
                modifyChangeButtons(false);
                modifyVendorButtons(true);
            }
            productImageRectangle.setFill(new ImagePattern
                    (getImageFromFileArray((Integer.parseInt(imageNumberLabel.getText()) - 1))));
        } else {
            productImageRectangle.setFill(
                    new ImagePattern(productControl.getProductDefaultImage()));
            modifyVendorButtons(false);
            modifyChangeButtons(false);
        }
    }

    private void modifyVendorButtons(boolean isEnable) {
        removeImageButton.setDisable(!isEnable);
        editImageButton.setDisable(!isEnable);
    }

    private void modifyChangeButtons(boolean isEnable) {
        nextImageButton.setDisable(!isEnable);
        previousImageButton.setDisable(!isEnable);
        slideShowToggleButton.setDisable(!isEnable);
    }

    public void upperButtonsOnMouse(MouseEvent mouseEvent) {
        ImageView button = (ImageView) mouseEvent.getSource();
        String style ="-fx-cursor: hand; -fx-opacity: 1";
        button.setStyle(style);
    }

    public void upperButtonsOutMouse(MouseEvent mouseEvent) {
        ImageView button = (ImageView) mouseEvent.getSource();
        String style ="-fx-opacity: 0.8";
        button.setStyle(style);
    }

    public void previousImage(MouseEvent mouseEvent) {
        changeImage(-1);
    }

    public void nextImage(MouseEvent mouseEvent) {
        changeImage(+1);
    }

    private void changeImage(int k) {
        Rectangle firstRectangle = copyRectangle(productImageRectangle);
        firstRectangle.setFill(new ImagePattern(getImageFromFileArray((Integer.parseInt(imageNumberLabel.getText()) - 1))));
        FadeTransition firstTransition = getFadeTransition(1, 0, 1, firstRectangle);

        int imagesNumber = productImageFiles.size();
        int nextNumber = Integer.parseInt(imageNumberLabel.getText()) + k;

        if(nextNumber == 0) {
            nextNumber = imagesNumber;
        } else if(nextNumber == imagesNumber + 1) {
            nextNumber = 1;
        }
        imageNumberLabel.setText("" + nextNumber);

        Rectangle secondRectangle = copyRectangle(productImageRectangle);
        secondRectangle.setFill(new ImagePattern(getImageFromFileArray((Integer.parseInt(imageNumberLabel.getText()) - 1))));
        FadeTransition secondTransition = getFadeTransition(0, 1, 1, secondRectangle);
        ParallelTransition parallelTransition = new ParallelTransition(firstTransition, secondTransition);
        imagePane.getChildren().addAll(firstRectangle, secondRectangle);
        parallelTransition.play();
        productImageRectangle.setFill(null);
        parallelTransition.setOnFinished(event -> {
            imagePane.getChildren().removeAll(firstRectangle, secondRectangle);
            updateImages();
        });
    }

    private Image getImageFromFileArray(int index) {
        try {
            FileInputStream inStream = new FileInputStream(productImageFiles.get(index));
            Image image = new Image(inStream);
            inStream.close();
            return image;
        } catch (IOException e) {
            //:)
            return null;
        }
    }

    private FadeTransition getFadeTransition(double from, double to, double seconds, Node node) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        fadeTransition.setDuration(Duration.seconds(seconds));
        fadeTransition.setNode(node);
        return fadeTransition;
    }

    private Rectangle copyRectangle(Rectangle rectangle) {
        Rectangle copyRectangle = new Rectangle();
        copyRectangle.setWidth(rectangle.getWidth()); copyRectangle.setHeight(rectangle.getHeight());
        copyRectangle.setLayoutX(rectangle.getLayoutX()); copyRectangle.setLayoutY(rectangle.getLayoutY());
        copyRectangle.setStroke(rectangle.getStroke());
        copyRectangle.setStrokeWidth(rectangle.getStrokeWidth());
        copyRectangle.setArcHeight(rectangle.getArcHeight()); copyRectangle.setArcWidth(rectangle.getArcWidth());
        return copyRectangle;
    }


    public void changeSlideShow(ActionEvent actionEvent) {
        if(slideShowToggleButton.isSelected())
            mainTimer.start();
        else {
            mainTimer.stop();
            changePictureTimer = -1;
        }
    }

    public void deleteImage(MouseEvent mouseEvent) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete This Photo?", ButtonType.NO, ButtonType.YES).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            productImageFiles.remove(Integer.parseInt(imageNumberLabel.getText()) - 1);
            imageNumberLabel.setText("1");
            updateImages();
        }
    }

    public void addNewImage(MouseEvent mouseEvent) {
        if(productImageFiles.size() < 6) {
            File pictureFile = getImageChooser().showOpenDialog(null);
            if (pictureFile != null) {
                productImageFiles.add(pictureFile);
                imageNumberLabel.setText("1");
                updateImages();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "You Can Add 6 Images At Most!").show();
        }
    }

    public void editImage(MouseEvent mouseEvent) {
        File pictureFile = getImageChooser().showOpenDialog(null);
        if(pictureFile != null) {
            int number = Integer.parseInt(imageNumberLabel.getText());
            productImageFiles.remove(number - 1);
            productImageFiles.add(number - 1, pictureFile);
            imageNumberLabel.setText("1");
            updateImages();
        }
    }

    private FileChooser getImageChooser() {
        FileChooser pictureChooser = new FileChooser();

        FileChooser.ExtensionFilter jpgExtensionFilter = new FileChooser.ExtensionFilter("JPG Files", "*.JPG");
        FileChooser.ExtensionFilter jpegExtensionFilter = new FileChooser.ExtensionFilter("JPEG Files", "*.JPEG");
        FileChooser.ExtensionFilter pngExtensionFilter = new FileChooser.ExtensionFilter("PNG Files", "*.PNG");
        FileChooser.ExtensionFilter bmpExtensionFilter = new FileChooser.ExtensionFilter("BMP Files", "*.BMP");

        pictureChooser.getExtensionFilters().add(jpgExtensionFilter);
        pictureChooser.getExtensionFilters().add(jpegExtensionFilter);
        pictureChooser.getExtensionFilters().add(pngExtensionFilter);
        pictureChooser.getExtensionFilters().add(bmpExtensionFilter);

        return pictureChooser;
    }

    public void setNonEdited(boolean nonEdited) {
        isNonEdited = nonEdited;
    }

    //Sepehr's Section

    //TheMainPane
    private void sendProduct() {
        Product product = null;

        ProductProcessor imageProcessor = subProcessors.get(0);
        imageProcessor.stopTimer();
        //sep
        ProductProcessor generalFieldProcessor = subProcessors.get(1);
        ProductProcessor specialFieldProcessor = subProcessors.get(3);

        List<Notification> productNotifications = new ArrayList<>();

        switch (menuType) {
            case VENDOR_ADD:
                product = this.product;
                specialFieldProcessor.setProductSpecialFields(product);
                generalFieldProcessor.setProductGeneralFields(product);
                productNotifications = sendProduct(imageProcessor.productImageFiles, "add", product);
                break;
            case VENDOR_EDIT:
                product = new Product();
                product.setID(this.product.getID());
                product.setSeen(this.product.getSeen());
                product.setSellerUserName(product.getSellerUserName());
                specialFieldProcessor.setProductSpecialFields(product);
                generalFieldProcessor.setProductGeneralFields(product);
                productNotifications = sendProduct(imageProcessor.productImageFiles, "edit", this.product, product);
                break;
        }

        if(productNotifications.get(0).equals(Notification.ADD_PRODUCT)
                || productNotifications.get(0).equals(Notification.EDIT_PRODUCT)) {
            successSending(productNotifications.get(0));
        } else {
            switch (menuType) {
                case VENDOR_ADD:
                    specialFieldProcessor.showProductSpecialErrors(productNotifications);
                    generalFieldProcessor.showProductGeneralErrors(productNotifications);
                    break;
                case VENDOR_EDIT:
                    generalFieldProcessor.showProductGeneralErrors(productNotifications);
                    generalFieldProcessor.setGeneralTextFields(product);
                    product.setOnSale(this.product.isOnSale());
                    product.setOffPrice((1 - this.product.getOffPercent() / 100) * product.getPrice());
                    specialFieldProcessor.setPrices(product);
                    break;
            }

        }

    }

    private List<Notification> sendProduct(ArrayList<File> productImageFiles, String sendType, Product... products) {
        Command<Product> productCommand = new Command<>(sendType + " product", Command.HandleType.PRODUCT, products);
        Response<Notification> productResponse = client.postAndGet(productCommand, Response.class, (Class<Notification>)Notification.class);

        if(sendType.equals("edit")) {
            Command<String> command = new Command<>("delete editing product pictures", Command.HandleType.PRODUCT, product.getID());
            client.postAndGet(command, Response.class, (Class<Object>)Object.class);
        } else {
            if(productResponse.getData().size() != 0 && productResponse.getDatum() == Notification.ADD_PRODUCT)
                product.setID(productResponse.getAdditionalString());
        }

        for (File productImageFile : productImageFiles) {
            String[] splitPath = productImageFile.getPath().split("\\.");
            String fileExtension = splitPath[splitPath.length - 1];
            Command<String> imageCommand = new Command<>(sendType + " product image", Command.HandleType.PICTURE_SEND, product.getID(), fileExtension);
            client.sendImage(imageCommand, productImageFile);
        }

        return productResponse.getData();
    }

    protected void stopTimer() {
        if(mainTimer != null) {
            mainTimer.stop();
        }
    }

    private void successSending(Notification notification) {
        Alert alert = notification.getAlert();
        Optional<ButtonType> optionalButtonType = alert.showAndWait();
        if(optionalButtonType.get() == ButtonType.OK) {
            closeSubStage(myStage, parentProcessor);
            ((ProductsProcessor)parentProcessor).initProductsPage();
        }
    }


    public void removeProduct() {
//        Alert alert = productControl.removeProductById(product.getID()).getAlert();
        Alert alert = removeProductByID(product.getID(), "product").getMessage().getAlert();
        Optional<ButtonType> optionalButtonType = alert.showAndWait();
        if(optionalButtonType.get() == ButtonType.OK) {
            ProductProcessor imageProcessor = subProcessors.get(0);
            imageProcessor.stopTimer();
            closeSubStage(myStage, parentProcessor);
            updateParentProcessor();
        }
    }

    private void updateParentProcessor() {
        if(parentProcessor instanceof ProductsProcessor) {
            ((ProductsProcessor) parentProcessor).initProductsPage();
        } else if(parentProcessor instanceof TableViewProcessor) {
            ((TableViewProcessor) parentProcessor).updateTable();
            ((TableViewProcessor) parentProcessor).updateSelectedItem();
        }
    }

    private void addToCart(String count) {
        if(product.isCountable()) {
            addToCartCountable(product.getID(), count);
        } else {
            addToCartUnCountable(product.getID(), count);
        }

        if(menuType == ProductMenuType.CART) {
            ((ProductsProcessor) parentProcessor).initTotalPricePart();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The Product Added To Cart Successfully");
        alert.setTitle("Congratulations, Buyer!!!");
        alert.setHeaderText("Yeah");
        alert.show();
    }

    private void addToCartUnCountable(String productID, String amountString) {
        Command<String> command = new Command<>("add to cart uncountable", Command.HandleType.PRODUCT, productID, amountString);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private void addToCartCountable(String productID, String countString) {
        Command<String> command = new Command<>("add to cart countable", Command.HandleType.PRODUCT, productID, countString);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    //GeneralInfoPane
    private Pane getInitGeneralInfoPane(Product product) {
        if(menuType == ProductMenuType.VENDOR_EDIT)
            product = /*productControl.getEditedProductByID(product.getID());*/getProductByID(product.getID(), "editing product");

        String generalInfoPaneName = "";
        switch (menuType) {
            case COMPARING_PRODUCTS:
                generalInfoPaneName = "ProductMenuGeneralInfoComparing";
                break;
            case VENDOR_ADD:
            case VENDOR_EDIT:
            case VENDOR_EDIT_UNAPPROVED:
                generalInfoPaneName = "ProductMenuGeneralInfo";
                break;
            case PRODUCTS_CUSTOMER:
            case PRODUCTS_VENDOR:
            case PRODUCTS:
            case ADMIN:
            case CART:
                generalInfoPaneName = "ProductMenuGeneralInfoMainProducts";
                break;
        }

        FXMLLoader loader = loadThePane(generalInfoPaneName);
        ProductProcessor processor = loader.getController();
        subProcessors.add(processor);
        //Todo Check The Use Of MenuType


        processor.setGeneralTextFields(product);
//        mainPane.setLeft(loader.getRoot());
        return loader.getRoot();
    }

    private void setGeneralTextFields(Product product) {
        //Todo Condition Checking With Enum With Unknown Space Of Saving

        //Todo Checke Sangin
        this.product = product;

        ProductMenuType menuType = ((ProductProcessor) parentProcessor).menuType;
        setGeneralStringTextFields();

        //Todo getQuantityOfCart
        if(menuType == ProductMenuType.CART) {
            if(product.isCountable()) {
                product.setCount(/*productControl.getProductById(product.getID())*/getProductByID(product.getID(), "product").getCount());
            } else {
                product.setAmount(/*productControl.getProductById(product.getID())*/getProductByID(product.getID(), "product").getAmount());
            }
        }

        if(menuType == ProductMenuType.VENDOR_ADD) {
            changeCountableField(null);
        } else {
            switch (menuType) {
                case COMPARING_PRODUCTS:
                    if(product.isOnSale()) {
                        offPrice.setText(getSmoothDoubleFormat(product.getOffPrice()));
                    } else {
                        generalPane.getChildren().removeAll(offArrow, offPrice);
                    }
                case PRODUCTS_CUSTOMER:
                case PRODUCTS_VENDOR:
                case PRODUCTS:
                case ADMIN:
                case CART:
                    if(product.getCategoryFeatures() == null || product.getCategoryFeatures().length() == 0)
                        categoryFeaturesTextArea.setText(" Not Set");
                    else
                        categoryFeaturesTextArea.setText(product.getCategoryFeatures());
            }

            nameTextField.setText(product.getName());

            categoryTextField.setText(product.getCategory());

            countableToggleButton.setSelected(product.isCountable());


            changeCountableField(null);
//            if (product.isCountable()) {
//                countTextField.setText(Integer.toString(product.getCount()));
//            } else {
//                countTextField.setText(Double.toString(product.getAmount()));
//            }

            brandTextField.setText(product.getBrand());

            descriptionTextArea.setText(product.getDescription());

            if(menuType == ProductMenuType.COMPARING_PRODUCTS)
                priceTextField.setText(getSmoothDoubleFormat(product.getPrice()));

            if (menuType != ProductMenuType.VENDOR_EDIT)
                disableEditingGeneralFields();
        }

        if(menuType == ProductMenuType.VENDOR_EDIT) {
            countableToggleButton.setDisable(true);
            changeCountableField(null);
        }
        //Todo
    }

    private void setGeneralStringTextFields() {
        setStringFields(nameTextField, 20);
        setStringFields(categoryTextField, 20);
        setStringFields(brandTextField, 20);
        setStringFields(descriptionTextArea, 100);
    }

    private void disableEditingGeneralFields() {
        switch (menuType) {
            case COMPARING_PRODUCTS:
                priceTextField.setEditable(false);
            case PRODUCTS_CUSTOMER:
            case PRODUCTS_VENDOR:
            case PRODUCTS:
            case ADMIN:
            case CART:
                categoryFeaturesTextArea.setEditable(false);
            case VENDOR_ADD:
            case VENDOR_EDIT:
            case VENDOR_EDIT_UNAPPROVED:
                nameTextField.setEditable(false);
                categoryTextField.setEditable(false);
                countableToggleButton.setDisable(true);
                countTextField.setEditable(false);
                brandTextField.setEditable(false);
                descriptionTextArea.setEditable(false);
        }
    }

    private void setProductGeneralFields(Product product) {

        product.setName(nameTextField.getText());
        product.setCategory(categoryTextField.getText());

        product.setCountable(countableToggleButton.isSelected());

        if(countTextField.getText() != null && !countTextField.getText().isEmpty()) {
            switch (countLabel.getText()) {
                case " Count ":
                    product.setCount(Integer.parseInt(countTextField.getText()));
                    break;
                case " Amount ":
                    product.setAmount(Double.parseDouble(countTextField.getText()));
                    break;
            }
        }

        product.setBrand(brandTextField.getText());
        product.setDescription(descriptionTextArea.getText());

    }

    private void showProductGeneralErrors(List<Notification> productNotifications) {

//        for (Notification productNotification : productNotifications) {
//            productNotification.getAlert().show();
//        }
        //Todo Check

        if(!productNotifications.get(0).equals(Notification.EMPTY_PRODUCT_PRICE))
            productNotifications.get(0).getAlert().show();

        if(productNotifications.contains(Notification.EMPTY_PRODUCT_NAME))
            nameTextField.setStyle(errorTextFieldStyle);

        if(productNotifications.contains(Notification.EMPTY_PRODUCT_CATEGORY)
                || productNotifications.contains(Notification.INVALID_PRODUCT_CATEGORY)) {
            categoryTextField.setStyle(errorTextFieldStyle);
        }

        if(productNotifications.contains(Notification.EMPTY_PRODUCT_COUNT))
            countTextField.setStyle(errorTextFieldStyle);

        if(productNotifications.contains(Notification.EMPTY_PRODUCT_BRAND))
            brandTextField.setStyle(errorTextFieldStyle);

        if(productNotifications.contains(Notification.EMPTY_PRODUCT_NAME))
            descriptionTextArea.setStyle(errorTextFieldStyle);
    }

//    private void editProduct() {
//        String productID = ((ProductProcessor) parentProcessor).product.getID();
//
//        Alert alert = null;
//        alert = editField("ProductName", nameTextField, productID, alert);
//        alert = editField("Category", categoryTextField, productID, alert);
//
//        String countFieldName = countLabel.getText().equals(" Count ") ? "Count" : "Amount";
//        alert = editField(countFieldName, countTextField, productID, alert);
//
//        alert = editField("Brand", brandTextField, productID, alert);
//        alert = editField("Description", descriptionTextArea, productID, alert);
//
//        if(alert.getTitle().equals("Edit Successful") ) {
//            ((ProductProcessor) parentProcessor).product = productControl.getEditedProductByID(productID);
//            setGeneralTextFields();
//        }
//
//        //Todo Koodoomaro Taraf Mitoone Khali Bezare?
//        //Todo Setting Alerts
//        alert.show();
//    }

    private Alert editField(String fieldName, TextInputControl textInputControl, String productID, Alert previousAlert) {
        Alert alert = productControl.editField(fieldName, textInputControl.getText(), productID).getAlert();

        if(!alert.getTitle().equals("Edit Successful"))
            textInputControl.setStyle(errorTextFieldStyle);

        if(previousAlert == null || previousAlert.getTitle().equals("Edit Successful"))
            return alert;
        else
            return previousAlert;
    }

    public void changeCountableField(ActionEvent actionEvent) {
        //Todo Checking Setting Change Listener Multiple Times !!!! Exactly
        Product product = ((ProductProcessor) parentProcessor).product;

        if(product == null)
            product = this.product;

        JFXTextField countTextFieldClone = getCloneOfCountTextField(countTextField);
        generalPane.getChildren().remove(countTextField);
        generalPane.getChildren().add(countTextFieldClone);
        countTextField = countTextFieldClone;

        if(countableToggleButton.isSelected()) {
            if(!countLabel.getText().equals(" Count ")) {
                countLabel.setText(" Count ");
                countLabel.setLayoutX(countLabel.getLayoutX() + 15);
            }

            countTextFieldClone.setText(Integer.toString(product.getCount()));
            setIntegerFields(countTextFieldClone, Integer.MAX_VALUE);
        } else {
            if(!countLabel.getText().equals(" Amount ")) {
                countLabel.setText(" Amount ");
                countLabel.setLayoutX(countLabel.getLayoutX() - 15);
            }

            countTextFieldClone.setText(getSmoothDoubleFormat(product.getAmount()));
            setDoubleFields(countTextFieldClone, Double.MAX_VALUE);
        }
    }

    public JFXTextField getCloneOfCountTextField(JFXTextField countTextField) {
        JFXTextField jfxTextField = new JFXTextField();

        //Properties
        jfxTextField.setEditable(countTextField.isEditable());
        jfxTextField.setFont(countTextField.getFont());

        //Layout
        jfxTextField.setPrefWidth(countTextField.getPrefWidth());
        jfxTextField.setPrefHeight(countTextField.getPrefHeight());
        jfxTextField.setLayoutX(countTextField.getLayoutX());
        jfxTextField.setLayoutY(countTextField.getLayoutY());

        //Code
        jfxTextField.setId(countTextField.getId());
        jfxTextField.setOnAction(this::textFieldMouseClicked);
        jfxTextField.setOnMouseClicked(this::textFieldMouseClicked);

        return jfxTextField;
    }


    public void textFieldMouseClicked(Event actionEvent) {
        TextInputControl textInputControl = (TextInputControl) actionEvent.getSource();
        textInputControl.setStyle(textInputControl.getStyle().replace(errorTextFieldStyle, ""));
    }

    //CommentsPane
    private void initCommentsPane() {
        FXMLLoader loader = loadThePane("ProductMenuCommentsPane");
        ProductProcessor processor = loader.getController();
        subProcessors.add(processor);
        processor.initCommentsThroughThePane();
        downBorderPane.setRight(loader.getRoot());
    }

    private void initCommentsThroughThePane() {
        Product product = ((ProductProcessor) parentProcessor).product;
        String productID = product.getID();
        int seen = product.getSeen();

        averageScore.setRating(getAverageScore(productID));
        averageScore.setDisable(true);

        viewsNum.setText(Integer.toString(seen));
        viewsNum.setDisable(true);

        commentsVBox.getChildren().removeAll(commentsVBox.getChildren());
        for (Comment productComment : getAllProductComments(productID))
            commentsVBox.getChildren().add(getCommentPane("ProductMenuShowCommentPane", productComment, CommentType.SHOW));

        //HaHa ????????
        switch (menuType) {
//            case PRODUCTS:
            case PRODUCTS_CUSTOMER:
            case CART:
                Comment comment = new Comment();
                comment.setProductID(productID);
                commentsVBox.getChildren().add(getCommentPane("ProductMenuAddCommentPane", comment, CommentType.ADD));
        }

    }

    private List<Comment> getAllProductComments(String productID) {
        Command<String> command = new Command<>("get product comments", Command.HandleType.PRODUCT, productID);
        Response<Comment> response = client.postAndGet(command, Response.class, (Class<Comment>)Comment.class);
        return response.getData();
    }

    //CommentPane
    private Pane getCommentPane(String panePath,Comment comment, CommentType commentType) {
        FXMLLoader loader = loadThePane(panePath);
        ProductProcessor processor = loader.getController();

        processor.initCommentFields(comment, commentType);
        return (Pane) loader.getRoot();
    }

    private void initCommentFields(Comment productComment, CommentType commentType) {
        Product product = ((ProductProcessor)parentProcessor.parentProcessor).product;
        switch (commentType) {
            case SHOW:
                userNameComment.setText(" " + productComment.getCustomerUsername());
                commentStatus.setText(" " + productComment.getTheStatus());
                commentTitle.setText(productComment.getTitle());
                commentContent.setText(productComment.getContent());
                commentScore.setRating(productComment.getScore());
                disableCommentFields();
//                commentPane.getChildren().remove(addComment);
                if(productComment.getScore() == 0)
                    commentPane.getChildren().remove(commentScore);

                break;
            case ADD:
                comment = productComment;
                setStringFields(commentTitle, 16);
                setStringFields(commentContent, 100);
                if(!isProductPurchasedByCustomer(product.getID(), getUsername())) {
                    commentPane.getChildren().remove(commentScore);
                }
                break;
        }
    }

    private void disableCommentFields() {
//        userNameComment.setDisable(true);
//        commentTitle.setDisable(true);
//        commentContent.setDisable(true);
//        commentScore.setDisable(true);
        userNameComment.setEditable(false);
        commentStatus.setEditable(false);
        commentTitle.setEditable(false);
        commentContent.setEditable(false);
        commentScore.setDisable(true);
    }

    public void addComment(ActionEvent actionEvent) {
        boolean isTitleEmpty = false, isContentEmpty = false;

        if(isTitleEmpty = (commentTitle.getText() == null || commentTitle.getText().isEmpty()))
            commentTitle.setStyle(commentTitle.getStyle() + " " + errorTextFieldStyle);

        if(isContentEmpty = (commentContent.getText() == null || commentContent.getText().isEmpty()))
            commentContent.setStyle(commentContent.getStyle() + " " + errorTextFieldStyle);

        if(!(isContentEmpty && isTitleEmpty)) {
            comment.setTitle(commentTitle.getText());
            comment.setContent(commentContent.getText());
            comment.setScore((int) commentScore.getRating());

            addTheComment(comment);

            //Todo Check If It Works Without The ProductType Or Not
            ((ProductProcessor) parentProcessor).initCommentsThroughThePane();
            //Todo Showing Alert Or Not
        }
    }

    private void addTheComment(Comment comment) {
        Command<Comment> command = new Command<>("add comment", Command.HandleType.PRODUCT, comment);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    //SpecialInfoPane
    private void initSpecialFields() {
        //Getting Off Price From parentProduct (Setting It From Control)
        Product product = ((ProductProcessor) parentProcessor).product;
        switch (menuType) {
            //Except Customer Section
            case VENDOR_EDIT_UNAPPROVED:
                specialImages.getChildren().removeAll(tickImage, buyersImage);
                specialImages.setLayoutX(specialImages.getLayoutX() + 80);
                price.setText(getSmoothDoubleFormat(product.getPrice()));
                price.setEditable(false);
                pricePane.getChildren().removeAll(offArrow, offPrice);
                specialPane.getChildren().removeAll(sellerPane, statusPane);
                pricePane.setLayoutX(78);
                break;
//                price.setDisable(true);
            case VENDOR_ADD:
                specialImages.getChildren().removeAll(buyersImage, removeImage);
                specialImages.setLayoutX(specialImages.getLayoutX() + 80);
                pricePane.getChildren().removeAll(offArrow, offPrice);
                specialPane.getChildren().removeAll(sellerPane, statusPane);
                pricePane.setLayoutX(78);
                setDoubleFields(price, Double.MAX_VALUE);
                break;
            case VENDOR_EDIT:
                setPrices(product);
                /*price.setText(Double.toString(product.getPrice()));
                setDoubleFields(price, Double.MAX_VALUE);
                if(product.isOnSale()) {
                    //Todo Set Price StrikeThrough
                    offPrice.setText(Double.toString(product.getOffPrice()));
                    offPrice.setEditable(false);
                    setTheCommunicationOfPrices();
                } else {
                    pricePane.getChildren().removeAll(offArrow, offPrice);
                }*/
                status.setText(product.getTheStatus());
                status.setEditable(false);
//                status.setDisable(true);
                specialPane.getChildren().remove(sellerPane);
                pricePane.setLayoutY(123);
                statusPane.setLayoutY(224);
                break;
            case ADMIN:
                specialImages.getChildren().removeAll(tickImage, buyersImage);
                specialImages.setLayoutX(specialImages.getLayoutX() + 80);
                initSpecialFieldsInGeneral();
                break;

            //Customer Section
            case CART:
                addToCart.setText("Change Quantity");
                addToCart.setLayoutX(122);
//                specialPane.getChildren().removeAll(cartCount, minusButton, plusButton);
                setCartCountRawText(product);
                setCartFields();
                initSpecialFieldsInGeneral();
                break;
            case PRODUCTS:
            case PRODUCTS_CUSTOMER:
                setCartCountRawText(product);
//                if(!product.isCountable())
//                    cartCount.setText(Double.toString(Math.min(product.getAmount(), 0.2)));
                initSpecialFieldsInGeneral();
                setCartFields();
                break;
            case PRODUCTS_VENDOR:
                specialPane.getChildren().removeAll(minusButton, plusButton);
                specialPane.getChildren().remove(cartCount);
                specialPane.getChildren().remove(addToCart);
                specialPane.getChildren().remove(compareImage);
                final int layoutYSetter = 5;
                pricePane.setLayoutY(pricePane.getLayoutY() + layoutYSetter);
                sellerPane.setLayoutY(sellerPane.getLayoutY() + layoutYSetter);
                statusPane.setLayoutY(statusPane.getLayoutY() + layoutYSetter);
                initSpecialFieldsInGeneral();
        }

        if(product.isOnSale()) {
            offPrice.setText(getSmoothDoubleFormat(product.getOffPrice()));
            offPrice.setEditable(false);
        }

    }

    private void initSpecialPane() {
        //Todo Condition Making For Choosing The Right Special Pane
        String paneName = null;

        switch (menuType) {
            case VENDOR_ADD:
            case VENDOR_EDIT:
            case VENDOR_EDIT_UNAPPROVED:
            case ADMIN:
                paneName = "ProductMenuSpecialInfoExceptCustomer";
                break;
            case CART:
            case PRODUCTS_CUSTOMER:
            case PRODUCTS:
            case PRODUCTS_VENDOR:
                paneName = "ProductMenuSpecialInfoCustomer";
                break;
        }

        FXMLLoader loader = loadThePane(paneName);
        ProductProcessor processor = loader.getController();
        subProcessors.add(processor);
        processor.initSpecialFields();
        upBorderPane.setRight(loader.getRoot());
    }

    private void initSpecialFieldsInGeneral() {
        Product product = ((ProductProcessor) parentProcessor).product;

        setPrices(product);
        seller.setText(product.getSellerUserName());
        seller.setEditable(false);

        status.setText(product.getTheStatus());
        status.setEditable(false);
    }

    private void setPrices(Product product) {
        price.setText(getSmoothDoubleFormat(product.getPrice()));
        setDoubleFields(price, Double.MAX_VALUE);

        if(product.isOnSale()) {
            if(!pricePane.getChildren().contains(offPrice)) {
                pricePane.getChildren().addAll(offArrow, offPrice);
            }
            //Todo Set Price StrikeThrough
            offPrice.setText(getSmoothDoubleFormat(product.getOffPrice()));
            offPrice.setEditable(false);
            setTheCommunicationOfPrices();
        } else {
            pricePane.getChildren().removeAll(offArrow, offPrice);
        }

        if(menuType != ProductMenuType.VENDOR_EDIT)
            price.setEditable(false);
    }

    private void setTheCommunicationOfPrices() {
        price.textProperty().addListener((observable, oldValue, newValue) -> {
            Product product = ((ProductProcessor) parentProcessor).product;
            offPrice.setDisable(false);

            if(newValue == null || newValue.isEmpty()) {
                offPrice.setText("");
            } else {
                double newOffPrice = (1.0 - product.getOffPercent() / 100) * Double.parseDouble(newValue);
                offPrice.setText(getSmoothDoubleFormat(newOffPrice));
            }

            offPrice.setDisable(true);
        });
    }


    private void setCartCountRawText(Product product) {
        switch (menuType) {
            case CART:
//                Product cartProduct = customerControl.getCartProductByID(product.getID());
                Product cartProduct = getProductByID(product.getID(), "cart product");

                if(product.isCountable()) {
                    cartCount.setText(Integer.toString(cartProduct.getCount()));
                } else {
                    cartCount.setText(getDoubleFormat(cartProduct.getAmount()));
                }
                break;
            case PRODUCTS_CUSTOMER:
            case PRODUCTS:
                if(!product.isCountable())
                    cartCount.setText(getDoubleFormat(Math.min(product.getAmount(), 0.2)));

        }
    }

    private void setCartFields() {
        Product product = ((ProductProcessor) parentProcessor).product;

        if(product.isCountable())
            setCartCountable();
        else
            setCartUnCountable();
    }

    private void setCartCountable() {
        Product product = ((ProductProcessor) parentProcessor).product;

        setIntegerFields(cartCount, product.getCount() + 1);
        cartCount.textProperty().addListener((observable, oldValue, newValue) -> {
            //Todo Check
            if(newValue.isEmpty() || newValue.equals("0")) {
                cartCount.setText("1");
            }
        });
        plusButton.setOnMouseClicked(event -> addCartCount());
        minusButton.setOnMouseClicked(event -> subtractCartCount());
    }

    public void addCartCount() {
        Product product = ((ProductProcessor) parentProcessor).product;

        int previousCartCount = Integer.parseInt(cartCount.getText());

        if(previousCartCount < product.getCount())
            cartCount.setText(Integer.toString(++previousCartCount));

    }

    public void subtractCartCount() {
        int previousCartCount = Integer.parseInt(cartCount.getText());

        if(previousCartCount > 1)
            cartCount.setText(Integer.toString(--previousCartCount));

    }


    private void setCartUnCountable() {
        Product product = ((ProductProcessor) parentProcessor).product;

        setDoubleFields(cartCount, product.getAmount() + 0.000001);
        cartCount.textProperty().addListener((observable, oldValue, newValue) -> {
            //Todo Check
            if(newValue.isEmpty() || newValue.equals("0") /*|| newValue.equals("0.")*/) {
                if (product.getAmount() > 0.2)
                    cartCount.setText("0.2");
                else
                    cartCount.setText("0.002");
            }
        });
        plusButton.setOnMouseClicked(event -> addCartAmount());
        minusButton.setOnMouseClicked(event -> subtractCartAmount());
    }

    public void addCartAmount() {
        Product product = ((ProductProcessor) parentProcessor).product;

        double previousCartAmount = Double.parseDouble(cartCount.getText());

        if(previousCartAmount < product.getAmount()) {
            double nextProductAmount = product.getAmount() - previousCartAmount > 0.2 ? previousCartAmount + 0.2 : product.getAmount();
//            DecimalFormat doubleFormatter = new DecimalFormat("#.#");
//            doubleFormatter.setRoundingMode(RoundingMode.HALF_UP);
            cartCount.setText(getDoubleFormat(nextProductAmount));
        }
    }

    public void subtractCartAmount() {
        double previousCartAmount = Double.parseDouble(cartCount.getText());

        //Todo Check
        if(previousCartAmount > 0.2)
            cartCount.setText(getDoubleFormat((Math.ceil(previousCartAmount * 5) - 1) / 5));
//            cartCount.setText(Double.toString((Math.ceil(previousCartAmount * 5) - 1) / 5));

    }


    //Similar Products Part
    private void initSimilarProductsPane() {
        similarProductHBoxes = new ArrayList<>();
        pagesNumber = (int)Math.ceil(similarProducts.size() / 4.0);

        for (int i = 0; i < pagesNumber; i++) {
            HBox hBox = new HBox();
            hBox.setPrefWidth(HBOX_WIDTH);
            hBox.setPrefHeight(HBOX_HEIGHT);
            int similarProductBeginIndex = 4 * i, similarProductEndIndex = Math.min(4 * (i + 1), similarProducts.size());

            hBox.getChildren().add(getSimilarProductPagePane(i != 0, false));

            for (Product similarProduct : similarProducts.subList(similarProductBeginIndex, similarProductEndIndex)) {
                Pane pane = new Pane();
                pane.setPrefHeight(255);
                pane.setPrefWidth(205);
                FXMLLoader fxmlLoader = loadThePane("SimilarProductPane");
                ProductProcessor similarProductProcessor = fxmlLoader.getController();
                similarProductProcessor.initSimilarProductPane(similarProduct);
                pane.getChildren().add(fxmlLoader.getRoot());
                hBox.getChildren().add(pane);
            }

            hBox.getChildren().add(getSimilarProductPagePane(i != pagesNumber - 1, true));

            similarProductHBoxes.add(hBox);
        }
    }

    private Pane getSimilarProductPagePane(boolean isNecessary, boolean isNext) {
        Pane pane = new Pane();
        pane.setPrefWidth(30);
        pane.setPrefHeight(HBOX_HEIGHT);

        if(isNecessary) {
            ImageView imageView = new ImageView();
            imageView.setLayoutY(HBOX_HEIGHT / 2 - 15);
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            imageView.getStyleClass().add("Page_Button");

            FileInputStream fileInputStream = null;

            try {
                if (isNext) {
                    imageView.setOnMouseClicked(event -> nextPage());
                    fileInputStream = new FileInputStream("src\\main\\resources\\" + IMAGE_FOLDER_URL + "Icons\\ProductMenu\\NextPage.png");

                } else {
                    imageView.setOnMouseClicked(event -> previousPage());
                    fileInputStream = new FileInputStream("src\\main\\resources\\" + IMAGE_FOLDER_URL + "Icons\\ProductMenu\\PreviousPage.png");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            imageView.setImage(new Image(fileInputStream));
            pane.getChildren().add(imageView);
        }

        return pane;
    }

    public void previousPage() {
        changePage(false);
    }

    public void nextPage() {
        changePage(true);
    }

    private void changePage(boolean isNext) {
        if(isNext)
            currentPageNumber++;
        else
            currentPageNumber--;

        similarProductsPane.setContent(similarProductHBoxes.get(currentPageNumber));
    }


    //Main Functions Part
    public void addToCartMouseClicked() {
        ((ProductProcessor) parentProcessor).addToCart(cartCount.getText());
    }

    public void tickMouseClicked(MouseEvent mouseEvent) {
        ((ProductProcessor) parentProcessor).sendProduct();
    }

    public void viewBuyers(MouseEvent mouseEvent) {
        ((ProductProcessor) parentProcessor).showBuyers();
    }

    public void showBuyers() {
        try {
            vendorControl.setCurrentProduct(product.getID());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
            Parent root = loader.load();
            TableViewProcessor<Account> tableViewProcessor = loader.getController();
            tableViewProcessor.setParentProcessor(this.parentProcessor);
            tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.PRODUCT_BUYERS);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.setTitle("Manage Offs");
            parentProcessor.parentProcessor.addSubStage(newStage);
            tableViewProcessor.setMyStage(newStage);
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Buyers.png")));
            newStage.show();
        } catch (IOException e) {
            //:)
        }
    }

    public void removeProductMouseClicked(MouseEvent mouseEvent) {
        ((ProductProcessor) parentProcessor).removeProduct();
    }

    public void compareProductMouseClicked(MouseEvent mouseEvent) {
        ((ProductProcessor)parentProcessor).openComparingProductsMenu();
    }

    private void openComparingProductsMenu() {
        setComparingProduct(product.getID(), 1);
        Stage stage = new Stage();
        stage.setTitle("Comparable Products");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductsMenuComparableProducts.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
            ProductsProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.initProcessor(ProductsProcessor.ProductsMenuType.PRODUCT_COMPARING_PRODUCTS);
            processor.setMyStage(stage);
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            addSubStage(stage);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("Compare 1.png")));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProductSpecialFields(Product product) {
        if(price.getText() != null && !price.getText().isEmpty())
            product.setPrice(Double.parseDouble(price.getText()));
    }

    private void showProductSpecialErrors(List<Notification> productNotifications) {
        if(productNotifications.contains(Notification.EMPTY_PRODUCT_PRICE)) {
            price.setStyle(errorTextFieldStyle);
        }
    }

    private String getDoubleFormat(double number) {
        DecimalFormat doubleFormatter = new DecimalFormat("#.#");
        doubleFormatter.setRoundingMode(RoundingMode.HALF_UP);
        return doubleFormatter.format(number);
    }

    private FXMLLoader loadThePane(String paneName) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(paneName + ".fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            //subProcessors.add(processor);
            processor.parentProcessor = this;
            processor.menuType = menuType;
            return loader;
        } catch (IOException e) {
            //:)
        }
        return null;
    }
    //Sepehr's Section

    private double getAverageScore(String productID) {
        Command<String> command = new Command<>("get average score", Command.HandleType.PRODUCT, productID);
        Response<String> response = client.postAndGet(command, Response.class, (Class<String>)String.class);
        return Double.parseDouble(response.getDatum());
    }

}
