package view;

import controller.Control;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import model.existence.Account;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileProcessor implements Initializable {
    private static Account account;

    public Pane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String accountType = Control.getType();

        if(accountType.equals("Admin")) {

        } else if(accountType.equals("Vendor")) {

        } else {

        }
    }
}
