package client.view;

import client.api.Command;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
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
import server.model.existence.Account;
import server.model.existence.Discount;
import server.model.existence.Off;
import notification.Notification;
import server.server.Response;

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
    public Pane addDiscountButton;
    public Pane infoPane;

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
        setStringFields(discountCodeTextField, 16);
        setDoubleFields(discountPercentTextField, 100.000001);
        setIntegerFields(maxRepetitionTextField, Integer.MAX_VALUE);
        setDoubleFields(maxDiscountTextField, Double.MAX_VALUE);

        startTimePicker.set24HourView(true);
        finishTimePicker.set24HourView(true);
    }

    private void setDiscountFields() {
        Discount mainDiscount = ((SaleProcessor) parentProcessor).discount;

        if(mainDiscount == null) {
            System.err.println("Shit Error In Sale Processor");
        } else {
            discountCodeTextField.setText(mainDiscount.getCode());

            if(mainDiscount.getDiscountPercent() != 0)
                discountPercentTextField.setText(getSmoothDoubleFormat(mainDiscount.getDiscountPercent()));

            if(mainDiscount.getMaxRepetition() != 0)
                maxRepetitionTextField.setText(Integer.toString(mainDiscount.getMaxRepetition()));

            if(mainDiscount.getMaxDiscount() != 0)
                maxDiscountTextField.setText(getSmoothDoubleFormat(mainDiscount.getMaxDiscount()));

            if(mainDiscount.getStartDate() != null) {
                setDateFieldsFromDate(startDatePicker, startTimePicker, mainDiscount.getStartDate());
                startDatePicker.setDisable(true);
                startTimePicker.setDisable(true);
            }

            if(mainDiscount.getFinishDate() != null)
                setDateFieldsFromDate(finishDatePicker, finishTimePicker, mainDiscount.getFinishDate());

            if(super.getType().equals("Admin")) {
                if(mainDiscount.getID() != null) {
                    startDatePicker.setEditable(false);
                    startTimePicker.setEditable(false);
                }
            } else  {
                //discountCodeLabel.setDisable(true);
                discountCodeTextField.setEditable(false);
//                discountPercentLabel.setDisable(true);
                discountPercentTextField.setEditable(false);
//                maxRepetitionLabel.setDisable(true);
                maxRepetitionTextField.setEditable(false);
//                maxDiscountLabel.setDisable(true);
                maxDiscountTextField.setEditable(false);

                startDatePicker.setDisable(true);
                startDatePicker.setOpacity(0.99);
                startTimePicker.setDisable(true);
                startTimePicker.setOpacity(0.99);
                finishDatePicker.setDisable(true);
                finishDatePicker.setOpacity(0.99);
                finishTimePicker.setDisable(true);
                finishTimePicker.setOpacity(0.99);

                Pane pane = (Pane)(((SaleProcessor) parentProcessor)).discountMainPane.getCenter();
                infoPane.getChildren().remove(saveChangeButton);
                ((SaleProcessor) parentProcessor).optionsVbox.getChildren().
                        removeAll(((SaleProcessor) parentProcessor).discountCustomersPane, ((SaleProcessor) parentProcessor).addDiscountButton);
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
            if(mainOff.getOffName() != null && mainOff.getOffName().length() != 0)
                offNameField.setText(mainOff.getOffName());
            if(mainOff.getOffPercent() != 0)
                offPercentField.setText("" + mainOff.getOffPercent());
            if(mainOff.getStartDate() != null)
                setDateFieldsFromDate(offStartDatePicker, offStartTimePicker, mainOff.getStartDate());
            if(mainOff.getFinishDate() != null)
                setDateFieldsFromDate(offFinishDatePicker, offFinishTimePicker, mainOff.getFinishDate());
            if(getType().equals("Admin")) {
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
        offInfoMainPane.getChildren().removeAll(saveOffChangeButton, deleteImageButton);
        offImageRectangle.setMouseTransparent(true);
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
            if(((SaleProcessor)parentProcessor).isDefaultPicture) {
                offImageRectangle.setStrokeWidth(0);
                deleteImageButton.setDisable(true);
            } else {
                offImageRectangle.setStrokeWidth(2);
                deleteImageButton.setDisable(false);
            }
            fileInputStream.close();
        } catch (IOException e) {
            //:)
        }
    }

    public void getOffImageFile() {
        boolean determiner = isOffEditing(off.getOffID()) && !isPreviousOff;
        offImageFile = client.getFile(getOffImageCommand(off.getOffID(), determiner));
        isDefaultPicture = !doesOffHaveImage(off.getOffID(), determiner);
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
                saleProcessor.setDiscountFields();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AddDiscountMouseClicked(MouseEvent mouseEvent) {
        //Todo Setting Notifications
        Notification notification = addDiscount(discount);

        Optional<ButtonType> optionalButtonType = notification.getAlert().showAndWait();

        if(optionalButtonType.get() == ButtonType.OK) {
            if(notification == Notification.ADD_DISCOUNT || notification == Notification.EDIT_DISCOUNT) {
                //Todo Check
                removeDiscountFromProperty(discount.getID());
                updateParentTable();
                this.myStage.close();
            } else
                discountInfoMouseClicked(null);
        }
    }

    private void removeDiscountFromProperty(String discountID) {
        discountID = discountID == null ? "" : discountID;
        Command<String> command = new Command<>("remove discount from property", Command.HandleType.GENERAL, discountID);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private Notification addDiscount(Discount discount) {
        discount.setID(discount.getID() == null ? "" : discount.getID());
        Command<Discount> command = new Command<>("add discount", Command.HandleType.GENERAL, discount);
        return client.postAndGet(command, Response.class, (Class<Object>)Object.class).getMessage();
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

    public ArrayList<String> getDiscountAddedUsernames() {
        Command<String>command = new Command("get discount added users", Command.HandleType.GENERAL, (discount.getID() == null ? "" : discount.getID()));
        Response<Account> response = client.postAndGet(command, Response.class, (Class<Account>)Account.class);
        ArrayList<String> usernames = new ArrayList<>();
        for (Account account : response.getData()) {
            usernames.add(account.getUsername());
        }
        return usernames;
    }

    public void addUserToDiscount(String username) {
        Command<String> command = new Command<>("add customer to discount", Command.HandleType.GENERAL, (discount.getID() == null ? "" : discount.getID()), username);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    public void removeUserFromDiscount(String username) {
        Command<String> command = new Command<>("delete customer from discount", Command.HandleType.GENERAL, (discount.getID() == null ? "" : discount.getID()), username);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
        Command<Discount> command = new Command<>("add discount to property", Command.HandleType.GENERAL, discount);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    @Override
    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
        if (!myStage.getTitle().equals("Add New Off")) {
            myStage.setOnCloseRequest(event -> {
                parentProcessor.removeSubStage(myStage);
                removeDiscountFromProperty(discount.getID());
            });
        }

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
            if(offMainPane.getCenter() == null || !offMainPane.getCenter().getId().equals("offInfoMainPane")) {
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
                String type = getType();
                if (type != null && (type.equals("Vendor")) && !isEditing && off.getStatus() != 2)
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_ADD_OFF_PRODUCTS);
                else if(type != null && (type.equals("Vendor")) && isEditing && off.getStatus() != 2) {
                    processor.setSelectedOff(off);
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_OFF_PRODUCTS);
                } else if(type != null && (type.equals("Vendor")) && isEditing && off.getStatus() == 2) {
                    processor.setSelectedOff(off);
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.VENDOR_OFF_PRODUCTS_UNAPPROVED);
                }
                else if (type != null && (type.equals("Admin"))) {
                    processor.setSelectedOff(off);
                    processor.initProcessor(ProductsProcessor.ProductsMenuType.ADMIN_OFF_PRODUCTS);
                }
                offMainPane.setCenter(root);
                //TODO
            }
        } catch (IOException e) {
            //:)
        }
    }

    public void AddOffMouseClicked(MouseEvent mouseEvent) {
        if(off.getStartDate() == null) {
            off.setStartDate(new Date(System.currentTimeMillis()));
        }
        File imageFile = (isDefaultPicture ? null : offImageFile);
        if(!isEditing) {
            Command<Off> command = new Command<>("add off", Command.HandleType.SALE, off);
            Response<String> response = client.postAndGet(command, Response.class, (Class<String>)String.class);
            Notification resultNotification = response.getMessage();
            off.setOffID(response.getDatum());
            Command<String> extensionCommand = new Command<>("send off image", Command.HandleType.PICTURE_SEND, off.getOffID(), client.file2Extension.apply(imageFile));
            client.sendImage(extensionCommand, imageFile);
            if (resultNotification == Notification.ADD_OFF && this.parentProcessor instanceof TableViewProcessor) {
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
                closeSubStage(myStage, parentProcessor);
            }
            resultNotification.getAlert().show();
        } else {
            Command<Off> command = new Command<>("edit off", Command.HandleType.SALE, off);
            Response response = client.postAndGet(command, Response.class, (Class<Object>)Object.class);
            Notification resultNotification = response.getMessage();
            Command<String> extensionCommand = new Command<>("send editing off image", Command.HandleType.PICTURE_SEND, off.getOffID(), client.file2Extension.apply(imageFile));
            client.sendImage(extensionCommand, imageFile);
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
        Command<String> command = new Command<>("get off image", Command.HandleType.PICTURE_GET, "1");
        ((SaleProcessor)parentProcessor).offImageFile = client.getFile(command);
        ((SaleProcessor)parentProcessor).isDefaultPicture = true;
        updateImageRectangle();
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public void setPreviousOff(boolean previousOff) {
        isPreviousOff = previousOff;
    }
}
