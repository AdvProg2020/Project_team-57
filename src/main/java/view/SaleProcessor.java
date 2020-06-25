package view;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import controller.Control;
import controller.account.AdminControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.existence.Discount;
import model.existence.Off;
import notification.Notification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SaleProcessor extends Processor implements Initializable {
    private static AdminControl adminControl = AdminControl.getController();
    private static ProductControl productControl = ProductControl.getController();

    //DiscountProcess
    public Label discountCodeLabel;
    public Label maxDiscountLabel;
    public Label maxRepetitionLabel;
    public Label discountPercentLabel;
    public Label finishDateLabel;
    public Label startDateLabel;
    public JFXTextField discountCodeTextField;
    public JFXTextField maxDiscountTextField;
    public JFXTextField maxRepetitionTextField;
    public JFXTextField discountPercentTextField;
    public JFXDatePicker startDatePicker;
    public JFXTimePicker startTimePicker;
    public JFXDatePicker finishDatePicker;
    public JFXTimePicker finishTimePicker;
    public ImageView saveChangeButton;
    public BorderPane discountMainPane;
    public Pane discountInfoPane, discountCustomersPane;
    public Pane addOffButton;
    public VBox optionsVbox;

    private Discount discount;
    //OffProcess
    public BorderPane offMainPane;
    public Pane offInfoPane;
    public Pane offProductsPane;
    private Off off;
    public JFXDatePicker offStartDatePicker;
    public JFXTextField offNameField;
    public JFXTextField offPercentField;
    public JFXDatePicker offFinishDatePicker;
    public JFXTimePicker offStartTimePicker;
    public JFXTimePicker offFinishTimePicker;
    public ImageView saveOffChangeButton;
    public Pane offInfoMainPane;
    public Rectangle offImageRectangle;
    public ImageView deleteImageButton;
    private File offImageFile;
    private boolean isDefaultPicture;
    private boolean isEditing = false;
    private boolean isPreviousOff = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String locationFile = location.getFile();

        if(locationFile.contains("DiscountMenuInfo")) {
            setFieldsSpecifications();
        } else if(locationFile.contains("OffMenuInfo")) {
            setOffFieldsSpecifications();
        }
    }

    private void setOffFieldsSpecifications() {
        setDoubleFields(offPercentField, 100.000001);
        offStartTimePicker.set24HourView(true);
        offFinishTimePicker.set24HourView(true);
    }

    private void setFieldsSpecifications() {
        setDoubleFields(discountPercentTextField, 100.000001);
        setIntegerFields(maxRepetitionTextField, Integer.MAX_VALUE);
        setDoubleFields(maxDiscountTextField, Double.MAX_VALUE);

        startTimePicker.set24HourView(true);
        finishTimePicker.set24HourView(true);
    }

    private void setFields() {
        Discount mainDiscount = ((SaleProcessor) parentProcessor).discount;

        if(mainDiscount == null) {
            ((SaleProcessor) parentProcessor).discount = new Discount();
        } else {
            discountCodeTextField.setText(mainDiscount.getCode());

            if(mainDiscount.getDiscountPercent() != 0)
                discountPercentTextField.setText(Double.toString(mainDiscount.getDiscountPercent()));

            if(mainDiscount.getMaxRepetition() != 0)
                maxRepetitionTextField.setText(Integer.toString(mainDiscount.getMaxRepetition()));

            if(mainDiscount.getMaxDiscount() != 0)
                maxDiscountTextField.setText(Double.toString(mainDiscount.getMaxDiscount()));

            if(mainDiscount.getStartDate() != null)
                setDateFieldsFromDate(startDatePicker, startTimePicker, mainDiscount.getStartDate());

            if(mainDiscount.getFinishDate() != null)
                setDateFieldsFromDate(finishDatePicker, finishTimePicker, mainDiscount.getFinishDate());

            if(Control.getType().equals("Admin")) {
                if(mainDiscount.getID() != null) {
                    startDatePicker.setEditable(false);
                    startTimePicker.setEditable(false);
                }
            } else  {
                discountCodeLabel.setDisable(true);
                discountCodeTextField.setDisable(true);
                discountPercentLabel.setDisable(true);
                discountPercentTextField.setDisable(true);
                maxRepetitionLabel.setDisable(true);
                maxRepetitionTextField.setDisable(true);
                maxDiscountLabel.setDisable(true);
                maxDiscountTextField.setDisable(true);

                startDateLabel.setDisable(true);
                startDatePicker.setDisable(true);
                startTimePicker.setDisable(true);

                finishDateLabel.setDisable(true);
                finishDatePicker.setDisable(true);
                finishTimePicker.setDisable(true);

                Pane pane = (Pane)discountMainPane.getCenter();
                pane.getChildren().remove(saveChangeButton);
            }
        }
    }

    private void setOffFields() {
        Off mainOff = ((SaleProcessor)parentProcessor).off;
        if(mainOff == null) {
            ((SaleProcessor)parentProcessor).off = new Off();
            ((SaleProcessor)parentProcessor).off.setOffID("");
            ((SaleProcessor)parentProcessor).getOffImageFile();
        } else {
//            System.out.println(":|");
            if(mainOff.getOffName() != null && mainOff.getOffName().length() != 0)
                offNameField.setText(mainOff.getOffName());
            if(mainOff.getOffPercent() != 0)
                offPercentField.setText("" + mainOff.getOffPercent());
            if(mainOff.getStartDate() != null)
                setDateFieldsFromDate(offStartDatePicker, offStartTimePicker, mainOff.getStartDate());
            if(mainOff.getFinishDate() != null)
                setDateFieldsFromDate(offFinishDatePicker, offFinishTimePicker, mainOff.getFinishDate());
            //((SaleProcessor)parentProcessor).getOffImageFile();
            if(Control.getType().equals("Admin")) {
                setAdminOffInfoFields(mainOff);
            } else {
                setVendorOffInfoFields(mainOff);
            }
        }
        updateImageRectangle();
    }

    private void setAdminOffInfoFields(Off off) {
        offNameField.setEditable(false);
        offPercentField.setEditable(false);
        offStartDatePicker.setDisable(true);
        offStartDatePicker.setOpacity(0.99);
        offStartTimePicker.setDisable(true);
        offStartTimePicker.setOpacity(0.99);
        offFinishDatePicker.setDisable(true);
        offFinishDatePicker.setOpacity(0.99);
        offFinishTimePicker.setDisable(true);
        offFinishTimePicker.setOpacity(0.99);
        offInfoMainPane.getChildren().remove(saveOffChangeButton);
        ((SaleProcessor)parentProcessor).optionsVbox.getChildren().remove(((SaleProcessor)parentProcessor).addOffButton);
    }

    private void setVendorOffInfoFields(Off off) {
        if(off.getStatus() != 2 && !isEditing) {
            offNameField.setEditable(true);
            offPercentField.setEditable(true);
            offStartDatePicker.setDisable(false);
            offStartTimePicker.setDisable(false);
            offFinishDatePicker.setDisable(false);
            offFinishTimePicker.setDisable(false);
            offStartDatePicker.setOpacity(1);
            offStartTimePicker.setOpacity(1);
            offFinishDatePicker.setOpacity(1);
            offFinishTimePicker.setOpacity(1);
        } else if(off.getStatus() != 2 && isEditing) {
            offStartDatePicker.setDisable(true);
            offStartDatePicker.setOpacity(0.99);
            offStartTimePicker.setDisable(true);
            offStartTimePicker.setOpacity(0.99);
        } else if(off.getStatus() == 2) {
            setAdminOffInfoFields(off);
        }
    }

    private void updateImageRectangle() {
        try {
            FileInputStream fileInputStream = new FileInputStream(((SaleProcessor)parentProcessor).offImageFile);
            Image image = new Image(fileInputStream);
            offImageRectangle.setFill(new ImagePattern(image));
            System.out.println(((SaleProcessor)parentProcessor).isDefaultPicture);
            if(((SaleProcessor)parentProcessor).isDefaultPicture) {
                offImageRectangle.setStrokeWidth(0);
                deleteImageButton.setDisable(true);
            } else {
                offImageRectangle.setStrokeWidth(2);
                deleteImageButton.setDisable(false);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getOffImageFile() {
        if(productControl.isOffEditing(off.getOffID()) && !isPreviousOff) {
            offImageFile = productControl.getEditingOffImageFileByID(off.getOffID());
            isDefaultPicture = !productControl.doesEditingOffHaveImage(off.getOffID());
        } else {
            offImageFile = productControl.getOffImageFileByID(off.getOffID());
            isDefaultPicture = !productControl.doesOffHaveImage(off.getOffID());
        }

        /*Image image = (off != null && off.getOffID() != null && off.getOffID().length() != 0 ?
                productControl.getOffImageByID(off.getOffID()) : productControl.getOffImageByID("1"));
        offImageRectangle.setFill(new ImagePattern(image));
        if(off == null || off.getOffID() != null || off.getOffID().length() == 0 || productControl.doesOffHaveImage(off.getOffID()))
            offImageRectangle.setStrokeWidth(0);*/
    }

    private void setDateFieldsFromDate(JFXDatePicker datePicker, JFXTimePicker timePicker, Date date) {
        LocalDateTime localDateTime = new Timestamp(date.getTime()).toLocalDateTime();
        datePicker.setValue(localDateTime.toLocalDate());
        timePicker.setValue(localDateTime.toLocalTime());
    }

    public void discountCustomersMouseClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewMenu.fxml"));
            Parent root = loader.load();
            discountInfoPane.setStyle("");
            discountCustomersPane.setStyle("-fx-background-color: #90CAF9;   -fx-background-radius: 0 10 10 0;");

            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.initProcessor(TableViewProcessor.TableViewType.DISCOUNT_CUSTOMERS);
            discountMainPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void discountInfoMouseClicked(MouseEvent mouseEvent) {
        try {
            if(discountMainPane.getCenter() == null || discountMainPane.getCenter().getId().equals("mainBorderPane")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DiscountMenuInfo.fxml"));
                discountMainPane.setCenter(loader.load());
                discountCustomersPane.setStyle("");
                discountInfoPane.setStyle("-fx-background-color: #90CAF9;   -fx-background-radius: 0 10 10 0;");

                SaleProcessor saleProcessor = loader.getController();
                saleProcessor.parentProcessor = this;
                saleProcessor.setFields();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AddDiscountMouseClicked(MouseEvent mouseEvent) {
        //Todo Setting Notifications
        Notification notification = adminControl.addAddedDiscount(discount);

        Optional<ButtonType> optionalButtonType = notification.getAlert().showAndWait();

        if(optionalButtonType.get() == ButtonType.OK) {
            if(notification.equals(Notification.ADD_DISCOUNT)) {
                updateParentTable();
                this.myStage.close();
            } else
                discountInfoMouseClicked(null);
        }
    }

    public void saveChangesMouseClicked(MouseEvent mouseEvent) {
        Discount discount = ((SaleProcessor) parentProcessor).discount;

        if(!isDateTimeEmpty(startDatePicker, startTimePicker)) {
            LocalDateTime localStartDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimePicker.getValue());
            Date startDate = new Date(Timestamp.valueOf(localStartDateTime).getTime());
            discount.setStartDate(startDate);
        }

        if(!isDateTimeEmpty(finishDatePicker, finishTimePicker)) {
            LocalDateTime localFinishDateTime = LocalDateTime.of(finishDatePicker.getValue(), finishTimePicker.getValue());
            Date finishDate = new Date(Timestamp.valueOf(localFinishDateTime).getTime());
            discount.setFinishDate(finishDate);
        }

        if(!isTextFieldEmpty(discountCodeTextField))
            discount.setCode(discountCodeTextField.getText());
        if(!isTextFieldEmpty(discountPercentTextField))
            discount.setDiscountPercent(Double.parseDouble(discountPercentTextField.getText()));
        if(!isTextFieldEmpty(maxDiscountTextField))
            discount.setMaxDiscount(Double.parseDouble(maxDiscountTextField.getText()));
        if(!isTextFieldEmpty(maxRepetitionTextField))
            discount.setMaxRepetition(Integer.parseInt(maxRepetitionTextField.getText()));

        ((SaleProcessor) parentProcessor).discountCustomersMouseClicked(null);
    }

    private boolean isDateTimeEmpty(JFXDatePicker datePicker, JFXTimePicker timePicker) {
        return datePicker.getValue() == null || timePicker.getValue() == null;
    }

    private boolean isTextFieldEmpty(TextField textField) {
        return textField.getText() == null || textField.getText().isEmpty();
    }

    public ArrayList<String> getDiscountAddedUsers() {
        return adminControl.getDiscountsAddedUsers().get(discount);
    }

    public boolean isAccountAddedInDiscount(String userName) {
        return adminControl.isUserAddedInDiscount(discount, userName);
    }

    public void addUserToDiscount(String userName) {
        adminControl.addUserToDiscountAddedUsers(discount, userName);
    }

    public void removeUserFromDiscount(String userName) {
        adminControl.removeUserFromDiscountAddedUsers(discount, userName);
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;

        //Todo Jesus
//        setFields();
    }

    @Override
    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
        if (!myStage.getTitle().equals("Add New Off")) {
            myStage.setOnCloseRequest(event -> {
                parentProcessor.removeSubStage(myStage);
                adminControl.removeDiscountFromHashMap(discount);
            });
        } /*else {
            myStage.setOnCloseRequest(event -> {
                parentProcessor.removeSubStage(myStage);
            });
        }*/

    }

    public void updateParentTable() {
        TableViewProcessor<Discount> parentTableViewProcessor = (TableViewProcessor<Discount>) parentProcessor;
        parentTableViewProcessor.removeSubStage(myStage);
        parentTableViewProcessor.updateTable();
        parentTableViewProcessor.updateSelectedItem();
    }

    public Discount getDiscount() {
        return discount;
    }

    //OffMethods
    public void offInfoPaneMouseClick(MouseEvent mouseEvent) {
        try {
            if(offMainPane.getCenter() == null || !offMainPane.getCenter().getId().equals("offInfoMainPane")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("OffMenuInfo.fxml"));
                offMainPane.setCenter(loader.load());
                offProductsPane.setStyle("");
                offInfoPane.setStyle("-fx-background-color: #3498DB;   -fx-background-radius: 0 10 10 0;");
                SaleProcessor saleProcessor = loader.getController();
                saleProcessor.parentProcessor = this;
                saleProcessor.setOffFields();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void offProductsPaneMouseClicked(MouseEvent mouseEvent) {
        try {
            if(offMainPane.getCenter() == null || !offMainPane.getCenter().getId().equals("userProductsScrollPane")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductsMenuOff.fxml"));
                Parent root = loader.load();
                offInfoPane.setStyle("");
                offProductsPane.setStyle("-fx-background-color: #3498DB;   -fx-background-radius: 0 10 10 0;");
                ProductsProcessor processor = loader.getController();
                processor.setParentProcessor(this);
                if (Control.getType() != null && (Control.getType().equals("Vendor")) && !isEditing && off.getStatus() != 2)
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_ADD_OFF_PRODUCTS);
                else if(Control.getType() != null && (Control.getType().equals("Vendor")) && isEditing && off.getStatus() != 2) {
                    processor.setSelectedOff(off);
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_OFF_PRODUCTS);
                } else if(Control.getType() != null && (Control.getType().equals("Vendor")) && isEditing && off.getStatus() == 2) {
                    processor.setSelectedOff(off);
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_OFF_PRODUCTS_UNAPPROVED);
                }
                else if (Control.getType() != null && (Control.getType().equals("Admin"))) {
                    processor.setSelectedOff(off);
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.ADMIN_OFF_PRODUCTS);
                }
                offMainPane.setCenter(root);

                //TODO
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AddOffMouseClicked(MouseEvent mouseEvent) {
        if(off.getStartDate() == null) {
            off.setStartDate(new Date(System.currentTimeMillis()));
        }
        File imageFile = (isDefaultPicture ? null : offImageFile);
        if(!isEditing) {
            Notification resultNotification = VendorControl.getController().addOff(off, imageFile);
            if (resultNotification == Notification.ADD_OFF) {
                //System.out.println(myStage);
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
                closeSubStage(myStage, parentProcessor);
            }
            resultNotification.getAlert().show();
        } else {
            System.out.println("Yeah You're Editing");
            Notification resultNotification = VendorControl.getController().editOff(off, imageFile);
            if (resultNotification == Notification.EDIT_OFF) {
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
                closeSubStage(myStage, parentProcessor);
            }
            resultNotification.getAlert().show();
        }
    }

    public void saveOffChange(MouseEvent mouseEvent) {
        Off off = ((SaleProcessor)parentProcessor).off;
        if(!isDateTimeEmpty(offStartDatePicker, offStartTimePicker)) {
            LocalDateTime localStartDateTime = LocalDateTime.of(offStartDatePicker.getValue(), offStartTimePicker.getValue());
            Date startDate = new Date(Timestamp.valueOf(localStartDateTime).getTime());
            off.setStartDate(startDate);
        }
        if(!isDateTimeEmpty(offFinishDatePicker, offFinishTimePicker)) {
            LocalDateTime localFinishDateTime = LocalDateTime.of(offFinishDatePicker.getValue(), offFinishTimePicker.getValue());
            Date finishDate = new Date(Timestamp.valueOf(localFinishDateTime).getTime());
            off.setFinishDate(finishDate);
        }
        if(!isTextFieldEmpty(offNameField))
            off.setOffName(offNameField.getText());
        if(!isTextFieldEmpty(offPercentField))
            off.setOffPercent(Double.parseDouble(offPercentField.getText()));
        ((SaleProcessor) parentProcessor).offProductsPaneMouseClicked(null);
    }

    public void setOff(Off off) {
        this.off = off;
    }

    public void addProductToOff(String id) {
        if(!isProductInOff(id)) {
            this.off.addProductToOff(id);
        }
    }

    public void deleteProductFromOff(String id) {
        if(isProductInOff(id)) {
            this.off.removeProductFromOff(id);
        }
    }

    public boolean isProductInOff(String id) {
        return this.off.getProductIDs().contains(id);
    }

    public void getImageFromVendor(MouseEvent mouseEvent) {
        File pictureFile = getImageChooser().showOpenDialog(null);
        if(pictureFile != null) {
            ((SaleProcessor)parentProcessor).offImageFile = pictureFile;
            ((SaleProcessor)parentProcessor).isDefaultPicture = false;
            updateImageRectangle();
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

    public void deleteImage(MouseEvent mouseEvent) {
        ((SaleProcessor)parentProcessor).offImageFile = productControl.getOffImageFileByID("1");
        ((SaleProcessor)parentProcessor).isDefaultPicture = true;
        updateImageRectangle();
    }

    public void setDefaultPicture(boolean defaultPicture) {
        isDefaultPicture = defaultPicture;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public void setPreviousOff(boolean previousOff) {
        isPreviousOff = previousOff;
    }
}
