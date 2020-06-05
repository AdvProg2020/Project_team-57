package view;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpProcessor implements Initializable {

    public Button signUp;
    public JFXTextField username;
    public JFXTextField password;
    public JFXTextField name;
    public JFXTextField lastName;
    public ImageView back;
    private final IOControl ioControl = IOControl.getController();
    public ImageView backImage;
    public JFXComboBox<String> accountTypeComboBox;
    private Stage myStage;

    public void register(ActionEvent event) {
        if (!isTextFieldEmpty()) {
            final Account account = new Account(username.getText(), password.getText());
            account.setFirstName(name.getText());
            account.setLastName(lastName.getText());
            account.setType(getAccountType());
            //System.out.println(account.getType());
            ioControl.register(account).show();
        }
    }

    private String getAccountType() {
        if(!ioControl.isThereAdmin()) {
            if(accountTypeComboBox.getSelectionModel().isSelected(0))
                return "Admin";
            else if(accountTypeComboBox.getSelectionModel().isSelected(1))
                return "Vendor";
            else
                return "Customer";
        } else
            return (accountTypeComboBox.getSelectionModel().isSelected(0) ? "Vendor" : "Customer");

    }

    private boolean isTextFieldEmpty() {
        lastName.setBorder(null);
        name.setBorder(null);
        password.setBorder(null);
        username.setBorder(null);
        if (!username.getText().isEmpty()) {
            if (!password.getText().isEmpty()) {
                if (!name.getText().isEmpty()) {
                    if (!lastName.getText().isEmpty()) {
                        return false;
                    } else {
                        lastName.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5))));
                        return true;
                    }
                } else {
                    name.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5))));
                    return true;
                }
            } else {
                password.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5))));
                return true;
            }
        } else {
            username.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5))));
            return true;
        }
    }

    public void backToSignInMenu(MouseEvent event) {
        Parent root1 = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignInMenu.fxml"));
            root1 = loader.load();
            SignInProcessor signInProcessor = loader.getController();
            signInProcessor.setMyStage(myStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myStage.setTitle("Sign In");
        myStage.setScene(new Scene(root1));
    }

    public void onMouse(MouseEvent event) {
        backImage.setOpacity(0.7);
    }

    public void outMouse(MouseEvent event) {
        backImage.setOpacity(0.4);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAccTypeComboBox();
    }

    private void initAccTypeComboBox() {
        if(!ioControl.isThereAdmin()) {
            accountTypeComboBox.getItems().add("Admin");
        }
        accountTypeComboBox.getItems().add("Vendor");
        accountTypeComboBox.getItems().add("Customer");
        accountTypeComboBox.getSelectionModel().selectFirst();
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;

    }
}
