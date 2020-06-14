package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.account.AdminControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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

    public JFXComboBox<Integer> startDateYearComboBox;
    public JFXComboBox<Integer> startDateDayComboBox;
    public JFXComboBox<Integer> startDateMonthComboBox;

    public TextField startDateHourTextField;
    public TextField startDateSecondTextField;
    public TextField startDateMinuteTextField;

    public JFXComboBox<Integer> finishDateYearComboBox;
    public JFXComboBox<Integer> finishDateDayComboBox;
    public JFXComboBox<Integer> finishDateMonthComboBox;

    public TextField finishDateHourTextField;
    public TextField finishDateSecondTextField;
    public TextField finishDateMinuteTextField;

    public BorderPane discountMainPane;

    private Discount discount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String locationFile = location.getFile();

        if(locationFile.contains("DiscountMenuInfo")) {
            setTextFieldsSpecifications();
            setComboBoxSpecifications();
            setFields();
        } else if(locationFile.contains("DiscountMenu")) {
            discount = new Discount();
            adminControl.addDiscountToHashMap(discount);
            profileInfoMouseClicked(null);
        }
    }


    private void setTextFieldsSpecifications() {
        setDoubleFields(discountPercentTextField, 100.000001);
        setIntegerFields(maxRepetitionTextField, Integer.MAX_VALUE);
        setDoubleFields(maxDiscountTextField, Double.MAX_VALUE);

        setIntegerFields(startDateHourTextField, 24);
        setIntegerFields(startDateMinuteTextField, 60);
        setIntegerFields(startDateSecondTextField, 60);

        setIntegerFields(finishDateHourTextField, 24);
        setIntegerFields(finishDateMinuteTextField, 60);
        setIntegerFields(finishDateSecondTextField, 60);
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


    private void setComboBoxSpecifications() {
        java.util.Date date = new java.util.Date();
        int currentYear = date.getYear() + 1900;

        setArrayListToComboBox(currentYear, currentYear + 10, startDateYearComboBox);
        setArrayListToComboBox(1, 13, startDateMonthComboBox);
        setArrayListToComboBox(1, 31, startDateDayComboBox);

        setArrayListToComboBox(2020, 2030, finishDateYearComboBox);
        setArrayListToComboBox(1, 13, finishDateMonthComboBox);
        setArrayListToComboBox(1, 31, finishDateDayComboBox);

        setStartDateComboBoxAutomatic(currentYear, date.getMonth() + 1, date.getDate(),
                startDateYearComboBox, startDateMonthComboBox, startDateDayComboBox);

        setStartDateComboBoxAutomatic(currentYear, date.getMonth() + 1, date.getDate(),
                finishDateYearComboBox, finishDateMonthComboBox, finishDateDayComboBox);
    }

    private void setStartDateComboBoxAutomatic(Integer currentYear, Integer currentMonth, Integer currentDay,
                    JFXComboBox<Integer> DateYearComboBox, JFXComboBox<Integer> DateMonthComboBox,
                                               JFXComboBox<Integer> DateDayComboBox) {
        DateYearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Integer> observableList = DateMonthComboBox.getItems();
            if(newValue != null && newValue.equals(currentYear)) {
                for(int i = 1; i < currentMonth; i++)
                    observableList.remove((Integer) i);

            } else if(!observableList.contains(1)) {
                Integer month = DateMonthComboBox.getValue();
                DateMonthComboBox.getSelectionModel().clearSelection(month);
                observableList.removeAll(observableList);

                for(int i = 1; i < 13; i++)
                    observableList.add(i);

                DateMonthComboBox.getSelectionModel().select(month);
            }
        });
        DateMonthComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Integer> observableList = DateDayComboBox.getItems();
            if(newValue != null && newValue.equals(currentMonth) &&
                (DateYearComboBox.getValue() != null && DateYearComboBox.getValue().equals(currentYear))) {
                for(int i = 1; i < currentDay; i++)
                    observableList.remove((Integer) i);

            } else if(!observableList.contains(1)) {
                Integer day = DateDayComboBox.getValue();
                DateDayComboBox.getSelectionModel().clearSelection();
                observableList.removeAll(observableList);

                for(int i = 1; i < 31; i++)
                    observableList.add(i);

                DateDayComboBox.getSelectionModel().select(day);
            }
        });
    }

    private void setArrayListToComboBox(int min, int max, ComboBox<Integer> comboBox) {
        ArrayList<Integer> comboBoxOptions = new ArrayList<>();

        for(int i = min; i < max; i++)
            comboBoxOptions.add(i);

        for (Integer comboBoxOption : comboBoxOptions)
            comboBox.getItems().add(comboBoxOption);
    }


    private void setFields() {
        if(discount != null) {
            discountCodeTextField.setText(((DiscountProcessor) parentProcessor).discount.getCode());
            discountPercentTextField.setText(Double.toString(((DiscountProcessor) parentProcessor).discount.getDiscountPercent()));
            maxRepetitionTextField.setText(Double.toString(((DiscountProcessor) parentProcessor).discount.getMaxRepetition()));
            maxDiscountTextField.setText(Double.toString(((DiscountProcessor) parentProcessor).discount.getMaxDiscount()));

            setDateComboBox(startDateYearComboBox, startDateMonthComboBox, startDateDayComboBox,
                    startDateHourTextField, startDateMinuteTextField, startDateSecondTextField,
                    ((DiscountProcessor) parentProcessor).discount.getStartDate());

            setDateComboBox(finishDateYearComboBox, finishDateMonthComboBox, finishDateDayComboBox,
                    finishDateHourTextField, finishDateMinuteTextField, finishDateSecondTextField,
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
                startDateYearComboBox.setDisable(true);
                startDateMonthComboBox.setDisable(true);
                startDateDayComboBox.setDisable(true);
                startDateHourTextField.setDisable(true);
                startDateMinuteTextField.setDisable(true);
                startDateSecondTextField.setDisable(true);

                finishDateLabel.setDisable(true);
                finishDateYearComboBox.setDisable(true);
                finishDateMonthComboBox.setDisable(true);
                finishDateDayComboBox.setDisable(true);
                finishDateHourTextField.setDisable(true);
                finishDateMinuteTextField.setDisable(true);
                finishDateSecondTextField.setDisable(true);
            }
        }
    }

    private void setDateComboBox(JFXComboBox<Integer> startDateYearComboBox, JFXComboBox<Integer> startDateMonthComboBox,
                                 JFXComboBox<Integer> startDateDayComboBox, TextField startDateHourTextField,
                                 TextField startDateMinuteTextField, TextField startDateSecondTextField, Date startDate) {

        startDateYearComboBox.getSelectionModel().select(startDate.getYear());
        startDateMonthComboBox.getSelectionModel().select(startDate.getMonth());
        startDateDayComboBox.getSelectionModel().select(startDate.getDay());

        startDateHourTextField.setText(Integer.toString(startDate.getHours()));
        startDateMinuteTextField.setText(Integer.toString(startDate.getMinutes()));
        startDateSecondTextField.setText(Integer.toString(startDate.getSeconds()));

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

        if(startDateYearComboBox.getValue() != null && startDateMonthComboBox.getValue() != null &&
                startDateDayComboBox.getValue() != null &&
                (startDateHourTextField.getText() != null || !startDateHourTextField.getText().isEmpty()) &&
                (startDateMinuteTextField.getText() != null || !startDateMinuteTextField.getText().isEmpty()) &&
                (startDateSecondTextField.getText() != null || !startDateSecondTextField.getText().isEmpty())
        ) {
            java.util.Date startDate = new java.util.Date(startDateYearComboBox.getValue() - 1900,
                    startDateMonthComboBox.getValue() - 1, startDateDayComboBox.getValue(),
                    Integer.parseInt(startDateHourTextField.getText()), Integer.parseInt(startDateMinuteTextField.getText()),
                    Integer.parseInt(startDateSecondTextField.getText()));
            //System.out.println(startDate);
            discount.setStartDate(new Date(startDate.getTime()));
        }

        if(finishDateYearComboBox.getValue() != null && finishDateMonthComboBox.getValue() != null &&
                finishDateDayComboBox.getValue() != null &&
                (finishDateHourTextField.getText() != null || !finishDateHourTextField.getText().isEmpty()) &&
                (finishDateMinuteTextField.getText() != null || !finishDateMinuteTextField.getText().isEmpty()) &&
                (finishDateSecondTextField.getText() != null || !finishDateSecondTextField.getText().isEmpty())
        ) {
            java.util.Date finishDate = new java.util.Date(finishDateYearComboBox.getValue() - 1900,
                    finishDateMonthComboBox.getValue() - 1, finishDateDayComboBox.getValue(),
                    Integer.parseInt(finishDateHourTextField.getText()), Integer.parseInt(finishDateMinuteTextField.getText()),
                    Integer.parseInt(finishDateSecondTextField.getText()));
            discount.setFinishDate(new Date(finishDate.getTime()));
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
