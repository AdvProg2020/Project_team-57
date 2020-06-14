package view;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import controller.Control;
import controller.account.AdminControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.existence.Discount;
import notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class DiscountProcessor extends Processor implements Initializable, changeTextFieldFeatures {
    private static AdminControl adminControl = AdminControl.getController();

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
    /*public TextField startDateHourTextField;
    public TextField startDateSecondTextField;
    public TextField startDateMinuteTextField;*/

    public JFXDatePicker finishDatePicker;
    public JFXTimePicker finishTimePicker;
    /*public TextField finishDateHourTextField;
    public TextField finishDateSecondTextField;
    public TextField finishDateMinuteTextField;*/

    public BorderPane discountMainPane;

    private Discount discount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String locationFile = location.getFile();

        if(locationFile.contains("DiscountMenuInfo")) {
            setFieldsSpecifications();
            setFields();
        } else if(locationFile.contains("DiscountMenu")) {
            discount = new Discount();
            adminControl.addDiscountToHashMap(discount);
            profileInfoMouseClicked(null);
        }
    }


    private void setFieldsSpecifications() {
        setDoubleFields(discountPercentTextField, 100.000001);
        setIntegerFields(maxRepetitionTextField, Integer.MAX_VALUE);
        setDoubleFields(maxDiscountTextField, Double.MAX_VALUE);

        startTimePicker.set24HourView(true);
        finishTimePicker.set24HourView(true);

        /*setIntegerFields(startDateHourTextField, 24);
        setIntegerFields(startDateMinuteTextField, 60);
        setIntegerFields(startDateSecondTextField, 60);

        setIntegerFields(finishDateHourTextField, 24);
        setIntegerFields(finishDateMinuteTextField, 60);
        setIntegerFields(finishDateSecondTextField, 60);*/
    }

    private void setDoubleFields(TextField priceTextField, double maxValue) {
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
                } else if(newValue.matches("\\d+(.(\\d)+)?") && Double.parseDouble(newValue) >= maxValue) {
                    //Todo checking
                    priceTextField.setText(oldValue);
                }

            }
        });
    }

    private void setIntegerFields(TextField priceTextField, Integer maxValue) {
        priceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                int newValueLength = newValue.length(), maxValueLength = Integer.toString(maxValue).length();

                if (!newValue.matches("\\d+")) {
                    priceTextField.setText(newValue.replaceAll("[^\\d]", ""));
                } else if(newValue.matches("\\d+") && (newValueLength > maxValueLength ||
                        (newValueLength == maxValueLength && newValue.compareTo(Integer.toString(maxValue)) >= 0))) {
                    priceTextField.setText(oldValue);
                }
            }
        });
    }


    private void setFields() {
        if(discount != null) {
            discountCodeTextField.setText(((DiscountProcessor) parentProcessor).discount.getCode());
            discountPercentTextField.setText(Double.toString(((DiscountProcessor) parentProcessor).discount.getDiscountPercent()));
            maxRepetitionTextField.setText(Double.toString(((DiscountProcessor) parentProcessor).discount.getMaxRepetition()));
            maxDiscountTextField.setText(Double.toString(((DiscountProcessor) parentProcessor).discount.getMaxDiscount()));

            setDateFieldsFromDate(startDatePicker, startTimePicker,
                    ((DiscountProcessor) parentProcessor).discount.getStartDate());

            setDateFieldsFromDate(finishDatePicker, finishTimePicker,
                    ((DiscountProcessor) parentProcessor).discount.getFinishDate());

            if(!Control.getType().equals("Admin")) {
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
            }
        }
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
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.initProcessor(TableViewProcessor.TableViewType.DISCOUNT_CUSTOMERS);
            discountMainPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void profileInfoMouseClicked(MouseEvent mouseEvent) {
        try {
            if(discountMainPane.getCenter() == null || discountMainPane.getCenter().getId().equals("mainBorderPane")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DiscountMenuInfo.fxml"));
                discountMainPane.setCenter(loader.load());
                DiscountProcessor discountProcessor = loader.getController();
                discountProcessor.parentProcessor = this;
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
            if(notification.equals(Notification.ADD_DISCOUNT))
                this.myStage.close();
            else
                profileInfoMouseClicked(null);
        }
    }

    public void saveChangesMouseClicked(MouseEvent mouseEvent) {
        Discount discount = ((DiscountProcessor) parentProcessor).discount;

        if(isDateTimeEmpty(startDatePicker, startTimePicker)) {
            LocalDateTime localStartDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimePicker.getValue());
            Date startDate = new Date(Timestamp.valueOf(localStartDateTime).getTime());
            discount.setStartDate(startDate);
            //System.out.println(startDate);
        }

        if(isDateTimeEmpty(finishDatePicker, startTimePicker)) {
            LocalDateTime localFinishDateTime = LocalDateTime.of(finishDatePicker.getValue(), finishTimePicker.getValue());
            Date finishDate = new Date(Timestamp.valueOf(localFinishDateTime).getTime());
            discount.setFinishDate(finishDate);
            //System.out.println(finishDate);
        }

        if(!isTextFieldEmpty(discountCodeTextField))
            discount.setCode(discountCodeTextField.getText());
        if(!isTextFieldEmpty(discountCodeTextField))
            discount.setDiscountPercent(Double.parseDouble(discountPercentTextField.getText()));
        if(!isTextFieldEmpty(discountCodeTextField))
            discount.setMaxDiscount(Double.parseDouble(maxDiscountTextField.getText()));
        if(!isTextFieldEmpty(discountCodeTextField))
            discount.setMaxRepetition(Integer.parseInt(maxRepetitionTextField.getText()));

        ((DiscountProcessor) parentProcessor).discountCustomersMouseClicked(null);
    }

    private boolean isDateTimeEmpty(JFXDatePicker datePicker, JFXTimePicker timePicker) {
        return datePicker.getValue() != null && timePicker.getValue() != null;
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
    }

    @Override
    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
        myStage.setOnCloseRequest(event -> {
            System.out.println("Hello");
            parentProcessor.removeSubStage(myStage);
            adminControl.removeDiscountFromHashMap(discount);
        });
    }


}
