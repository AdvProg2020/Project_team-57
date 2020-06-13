package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.account.AdminControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import model.existence.Discount;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class DiscountProcessor /*extends Processor*/ implements TableHold, Initializable, changeTextFieldFeatures {
    private static AdminControl adminControl = AdminControl.getController();

    public Label discountCodeLabel;
    public Label maxDiscountLabel;
    public Label maxRepetitionLabel;
    public Label discountPercentLabel;
    public Label finishDateLabel;
    public Label startDateLabel;
    public JFXTextField maxDiscountTextField;
    public JFXTextField maxRepetitionTextField;
    public JFXTextField discountPercentTextField;
    public JFXComboBox<Integer> startDateYearComboBox;
    public JFXComboBox<Integer> startDateDayComboBox;
    public JFXComboBox<Integer> startDateMonthComboBox;
    public TextField startDateHourTextField;
    public TextField startDateSecondTextField;
    public TextField startDateMinuteTextField;
    public JFXTextField discountCodeTextField;
    public JFXComboBox<Integer> finishDateYearComboBox;
    public JFXComboBox<Integer> finishDateDayComboBox;
    public JFXComboBox<Integer> finishDateMonthComboBox;
    public TextField finishDateHourTextField;
    public TextField finishDateSecondTextField;
    public TextField finishDateMinuteTextField;
    public BorderPane discountMainPane;

    private Discount discount;

    public DiscountProcessor parentProcessor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String locationFile = location.getFile();

        if(locationFile.contains("DiscountMenuInfo")) {
            setTextFieldsSpecifications();
            setComboBoxSpecifications();
            //Todo SetFields From Ashkan
        } else if(locationFile.contains("DiscountMenu")) {
            discount = new Discount();
            adminControl.addDiscountToHashMap(discount);
            profileInfoMouseClicked(null);
        }
    }

    private void setFields() {
        if(discount != null) {
            discountCodeTextField.setText(parentProcessor.discount.getCode());
            discountPercentTextField.setText(Double.toString(parentProcessor.discount.getDiscountPercent()));
            maxRepetitionTextField.setText(Double.toString(parentProcessor.discount.getMaxRepetition()));
            maxDiscountTextField.setText(Double.toString(parentProcessor.discount.getMaxDiscount()));

            setDateComboBox(startDateYearComboBox, startDateMonthComboBox, startDateDayComboBox,
                    startDateHourTextField, startDateMinuteTextField, startDateSecondTextField, parentProcessor.discount.getStartDate());
            setDateComboBox(finishDateYearComboBox, finishDateMonthComboBox, finishDateDayComboBox,
                    finishDateHourTextField, finishDateMinuteTextField, finishDateSecondTextField, parentProcessor.discount.getFinishDate());

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

    private void setComboBoxSpecifications() {
        setArrayListToComboBox(2020, 2030, startDateYearComboBox);
        setArrayListToComboBox(1, 13, startDateMonthComboBox);
        setArrayListToComboBox(1, 31, startDateDayComboBox);

        setArrayListToComboBox(2020, 2030, finishDateYearComboBox);
        setArrayListToComboBox(1, 13, finishDateMonthComboBox);
        setArrayListToComboBox(1, 31, finishDateDayComboBox);

        //Todo Checking Current Date
    }

    private void setArrayListToComboBox(int min, int max, ComboBox<Integer> comboBox) {
        ArrayList<Integer> comboBoxOptions = new ArrayList<>();
        for(int i = min; i < max; i++)
            comboBoxOptions.add(i);
        setSpecificComboBoxOptions(comboBoxOptions, comboBox);
    }

    private void setSpecificComboBoxOptions(ArrayList<Integer> comboBoxOptions, ComboBox<Integer> comboBox) {
        for (Integer comboBoxOption : comboBoxOptions) {
            comboBox.getItems().add(comboBoxOption);
        }
    }

    private void setTextFieldsSpecifications() {
        setPriceFields(discountPercentTextField, 100.000001);
        setTimeFields(maxRepetitionTextField, Integer.MAX_VALUE);
        setPriceFields(maxDiscountTextField);

        setTimeFields(startDateHourTextField, 24);
        setTimeFields(startDateMinuteTextField, 60);
        setTimeFields(startDateSecondTextField, 60);

        setTimeFields(finishDateHourTextField, 24);
        setTimeFields(finishDateMinuteTextField, 60);
        setTimeFields(finishDateSecondTextField, 60);
    }

    private void setPriceFields(TextField priceTextField) {
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

            }
        });
    }

    private void setPriceFields(TextField priceTextField, double maxValue) {
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

    private void setTimeFields(TextField priceTextField, double maxValue) {
        priceTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                if (!newValue.matches("\\d+")) {
                    priceTextField.setText(newValue.replaceAll("[^\\d]", ""));
                } else if(newValue.matches("\\d+") && (Integer.parseInt(newValue) < 0 ||Integer.parseInt(newValue) >= maxValue)) {
                    //Todo checking
                    priceTextField.setText(oldValue);
                }

            }
        });
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DiscountMenuInfo.fxml"));
        try {
            discountMainPane.setCenter(loader.load());
            DiscountProcessor discountProcessor = loader.getController();
            discountProcessor.parentProcessor = this;
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void AddDiscountMouseClicked(MouseEvent mouseEvent) {
        adminControl.addAddedDiscount(discount);
    }

    public void saveChangesMouseClicked(MouseEvent mouseEvent) {
        Discount discount = parentProcessor.discount;

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

        if(!isItEmpty(discountCodeTextField))
            discount.setCode(discountCodeTextField.getText());
        if(!isItEmpty(discountCodeTextField))
            discount.setDiscountPercent(Double.parseDouble(discountPercentTextField.getText()));
        if(!isItEmpty(discountCodeTextField))
            discount.setMaxDiscount(Double.parseDouble(maxDiscountTextField.getText()));
        if(!isItEmpty(discountCodeTextField))
            discount.setMaxRepetition(Integer.parseInt(maxRepetitionTextField.getText()));

        parentProcessor.discountCustomersMouseClicked(null);
    }

    private boolean isItEmpty(TextField textField) {
        return textField.getText() == null || textField.getText().isEmpty();
    }
}
