package view;

import com.jfoenix.controls.JFXTextField;
import controller.Control;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import model.existence.Account;

import javax.swing.text.html.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileProcessor implements Initializable {
    private static Account account;

    public Pane mainPane;
    public JFXTextField firstName, lastName, email, brand, credit;
    public ImageView profileImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStyleSheets();
        setFields();
    }

    private void setFields() {
        firstName.setText(account.getFirstName());
        lastName.setText(account.getLastName());
        email.setText(account.getEmail());

        if(account.getType().equals("Vendor")) {
            brand.setText(account.getBrand());
        } else {
            //TODO
        }
    }

    public void setStyleSheets() {
        String accountType = Control.getType(), styleSheet;
        ObservableList<String> styleSheets = mainPane.getStylesheets();
        styleSheets.removeAll(styleSheets);

        if(accountType.equals("Admin")) {
            styleSheet = "CSS files\\AdminProfileMenu.css";
        } else if(accountType.equals("Vendor")) {
            styleSheet = "CSS files\\VendorProfileMenu.css";
        } else {
            styleSheet = "CSS files\\CustomerProfileMenu.css";
        }
        styleSheets.add(styleSheet);
    }
}
