package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.existence.Discount;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscountProcessor implements Initializable/*, TableHold*/{
    public Label discountCodeLabel;
    public Label maxDiscountLabel;
    public Label maxRepetitionLabel;
    public Label discountPercentLabel;
    public Label finishDateLabel;
    public Label startDateLabel;
    public JFXTextField maxDiscountTextField;
    public JFXTextField maxRepetitionTextField;
    public JFXTextField discountPercentTextField;
    public JFXComboBox startDateYearComboBox;
    public JFXComboBox startDateDayComboBox;
    public JFXComboBox startDateMonthComboBox;
    public TextField startDateHourTextField;
    public TextField startDateSecondTextField;
    public TextField startDateMonthTextField;
    public JFXTextField discountCodeTextField;
    public JFXComboBox finishDateYearComboBox;
    public JFXComboBox finishDateDayComboBox;
    public JFXComboBox finishDateMonthComboBox;
    public TextField finishDateHourTextField;
    public TextField finishDateSecondTextField;
    public TextField finishDateMinuteTextField;

    private Discount discount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switch (Control.getType()) {
            case "Admin" :

                break;
            case "Vendor" :
            case "Customer" :

                break;
        }
    }


}
