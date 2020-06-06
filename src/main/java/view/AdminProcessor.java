package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import controller.Control;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import model.existence.Account;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminProcessor implements Initializable {

    public JFXButton dashboardButton;
    public BorderPane mainPane;
    public JFXButton accountsButton;
    public JFXButton productsButton;
    public JFXButton offsButton;
    public Label usernameLabel;
    public JFXButton mainMenuButton;
    private ArrayList<JFXButton> buttons = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.toString().contains("AdminMenu")) {
            Account account = new Account("Ashkan", "a1234567");
            account.setType("Admin");
            account.setFirstName("Ashkan");
            account.setLastName("Khademian");
            IOControl ioControl = IOControl.getController();
            ioControl.register(account);
            ioControl.login(account);
            initButtons();
            selectThisButton(dashboardButton);
            initLabelsForUsername();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
            Parent subRoot = null;
            try {
                subRoot = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loader.setController(this);
            mainPane.setCenter(subRoot);
        }

    }

    private void initLabelsForUsername() {
        usernameLabel.setText(Control.getUsername());
    }

    private void initButtons() {
        buttons.add(dashboardButton);
        buttons.add(accountsButton);
        buttons.add(productsButton);
        buttons.add(offsButton);
        buttons.add(mainMenuButton);
    }

    private void selectThisButton(JFXButton selectedButton) {
        selectedButton.setRipplerFill(Color.valueOf("#80cbc4"));
        selectedButton.setStyle("-fx-background-color: #80cbc4;");
        for (JFXButton button : buttons) {
            if(button != selectedButton) {
                button.setRipplerFill(Color.WHITE);
                button.setStyle("-fx-background-color: #ffffff;");
            }
        }
    }

    public void personalInfo(MouseEvent mouseEvent) {

    }

    public void marketStats(MouseEvent mouseEvent) {
    }

    public void setOptions(ActionEvent actionEvent) {
        JFXButton selectedButton = (JFXButton) actionEvent.getSource();
        selectThisButton(selectedButton);
        try {
            if (selectedButton.equals(dashboardButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
                Parent subRoot = loader.load();
                loader.setController(this);
                mainPane.setCenter(subRoot);
            } else if (selectedButton.equals(accountsButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminAccounts.fxml"));
                Parent subRoot = loader.load();
                loader.setController(this);
                mainPane.setCenter(subRoot);
            } else if(selectedButton.equals(productsButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminProducts.fxml"));
                Parent subRoot = loader.load();
                loader.setController(this);
                mainPane.setCenter(subRoot);
            } else if(selectedButton.equals(offsButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminOffs.fxml"));
                Parent subRoot = loader.load();
                loader.setController(this);
                mainPane.setCenter(subRoot);
            } else if(selectedButton.equals(mainMenuButton)) {
                System.out.println("Back To Main Menu");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
