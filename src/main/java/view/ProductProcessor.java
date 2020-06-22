package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
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
import model.existence.Comment;
import model.existence.Product;
import org.controlsfx.control.Rating;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProductProcessor extends Processor {

    public Pane sellerPane;
    public Pane statusPane;
    public Pane pricePane;

    public void setMenuType(ProductMenuType menuType) {
        this.menuType = menuType;
    }

    public static enum ProductMenuType {
        CART, VENDOR_ADD, VENDOR_EDIT, ADMIN, CUSTOMER, PRODUCTS;
    }

    public static enum CommentType {
        ADD, SHOW;
    }

    ProductControl productControl = ProductControl.getController();
    private static VendorControl vendorControl = VendorControl.getController();

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
    public JFXButton saveChangesButton;

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
        initImagePanel();

        //Sepehr's Section
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
            processor.imageNumberLabel.setText("1");
            processor.setImage();
            if(!productControl.doesProductHaveImage(product.getID())) {
                processor.disableChangeButtons(true);
            }
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
            if(menuType != ProductMenuType.VENDOR_ADD && menuType != ProductMenuType.VENDOR_EDIT) {
                processor.imagePane.getChildren().removeAll(removeImageButton, editImageButton, addImageButton);
            }
            upBorderPane.setLeft(root);
        } catch (IOException e) {
        e.printStackTrace();
        }
    }

    private void disableChangeButtons(boolean imageButton) {
        nextImageButton.setDisable(true);
        previousImageButton.setDisable(true);
        slideShowToggleButton.setDisable(true);
        removeImageButton.setDisable(true);
        editImageButton.setDisable(true);
    }

    private void setImage() {
        int imageNumber = Integer.parseInt(imageNumberLabel.getText());
        productImageRectangle.setFill(new ImagePattern
                        (productControl.getProductImageByID(((ProductProcessor)parentProcessor).product.getID(), imageNumber)));
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
        Rectangle firstRectangle = copyRectangle(productImageRectangle);
        firstRectangle.setFill(new ImagePattern(productControl.getProductImageByID(((ProductProcessor)parentProcessor).product.getID(), Integer.parseInt(imageNumberLabel.getText()))));
        FadeTransition firstTransition = getFadeTransition(1, 0, 1, firstRectangle);

        int imagesNumber = productControl.getProductImagesNumberByID(((ProductProcessor)parentProcessor).product.getID());
        int nextNumber = Integer.parseInt(imageNumberLabel.getText()) - 1;
        if(nextNumber == 0) {
            nextNumber = imagesNumber;
        }
        imageNumberLabel.setText("" + nextNumber);

        Rectangle secondRectangle = copyRectangle(productImageRectangle);
        secondRectangle.setFill(new ImagePattern(productControl.getProductImageByID(((ProductProcessor)parentProcessor).product.getID(), Integer.parseInt(imageNumberLabel.getText()))));
        FadeTransition secondTransition = getFadeTransition(0, 1, 1, secondRectangle);
        ParallelTransition parallelTransition = new ParallelTransition(firstTransition, secondTransition);
        imagePane.getChildren().addAll(firstRectangle, secondRectangle);
        parallelTransition.play();
        productImageRectangle.setFill(null);
        parallelTransition.setOnFinished(event -> {
            imagePane.getChildren().removeAll(firstRectangle, secondRectangle);
            setImage();
        });
        setImage();
    }

    public void nextImage(MouseEvent mouseEvent) {
        Rectangle firstRectangle = copyRectangle(productImageRectangle);
        firstRectangle.setFill(new ImagePattern(productControl.getProductImageByID(((ProductProcessor)parentProcessor).product.getID(), Integer.parseInt(imageNumberLabel.getText()))));
        FadeTransition firstTransition = getFadeTransition(1, 0, 1, firstRectangle);

        int imagesNumber = productControl.getProductImagesNumberByID(((ProductProcessor)parentProcessor).product.getID());
        int nextNumber = Integer.parseInt(imageNumberLabel.getText()) + 1;
        if(nextNumber == imagesNumber + 1) {
            nextNumber = 1;
        }
        imageNumberLabel.setText("" + nextNumber);

        Rectangle secondRectangle = copyRectangle(productImageRectangle);
        secondRectangle.setFill(new ImagePattern(productControl.getProductImageByID(((ProductProcessor)parentProcessor).product.getID(), Integer.parseInt(imageNumberLabel.getText()))));
        FadeTransition secondTransition = getFadeTransition(0, 1, 1, secondRectangle);
        ParallelTransition parallelTransition = new ParallelTransition(firstTransition, secondTransition);
        imagePane.getChildren().addAll(firstRectangle, secondRectangle);
        parallelTransition.play();
        productImageRectangle.setFill(null);
        parallelTransition.setOnFinished(event -> {
            imagePane.getChildren().removeAll(firstRectangle, secondRectangle);
            setImage();
        });

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
            changePictureTimer = -1;
        }
    }

    public void deleteImage(MouseEvent mouseEvent) {
        productControl.deleteProductImage(((ProductProcessor)parentProcessor).product.getID(), Integer.parseInt(imageNumberLabel.getText()));
        ((ProductProcessor)parentProcessor).initImagePanel();
    }

    public void addNewImage(MouseEvent mouseEvent) {
        File pictureFile = getImageChooser().showOpenDialog(null);
        if(pictureFile != null) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(pictureFile);
                Image image = new Image(fileInputStream);
                productControl.addProductPicture(((ProductProcessor)parentProcessor).product.getID(), pictureFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ((ProductProcessor)parentProcessor).initImagePanel();
    }

    public void editImage(MouseEvent mouseEvent) {
        File pictureFile = getImageChooser().showOpenDialog(null);
        if(pictureFile != null) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(pictureFile);
                Image image = new Image(fileInputStream);
                productImageRectangle.setFill(null);
                System.gc();
                productControl.editProductPicture(((ProductProcessor)parentProcessor).product.getID(), pictureFile, Integer.parseInt(imageNumberLabel.getText()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ((ProductProcessor)parentProcessor).initImagePanel();
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

    //GeneralInfoPane
    private void initGeneralInfoPane() {
        if(menuType == ProductMenuType.VENDOR_EDIT)
            product = productControl.getEditedProductByID(product.getID());

        FXMLLoader loader = loadThePane("ProductMenuGeneralInfo");
        ProductProcessor processor = loader.getController();
        //Todo Check The Use Of MenuType

        processor.setGeneralTextFields();
        mainPane.setLeft(loader.getRoot());
    }

    private void setGeneralTextFields() {
        //Todo Condition Checking With Enum With Unknown Space Of Saving
        Product product = ((ProductProcessor) parentProcessor).product;
        ProductMenuType menuType = ((ProductProcessor) parentProcessor).menuType;
        setGeneralStringTextFields();

        if(menuType == ProductMenuType.VENDOR_ADD) {
            //Todo Changing The Name Of Save Changes Button
        } else {
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

            if(menuType != ProductMenuType.VENDOR_EDIT)
                disableEditingGeneralFields();

            //Todo
        }
    }

    private void setGeneralStringTextFields() {
        setStringFields(nameTextField, 20);
        setStringFields(categoryTextField, 20);
        setStringFields(brandTextField, 20);
        setStringFields(descriptionTextArea, 100);
    }

    private void disableEditingGeneralFields() {
        generalPane.getChildren().remove(saveChangesButton);
        nameTextField.setDisable(true);
        categoryTextField.setDisable(true);
        countableToggleButton.setDisable(true);
        countTextField.setDisable(true);
        brandTextField.setDisable(true);
        descriptionTextArea.setDisable(true);
    }

    public void saveChangesAction(ActionEvent actionEvent) {
        ProductMenuType menuType = ((ProductProcessor) parentProcessor).menuType;

        if(menuType == ProductMenuType.VENDOR_ADD) {
            addProduct();
        } else if(menuType == ProductMenuType.VENDOR_EDIT) {
            editProduct();
        } else {
            System.out.println("Shit. Error In Save Changes");
        }
    }

    private void addProduct() {
        product = new Product();
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

        //Todo Koodoomaro Taraf Mitoone Khali Bezare?
        vendorControl.addProduct(product);
        System.out.println("Yeah, Baby");
    }

    private void editProduct() {
        String productID = ((ProductProcessor) parentProcessor).product.getID();

        Alert alert = null;
        alert = editField("ProductName", nameTextField, productID, alert);
        alert = editField("Category", categoryTextField, productID, alert);

        String countFieldName = countLabel.getText().equals(" Count ") ? "Count" : "Amount";
        alert = editField(countFieldName, countTextField, productID, alert);

        alert = editField("Brand", brandTextField, productID, alert);
        alert = editField("Description", descriptionTextArea, productID, alert);

        if(alert.getTitle().equals("Edit Successful") ) {
            ((ProductProcessor) parentProcessor).product = productControl.getEditedProductByID(productID);
            setGeneralTextFields();
        }

        //Todo Koodoomaro Taraf Mitoone Khali Bezare?
        //Todo Setting Alerts
        alert.show();
    }

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

        if(menuType == ProductMenuType.CUSTOMER) {
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
                break;
            case ADD:
                comment = productComment;
                setStringFields(commentTitle, 16);
                setStringFields(commentContent, 100);
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
            case CUSTOMER:
            case PRODUCTS:
                paneName = "ProductMenuSpecialInfoCustomer";
                break;
            default:
                System.out.println("Error In Init Special Pane");
        }

        FXMLLoader loader = loadThePane(paneName);
        ProductProcessor processor = loader.getController();
        processor.initSpecialFields();
        upBorderPane.setRight(loader.getRoot());
    }

    private void initSpecialFields() {
        //Getting Off Price From parentProduct (Setting It From Control)
        Product product = ((ProductProcessor) parentProcessor).product;
        product.setOnSale(true);
        product.setOffPrice(3000);
        product.setOffPercent(2500.0 / 55);
//        System.out.println(product.isOnSale());
        switch (menuType) {
            //Except Customer Section
            case VENDOR_ADD:
                tickImage.setOnMouseClicked(event -> addCreatedProduct());
                specialImages.getChildren().removeAll(buyersImage, removeImage);
                specialImages.setLayoutX(specialImages.getLayoutX() + 80);
                pricePane.getChildren().removeAll(offArrow, offPrice);
                specialPane.getChildren().removeAll(sellerPane, statusPane);
                pricePane.setLayoutX(78);
                setDoubleFields(price, Double.MAX_VALUE);
                break;
            case VENDOR_EDIT:
                tickImage.setOnMouseClicked(event -> editCreatedProduct());
                setPrices();
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
                addToCart.setText("Remove From Cart");
                addToCart.setLayoutX(122);
                specialPane.getChildren().removeAll(cartCount, minusButton, plusButton);
            case PRODUCTS:
            case CUSTOMER:
                initSpecialFieldsInGeneral();
                setCartFields();
                break;

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

        setPrices();
        seller.setText(product.getSellerUserName());
        seller.setDisable(true);

        status.setText(product.getTheStatus());
        status.setDisable(true);
    }

    private void setPrices() {
        Product product = ((ProductProcessor) parentProcessor).product;

        price.setText(Double.toString(product.getPrice()));
        setDoubleFields(price, Double.MAX_VALUE);

        if(product.isOnSale()) {
            //Todo Set Price StrikeThrough
            offPrice.setText(Double.toString(product.getOffPrice()));
            offPrice.setEditable(false);
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
            offPrice.setEditable(true);

            if(newValue == null || newValue.isEmpty()) {
                offPrice.setText("");
            } else {
                double newOffPrice = (1.0 - product.getOffPercent() / 100) * Double.parseDouble(newValue);
                offPrice.setText(Double.toString(newOffPrice));
            }

            offPrice.setEditable(false);
        });
    }


    private void setCartFields() {
        if(product.isCountable())
            setCartCountable();
        else
            setCartUnCountable();
    }

    private void setCartCountable() {
        Product product = ((ProductProcessor) parentProcessor).product;

        setIntegerFields(cartCount, product.getCount());
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
            if(newValue.isEmpty() || newValue.equals("0")) {
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
            cartCount.setText(Double.toString(0.2 * Math.ceil(previousCartAmount * 5) - 0.2));
    }

    public void addCreatedProduct() {
        //Todo
    }

    public void editCreatedProduct() {
        //Todo
    }

    public void viewBuyers(MouseEvent mouseEvent) {
        //Todo
    }

    public void removeProduct(MouseEvent mouseEvent) {
        //Todo
    }



    private FXMLLoader loadThePane(String paneName) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(paneName + ".fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
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
