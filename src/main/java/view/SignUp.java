package view;

import com.jfoenix.controls.JFXTextField;
import controller.IOControl;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import model.existence.Account;

public class SignUp {

    public Button signUp;
    public JFXTextField username;
    public JFXTextField password;
    public JFXTextField name;
    public JFXTextField lastName;
    public ImageView back;
    private final IOControl ioControl = IOControl.getController();

    public void register(ActionEvent event) {
        if (!isTextFieldEmpty()) {
            final Account account = new Account(username.getText(), password.getText());
            account.setFirstName(name.getText());
            account.setLastName(lastName.getText());
          //  ioControl.register(account).getAlert().show();
        }
    }

    private boolean isTextFieldEmpty() {
        if (!username.getText().isEmpty()) {
            if (!password.getText().isEmpty()) {
                if (!name.getText().isEmpty()) {
                    if (!lastName.getText().isEmpty()) {
                        return false;
                    } else {
                        lastName.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                        return true;
                    }
                } else {
                    name.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                    return true;
                }
            } else {
                password.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                return true;
            }
        } else {
            username.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
            return true;
        }
    }

    public void backToSignInMenu(MouseEvent event) {

    }
}
