package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import controller.Control;
import controller.account.CustomerControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
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
import javafx.util.Duration;
import model.db.CartTable;
import model.existence.Comment;
import model.existence.Product;
import notification.Notification;
import org.controlsfx.control.Rating;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ProductProcessor extends Processor {

    public Pane sellerPane;
    public Pane statusPane;
    public Pane pricePane;

    public void setMenuType(ProductMenuType menuType) {
        this.menuType = menuType;
    }

    public static enum ProductMenuType {
        CART, VENDOR_ADD, VENDOR_EDIT, ADMIN, PRODUCTS_CUSTOMER, PRODUCTS, PRODUCTS_VENDOR;
    }

    public static enum CommentType {
        ADD, SHOW;
    }

    ProductControl productControl = ProductControl.getController();
    private static VendorControl vendorControl = VendorControl.getController();
    private static CustomerControl customerControl = CustomerControl.getController();

    private ArrayList<ProductProcessor> subProcessors;

    //MainPane
    public BorderPane mainPane;
    private Product product;
    private AnimationTimer mainTimer;
    private long changePictureTimer = -1;
    private long changePicturePeriod = 8_000_000_000L;
    public BorderPane upBorderPane;
    private ProductMenuType menuType;

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
    public JFXToggleButton countableToggleButton;
    public Label countLabel;
    public JFXTextField countTextField;
    public JFXTextField brandTextField;
    public JFXTextArea descriptionTextArea;

    //CommentsPane
    public VBox commentsVBox;
    public Rating averageScore;
    public JFXTextField viewsNum;

    //CommentPane
    private Comment comment;
    public Pane commentPane;
    public JFXTextField commentTitle;
    public JFXTextArea commentContent;
    public JFXButton addComment;
    public JFXTextField userNameComment;
    public Rating commentScore;

    //SpecialPane
    public Pane specialPane;
    public HBox specialImages;
    public ImageView tickImage;
    public ImageView buyersImage;
    public ImageView removeImage;
    public JFXTextField price;
    public ImageView offArrow;
    public JFXTextField offPrice;
    public ImageView sellerIcon;
    public JFXTextField seller;
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
        initGeneralInfoPane();
        initCommentsPane();
        initSpecialPane();
    }

    private void initImagePanel() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenuImages.fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            subProcessors.add(processor);
            processor.imageNumberLabel.setText("1");
            changePictureTimer = -1;
            mainTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if(changePictureTimer == -1) {
                        changePictureTimer = now;
                    }
                    if(now - changePictureTimer > changePicturePeriod) {
                        processor.nextImage(null);
                        changePictureTimer = now;
                    }
                }
            };
            if(menuType != ProductMenuType.VENDOR_EDIT && menuType != ProductMenuType.VENDOR_ADD) {
                processor.imagePane.getChildren().removeAll(processor.addImageButton, processor.editImageButton, processor.removeImageButton);
            }
            processor.productImageFiles = new ArrayList<>();
            processor.getImages();
            upBorderPane.setLeft(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getImages() {
        if(menuType != ProductMenuType.VENDOR_ADD) {
            productImageFiles = productControl.
                    getProductImageFiles(((ProductProcessor)parentProcessor).product);
        }
        updateImages();
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
        } catch (IOException e) { e.printStackTrace(); }
        return null;
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
            ((ProductProcessor)parentProcessor).mainTimer.start();
            else {
            ((ProductProcessor)parentProcessor).mainTimer.stop();
            ((ProductProcessor)parentProcessor).changePictureTimer = -1;
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
        File pictureFile = getImageChooser().showOpenDialog(null);
        if(pictureFile != null) {
            productImageFiles.add(pictureFile);
            imageNumberLabel.setText("1");
            updateImages();
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

    //Sepehr's Section

    //TheMainPane
    private void sendProduct() {
        Product product = null;

        ProductProcessor imageProcessor = subProcessors.get(0);
        //sep
        ProductProcessor generalFieldProcessor = subProcessors.get(1);
        ProductProcessor specialFieldProcessor = subProcessors.get(3);

        ArrayList<Notification> productNotifications = new ArrayList<>();

        switch (menuType) {
            case VENDOR_ADD:
                product = this.product;
                specialFieldProcessor.setProductSpecialFields(product);
                generalFieldProcessor.setProductGeneralFields(product);
                productNotifications = vendorControl.addProduct(product, imageProcessor.productImageFiles);
                break;
            case VENDOR_EDIT:
                product = new Product();
                product.setID(this.product.getID());
                product.setSeen(this.product.getSeen());
                product.setSellerUserName(product.getSellerUserName());
                specialFieldProcessor.setProductSpecialFields(product);
                generalFieldProcessor.setProductGeneralFields(product);
                productNotifications.add(vendorControl.editProduct(this.product, product, imageProcessor.productImageFiles));
                break;
            default:
                System.out.println("Serious Error In Sending Product");
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
                    specialFieldProcessor.setPrices(product);
                    break;
            }

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
        Alert alert = productControl.removeProductById(product.getID()).getAlert();
        Optional<ButtonType> optionalButtonType = alert.showAndWait();
        if(optionalButtonType.get() == ButtonType.OK) {
            closeSubStage(myStage, parentProcessor);
            ((ProductsProcessor)parentProcessor).initProductsPage();
        }
    }

    private void addToCart(String count) {
        if(product.isCountable()) {
            customerControl.addToCartCountable(product.getID(), Integer.parseInt(count));
        } else {
            customerControl.addToCartUnCountable(product.getID(), Double.parseDouble(count));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The Product Added To Cart Successfully");
        alert.setTitle("Congratulations, Buyer!!!");
        alert.setHeaderText("Yeah");
        alert.show();
    }

    //GeneralInfoPane
    private void initGeneralInfoPane() {
        if(menuType == ProductMenuType.VENDOR_EDIT)
            product = productControl.getEditedProductByID(product.getID());

        FXMLLoader loader = loadThePane("ProductMenuGeneralInfo");
        ProductProcessor processor = loader.getController();
        subProcessors.add(processor);
        //Todo Check The Use Of MenuType

        processor.setGeneralTextFields(product);
        mainPane.setLeft(loader.getRoot());
    }

    private void setGeneralTextFields(Product product) {
        //Todo Condition Checking With Enum With Unknown Space Of Saving
        ProductMenuType menuType = ((ProductProcessor) parentProcessor).menuType;
        setGeneralStringTextFields();

        //Todo getQuantityOfCart
        if(menuType == ProductMenuType.CART) {
            if(product.isCountable()) {
                product.setCount(productControl.getProductById(product.getID()).getCount());
            } else {
                product.setAmount(productControl.getProductById(product.getID()).getAmount());
            }
        }
        if(menuType != ProductMenuType.VENDOR_ADD) {
            nameTextField.setText(product.getName());

            categoryTextField.setText(product.getCategory());

            countableToggleButton.setSelected(product.isCountable());
            changeCountableField(null);

            if (product.isCountable())
                countTextField.setText(Integer.toString(product.getCount()));
            else
                countTextField.setText(Double.toString(product.getAmount()));

            brandTextField.setText(product.getBrand());

            descriptionTextArea.setText(product.getDescription());

            if (menuType != ProductMenuType.VENDOR_EDIT)
                disableEditingGeneralFields();
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
        nameTextField.setDisable(true);
        categoryTextField.setDisable(true);
        countableToggleButton.setDisable(true);
        countTextField.setDisable(true);
        brandTextField.setDisable(true);
        descriptionTextArea.setDisable(true);
    }

    private void setProductGeneralFields(Product product) {

        product.setName(nameTextField.getText());
        product.setCategory(categoryTextField.getText());

        product.setCountable(countableToggleButton.isSelected());

        switch (countLabel.getText()) {
            case " Count " :
                product.setCount(Integer.parseInt(countTextField.getText()));
                break;
            case " Amount " :
                product.setAmount(Double.parseDouble(countTextField.getText()));
                break;
            default:
                System.out.println("Fuck!!!! \nError In Save Changes Count Amount Part");
        }

        product.setBrand(brandTextField.getText());
        product.setDescription(descriptionTextArea.getText());

    }

    private void showProductGeneralErrors(ArrayList<Notification> productNotifications) {

        for (Notification productNotification : productNotifications) {
            productNotification.getAlert().show();
        }

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

        if(countableToggleButton.isSelected() && countLabel.getText().equals(" Amount ")) {
            countLabel.setText(" Count ");
            //Todo Check Layout Function
            countLabel.setLayoutX(countLabel.getLayoutX() + 15);
            countTextField.setText(Integer.toString(product.getCount()));
            setIntegerFields(countTextField, Integer.MAX_VALUE);
        } else if(!countableToggleButton.isSelected() && countLabel.getText().equals(" Count ")) {
            countLabel.setText(" Amount ");
            //Todo Check Layout Function
            countTextField.setText(Double.toString(product.getAmount()));
            countLabel.setLayoutX(countLabel.getLayoutX() - 15);
            setDoubleFields(countTextField, Double.MAX_VALUE);
        }
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
        mainPane.setRight(loader.getRoot());
    }

    private void initCommentsThroughThePane() {
        Product product = ((ProductProcessor) parentProcessor).product;
        String productID = product.getID();
        int seen = product.getSeen();

        averageScore.setRating(productControl.getAverageScore(productID));
        averageScore.setDisable(true);

        viewsNum.setText(Integer.toString(seen));
        viewsNum.setDisable(true);

        commentsVBox.getChildren().removeAll(commentsVBox.getChildren());
        for (Comment productComment : productControl.getAllProductComments(productID))
            commentsVBox.getChildren().add(getCommentPane("ProductMenuShowCommentPane", productComment, CommentType.SHOW));

        //HaHa 🤣🤣🤣🤣
        switch (menuType) {
//            case PRODUCTS:
            case PRODUCTS_CUSTOMER:
            case CART:
                Comment comment = new Comment();
                comment.setProductID(productID);
                commentsVBox.getChildren().add(getCommentPane("ProductMenuAddCommentPane", comment, CommentType.ADD));
        }

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
                userNameComment.setText(productComment.getCustomerUsername());
                commentTitle.setText(productComment.getTitle());
                commentContent.setText(productComment.getContent());
                commentScore.setRating(productComment.getScore());
                userNameComment.setDisable(true);
                commentTitle.setDisable(true);
                commentContent.setDisable(true);
                commentScore.setDisable(true);
//                commentPane.getChildren().remove(addComment);
                if(productComment.getScore() == 0)
                    commentPane.getChildren().remove(commentScore);

                break;
            case ADD:
                comment = productComment;
                setStringFields(commentTitle, 16);
                setStringFields(commentContent, 100);
                if(!customerControl.isProductPurchasedByCustomer(product.getID(), Control.getUsername())) {
                    commentPane.getChildren().remove(commentScore);
                }
                    break;
            default:
                System.out.println("Shit. Error In InitCommentFields");
        }
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

            productControl.addComment(comment);

            //Todo Check If It Works Without The ProductType Or Not
            ((ProductProcessor) parentProcessor).initCommentsThroughThePane();
            //Todo Showing Alert Or Not
        }
    }

    //SpecialInfoPane
    private void initSpecialPane() {
        //Todo Condition Making For Choosing The Right Special Pane
        String paneName = null;

        switch (menuType) {
            case VENDOR_ADD:
            case VENDOR_EDIT:
            case ADMIN:
                paneName = "ProductMenuSpecialInfoExceptCustomer";
                break;
            case CART:
            case PRODUCTS_CUSTOMER:
            case PRODUCTS:
            case PRODUCTS_VENDOR:
                paneName = "ProductMenuSpecialInfoCustomer";
                break;
            default:
                System.out.println("Error In Init Special Pane");
        }

        FXMLLoader loader = loadThePane(paneName);
        ProductProcessor processor = loader.getController();
        subProcessors.add(processor);
        processor.initSpecialFields();
        upBorderPane.setRight(loader.getRoot());
    }

    private void initSpecialFields() {
        //Getting Off Price From parentProduct (Setting It From Control)
        Product product = ((ProductProcessor) parentProcessor).product;
//        System.out.println(product.isOnSale());
        switch (menuType) {
            //Except Customer Section
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
                status.setDisable(true);
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
                Product cartProduct = customerControl.getCartProductByID(product.getID());

                if(product.isCountable()) {
                    cartCount.setText(Integer.toString(cartProduct.getCount()));
                } else {
                    cartCount.setText(Double.toString(cartProduct.getAmount()));
                }

                setCartFields();
                initSpecialFieldsInGeneral();
                break;
            case PRODUCTS:
            case PRODUCTS_CUSTOMER:
                initSpecialFieldsInGeneral();
                setCartFields();
                break;
            case PRODUCTS_VENDOR:
                specialPane.getChildren().removeAll(minusButton, plusButton);
                specialPane.getChildren().remove(cartCount);
                specialPane.getChildren().remove(addToCart);

                final int layoutYSetter = 5;
                pricePane.setLayoutY(pricePane.getLayoutY() + layoutYSetter);
                sellerPane.setLayoutY(sellerPane.getLayoutY() + layoutYSetter);
                statusPane.setLayoutY(statusPane.getLayoutY() + layoutYSetter);
            default:
                System.out.println("Error In #initSpecialFields");
                break;
        }

        if(product.isOnSale()) {
            offPrice.setText(Double.toString(product.getOffPrice()));
            offPrice.setEditable(false);
        }


    }

    private void initSpecialFieldsInGeneral() {
        Product product = ((ProductProcessor) parentProcessor).product;

        setPrices(product);
        seller.setText(product.getSellerUserName());
        seller.setDisable(true);

        status.setText(product.getTheStatus());
        status.setDisable(true);
    }

    private void setPrices(Product product) {
        price.setText(Double.toString(product.getPrice()));
        setDoubleFields(price, Double.MAX_VALUE);

        if(product.isOnSale()) {
            //Todo Set Price StrikeThrough
            offPrice.setText(Double.toString(product.getOffPrice()));
            offPrice.setDisable(true);
            setTheCommunicationOfPrices();
        } else {
            pricePane.getChildren().removeAll(offArrow, offPrice);
        }

        if(menuType != ProductMenuType.VENDOR_EDIT)
            price.setDisable(true);
    }

    private void setTheCommunicationOfPrices() {
        price.textProperty().addListener((observable, oldValue, newValue) -> {
            Product product = ((ProductProcessor) parentProcessor).product;
            offPrice.setDisable(false);

            if(newValue == null || newValue.isEmpty()) {
                offPrice.setText("");
            } else {
                double newOffPrice = (1.0 - product.getOffPercent() / 100) * Double.parseDouble(newValue);
                offPrice.setText(Double.toString(newOffPrice));
            }

            offPrice.setDisable(true);
        });
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
            if(newValue.isEmpty() || newValue.equals("0") || newValue.equals("0.")) {
                if (product.getAmount() > 0.2)
                    cartCount.setText("0.2");
                else
                    cartCount.setText("0.000002");
            }
        });
        plusButton.setOnMouseClicked(event -> addCartAmount());
        minusButton.setOnMouseClicked(event -> subtractCartAmount());
    }

    public void addCartAmount() {
        Product product = ((ProductProcessor) parentProcessor).product;

        double previousCartAmount = Double.parseDouble(cartCount.getText());

        if(previousCartAmount < product.getAmount())
            cartCount.setText(Double.toString(product.getAmount() - previousCartAmount > 0.2 ? previousCartAmount + 0.2 : product.getAmount()));
    }

    public void subtractCartAmount() {
        double previousCartAmount = Double.parseDouble(cartCount.getText());

        //Todo Check
        if(previousCartAmount > 0.2)
            cartCount.setText(Double.toString(Math.ceil(previousCartAmount * 5) / 5 - 0.2));
    }

    public void addToCartMouseClicked() {
        ((ProductProcessor) parentProcessor).addToCart(cartCount.getText());
    }

    public void tickMouseClicked(MouseEvent mouseEvent) {
        ((ProductProcessor) parentProcessor).sendProduct();
    }

    public void viewBuyers(MouseEvent mouseEvent) {
        //Todo
    }

    public void removeProductMouseClicked(MouseEvent mouseEvent) {
        ((ProductProcessor) parentProcessor).removeProduct();
    }

    private void setProductSpecialFields(Product product) {
        product.setPrice(Double.parseDouble(price.getText()));
    }

    private void showProductSpecialErrors(ArrayList<Notification> productNotifications) {
        if(productNotifications.contains(Notification.EMPTY_PRODUCT_PRICE)) {
            price.setStyle(errorTextFieldStyle);
        }
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
            System.out.println("Shit. Error In Loading The Pane : " + paneName);
            e.printStackTrace();
        }

        return null;
    }
    //Sepehr's Section

}
