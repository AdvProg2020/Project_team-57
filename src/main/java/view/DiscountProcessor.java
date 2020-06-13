package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.account.AdminControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.existence.Discount;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DiscountProcessor implements Initializable, TableHold, changeTextFieldFeatures {
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
    public Pane discountMainPane;

    private Discount discount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String locationFile = location.getFile();

        if(locationFile.contains("DiscountMenuInfo")) {
            setTextFieldsSpecifications();
            setComboBoxSpecifications();
            setFields();
        }
    }

    private void setFields() {
        if(discount != null) {
            discountCodeTextField.setText(discount.getCode());
            discountPercentTextField.setText(Double.toString(discount.getDiscountPercent()));
            maxRepetitionTextField.setText(Double.toString(discount.getMaxRepetition()));
            maxDiscountTextField.setText(Double.toString(discount.getMaxDiscount()));

            setDateComboBox(startDateYearComboBox, startDateMonthComboBox, startDateDayComboBox,
                    startDateHourTextField, startDateMinuteTextField, startDateSecondTextField, discount.getStartDate());
            setDateComboBox(finishDateYearComboBox, finishDateMonthComboBox, finishDateDayComboBox,
                    finishDateHourTextField, finishDateMinuteTextField, finishDateSecondTextField, discount.getFinishDate());

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
        setPriceFields(maxRepetitionTextField);
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
        //Todo
    }

    public void profileInfoMouseClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DiscountMenuInfo.fxml"));

        if(!discountMainPane.getChildren().isEmpty())
            discountMainPane.getChildren().remove(0);

        discountMainPane.getChildren().add(loader.load());
    }

    public HashMap<Discount, ArrayList<String>> getDiscountAddedUsers() {
        return adminControl.getDiscountsAddedUsers();
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
}
