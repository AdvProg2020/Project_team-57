package view;

import controller.Control;
import controller.account.AccountControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.IOException;
import java.util.Optional;

public class AccountProcessor extends Processor{


    public void logOutButton() {
        //System.out.println("hello");
        Optional<ButtonType> buttonType =
                new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure About Logging Out?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            subStages.forEach(stage -> {
                stage.close();
            });
            Control.logOut();
            backToMainMenu();
        }

    }

    public void backToMainMenu() {
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource("WelcomeMenu.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.getStage().getIcons().remove(0);
        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Main Icon.png")));
        Main.setScene("Boos Market", root);
    }

    public void showProfileMenu() {
        try {
            if(Control.getType().equals("Admin")) {
                System.out.println(parentProcessor);
                if (!canOpenSubStage(Control.getUsername() + " Profile", parentProcessor))
                    return;
            }
            else
                if(!canOpenSubStage(Control.getUsername() + " Profile", this))
                return;
            AccountControl accountControl = AccountControl.getController();
            ProfileProcessor.setAccount(accountControl.getAccount());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileMenu.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Profile Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle(Control.getUsername() + " Profile");
            if(Control.getType().equals("Admin"))
                parentProcessor.addSubStage(newStage);
            else
                addSubStage(newStage);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
