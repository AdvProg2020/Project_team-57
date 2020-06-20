package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import controller.Control;
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


    public void setMenuType(ProductMenuType menuType) {
        this.menuType = menuType;
    }

    public static enum ProductMenuType {
        CART, VENDOR_ADD, VENDOR_EDIT, ADMIN, PRODUCTS
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

    //ProductGeneralInfoPane
    public Pane generalInfoPane;
    public JFXTextField nameTextField;
    public JFXTextField categoryTextField;
    public JFXToggleButton countableToggleButton;
    public Label countLabel;
    public JFXTextField countTextField;
    public JFXTextField brandTextField;
    public JFXTextArea descriptionTextArea;
    public JFXButton saveChangesButton;

    //ProductCommentsPane
    public VBox commentsVBox;
    public Rating averageScore;
    public JFXTextField viewsNum;

    //ProductCommentPane
    private Comment comment;
    public Pane commentPane;
    public JFXTextField commentTitle;
    public JFXTextArea commentContent;
    public JFXButton addComment;
    public JFXTextField userNameComment;
    public Rating commentScore;

    public void initProcessor(Product product, ProductMenuType productMenuType) {
        this.menuType = productMenuType;
        this.product = product;
        initImagePanel();

        //Sepehr's Section
        initGeneralInfoPane(productMenuType);
        initCommentsPane(productMenuType);
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
    private void initGeneralInfoPane(ProductMenuType productMenuType) {
        try {
            if(productMenuType == ProductMenuType.VENDOR_EDIT)
                product = productControl.getEditedProductByID(product.getID());

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenuGeneralInfo.fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.setGeneralTextFields();
            mainPane.setLeft(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        generalInfoPane.getChildren().remove(saveChangesButton);
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
    private void initCommentsPane(ProductMenuType productMenuType) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenuCommentsPane.fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.initCommentsThroughThePane();
            mainPane.setRight(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initCommentsThroughThePane() {
        Product product = ((ProductProcessor) parentProcessor).product;
        String productID = product.getID();
        int seen = product.getSeen();

        averageScore.setRating(productControl.getAverageScore(productID));
        averageScore.setDisable(true);

        viewsNum.setText(Integer.toString(seen));
        viewsNum.setDisable(true);

        for (Comment productComment : productControl.getAllProductComments(productID))
            commentsVBox.getChildren().add(getCommentPane("ProductMenuShowCommentPane", productComment, CommentType.SHOW));

        if(Control.getType() != null && Control.getType().equals("Customer")) {
            Comment comment = new Comment();
            comment.setProductID(productID);
            commentsVBox.getChildren().add(getCommentPane("ProductMenuAddCommentPane", new Comment(), CommentType.ADD));
        }
    }

    private Pane getCommentPane(String panePath,Comment comment, CommentType commentType) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(panePath + ".fxml"));
            Parent root = loader.load();
            ProductProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.initCommentFields(comment, commentType);
            return (Pane) root;
        } catch (IOException e) {
            System.out.println("Shit. Error In Loading Comment Pane");
            e.printStackTrace();
        }

        return null;
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
            ((ProductProcessor) parentProcessor).initCommentsThroughThePane();
            //Todo Showing Alert Or Not
        }
    }
    //Sepehr's Section

}
