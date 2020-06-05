package view;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
    private ArrayList<JFXButton> buttons = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.toString().contains("AdminMenu")) {
            initButtons();
        }

    }

    private void initButtons() {
        buttons.add(dashboardButton);
        buttons.add(accountsButton);
        buttons.add(productsButton);
        buttons.add(offsButton);
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
        System.out.println("Fuck");
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

            } else if(selectedButton.equals(productsButton)) {

            } else if(selectedButton.equals(offsButton)) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
