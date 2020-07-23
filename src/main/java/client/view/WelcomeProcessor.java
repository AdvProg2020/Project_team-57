package client.view;

import client.api.Command;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import server.controller.IOControl;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import server.server.Response;
import server.model.existence.Account;
import server.model.existence.Account.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class WelcomeProcessor extends Processor implements Initializable {
    private static IOControl ioControl = IOControl.getController();

    //WelcomeMenu
    public Pane mainPane;

    //SignInMenu
    public JFXTextField userNameField;
    public JFXPasswordField passwordField;
    public JFXButton loginButton;

    //SignUpMenu
    private static boolean isNormal = true;
    private static boolean isAdmin = true;
    public Button signUp;
    public JFXTextField username;
    public JFXPasswordField password;
    public JFXTextField name;
    public JFXTextField lastName;
    public ImageView back;
    public ImageView backImage;
    public JFXComboBox<String> accountTypeComboBox;
    public ImageView imageOfSignUp;
    public AnchorPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String locationFile = location.getFile();

        if(locationFile.contains("WelcomeMenu"))
            setBG();
        else if(locationFile.contains("SignInMenu")) {
        }
        else if(locationFile.contains("SignUpMenu"))
            initAccTypeComboBox();

    }

    //WelcomeMenu
    private void setBG() {
        Image image = new Image(IMAGE_FOLDER_URL + "Backgrounds\\WelcomeMenuBG.jpg");
        Background background = new Background(
                new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                        new BackgroundSize(mainPane.getWidth(), mainPane.getHeight(), false, false, true, false)));
        mainPane.setBackground(background);
    }

    public void openAccountMenu() {
        if(isLoggedIn()) {
            try {
                Parent root = null;
                Main.getStage().getIcons().remove(0);
                Account loggedInAccount = getLoggedInAccount();

                switch (loggedInAccount.getType()) {
                    case "Admin" :
                        initAdminMenu();
                        break;
                    case "Vendor" :
                        root = FXMLLoader.load(Main.class.getResource("VendorMenu.fxml"));
                        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Vendor Icon.png")));
                        Main.setScene( loggedInAccount.getUsername() + " Menu", root);
                        break;
                    case "Customer" :
                        root = FXMLLoader.load(Main.class.getResource("CustomerMenu.fxml"));
                        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Customer Icon.png")));
                        Main.setScene(loggedInAccount.getUsername() + " Menu", root);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SignInMenu.fxml"));
                Parent root = loader.load();
                WelcomeProcessor signInProcessor = loader.getController();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                signInProcessor.setMyStage(newStage);
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("Login Icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Sign In");
                newStage.show();
            } catch (IOException e) {
                //:)
            }
        }
    }

    private void initAdminMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("AdminMenu.fxml"));
            Parent root = loader.load();
            AdminProcessor adminProcessor = loader.getController();
            adminProcessor.setMyStage(Main.getStage());
            Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Admin Icon.png")));
            Main.setScene(getLoggedInAccount().getUsername() + " Menu", root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openProductsMenu(ActionEvent actionEvent) {
        /*ProductControl.getController().setOffListic(false);*/
        setOffListic(false);
        try {
            Parent root;
            Main.getStage().getIcons().remove(0);
            Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Market Logo.png")));
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductsMenu.fxml"));
            root = loader.load();
            ProductsProcessor productsProcessor = loader.getController();
            productsProcessor.initProcessor(ProductsProcessor.ProductsMenuType.MAIN_PRODUCTS);
            Main.setScene( "Products Menu", root);
        } catch (IOException e) {
            //:)
        }
    }

    public void showOffsMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("OffList.fxml"));
            Main.getStage().getIcons().remove(0);
            Main.getStage().getIcons().add(new Image(IMAGE_FOLDER_URL + "Icons/discount.png"));
            Main.setScene("Off Menu", root);
        } catch (IOException e) {
            //:)
        }
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    //SignInMenu
    public void login() {
        if(userNameField.getText().equals("") || passwordField.getText().equals("")) {
            if(userNameField.getText().equals(""))
                userNameField.setStyle(errorTextFieldStyle);
            if(passwordField.getText().equals(""))
                passwordField.setStyle(errorTextFieldStyle);
        } else {
            Command<Account> command = getIOCommand("login", new Account(userNameField.getText(), passwordField.getText()));
            Response<String> loginResponse = client.postAndGet(command, Response.class, (Class<String>)String.class);

            Alert alert = loginResponse.getMessage().getAlert();
            Optional<ButtonType> optionalButtonType = alert.showAndWait();
            if(optionalButtonType.get() == ButtonType.OK) {
                if(alert.getHeaderText().equals("Congratulations")) {
                    if(parentProcessor != null && parentProcessor instanceof ProductsProcessor) {
                        ((ProductsProcessor) parentProcessor).myStage.close();
                        ((ProductsProcessor)((ProductsProcessor) parentProcessor).parentProcessor).showCartProducts(null);
                    }
                    client.setAuthToken(loginResponse.getData().get(0));
                    myStage.close();
                }
                if(alert.getHeaderText().contains("Username"))
                    userNameField.setStyle(errorTextFieldStyle);
                if(alert.getHeaderText().contains("Password"))
                    passwordField.setStyle(errorTextFieldStyle);
            }
        }

    }

    public void enterSignUpMenu(MouseEvent mouseEvent) {
        WelcomeProcessor.setIsNormal(true);
        Parent root1 = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpMenu.fxml"));
            root1 = loader.load();
            WelcomeProcessor signUpProcessor = loader.getController();
            signUpProcessor.setMyStage(myStage);
            signUpProcessor.setParentProcessor(parentProcessor);
        } catch (IOException e) {
            //:)
        }
        myStage.setTitle("Sign Up Menu");
        myStage.setScene(new Scene(root1));
    }

    public void signInTextFieldMouseClicked(Event event) {
        ((TextField) event.getSource()).setStyle("");
    }

    //SignUpMenu
    private void initAccTypeComboBox() {
        if(isNormal) {
            Command command = new Command("is there admin", Command.HandleType.ACCOUNT);
            Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
            if(!response.getData().get(0)) {
                accountTypeComboBox.getItems().add("Admin");
            }
            accountTypeComboBox.getItems().add("Vendor");
            accountTypeComboBox.getItems().add("Customer");
            accountTypeComboBox.getSelectionModel().selectFirst();
        } else {
            imageOfSignUp.setImage(new Image(IMAGE_FOLDER_URL + "Backgrounds\\steve jobs.png"));
            pane.getChildren().remove(backImage);
            if(isAdmin) {
                accountTypeComboBox.getItems().add("Admin");
            } else {
                accountTypeComboBox.getItems().add("Supporter");
            }
            accountTypeComboBox.getSelectionModel().selectFirst();
        }
    }

    public static void setIsNormal(boolean isNormal) {
        WelcomeProcessor.isNormal = isNormal;
    }

    public static void setIsAdmin(boolean isAdmin) {
        WelcomeProcessor.isAdmin = isAdmin;
    }

    public void register(ActionEvent event) {
        if (!isTextFieldEmpty()) {
            Response response = null;
            if(isAdmin || isNormal /*accountTypeComboBox.getSelectionModel().getSelectedItem().equals("Supporter")*/) {
                final Account account = new Account(username.getText(), password.getText());
                account.setFirstName(name.getText());
                account.setLastName(lastName.getText());
                account.setType(getAccountType());
                response =
                        client.postAndGet(getIOCommand("register", account), Response.class, (Class<Object>)Object.class);
            } else {
                final Supporter supporter = new Supporter();
                supporter.setUsername(userNameField.getText());
                supporter.setPassword(passwordField.getText());
                supporter.setFirstName(name.getText());
                supporter.setLastName(lastName.getText());
                response = registerSupporter(supporter);
            }

            Alert alert = response.getMessage().getAlert();
            if (alert.getTitle().equals("Successful")) {
                alert.show();
                signUp.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5))));
                if(isNormal)
                    backToSignInMenu(null);
                else {
                    username.deleteText(0, username.getText().length());
                    password.deleteText(0, password.getText().length());
                    name.deleteText(0, name.getText().length());
                    lastName.deleteText(0, lastName.getText().length());
                }
                return;
            }
            showError(alert);
        }
    }

    private Response registerSupporter(Supporter supporter) {
        Command<Supporter> command = new Command<>("register supporter", Command.HandleType.ACCOUNT, supporter);
        return client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    private void showError(Alert alert) {
        if (alert.getHeaderText().equals("Username Length Not Valid") ||
                alert.getHeaderText().equals("Username Format Not Valid") ||
                alert.getHeaderText().equals("Full Username")) {
            username.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else if (alert.getHeaderText().equals("Password Length Not Valid") ||
                alert.getHeaderText().equals("Password Format Not Valid")) {
            password.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else if (alert.getHeaderText().equals("Invalid FirstName")) {
            name.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else if (alert.getHeaderText().equals("Invalid LastName")) {
            lastName.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            alert.show();
        } else
            alert.show();
    }

    private String getAccountType() {
        if(isNormal) {
            Command command = new Command("is there admin", Command.HandleType.ACCOUNT);
            Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
            if(!response.getData().get(0)) {
                if(accountTypeComboBox.getSelectionModel().isSelected(0)) {
                    return "Admin";
                }
                else if(accountTypeComboBox.getSelectionModel().isSelected(1))
                    return "Vendor";
                else
                    return "Customer";
            } else
                return (accountTypeComboBox.getSelectionModel().isSelected(0) ? "Vendor" : "Customer");
        }
        return "Admin";
    }

    private boolean isTextFieldEmpty() {
        lastName.setBorder(null);
        name.setBorder(null);
        password.setBorder(null);
        username.setBorder(null);

        if(username.getText().isEmpty() || password.getText().isEmpty()
                || name.getText().isEmpty() || lastName.getText().isEmpty()) {
            if(username.getText().isEmpty()) {
                username.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(password.getText().isEmpty()) {
                password.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(name.getText().isEmpty()) {
                name.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            if(lastName.getText().isEmpty()) {
                lastName.setBorder(new Border(new BorderStroke(Color.FIREBRICK, BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0))));
            }
            return true;
        }
        return false;
    }

    public void backToSignInMenu(MouseEvent event) {
        Parent root1 = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignInMenu.fxml"));
            root1 = loader.load();
            WelcomeProcessor signInProcessor = loader.getController();
            signInProcessor.setMyStage(myStage);
            signInProcessor.setParentProcessor(parentProcessor);
        } catch (IOException e) {
            //:)
        }
        myStage.setTitle("Sign In");
        myStage.setScene(new Scene(root1));
    }

    public void signUpTextFieldMouseClicked(Event event) {
        ((TextField) event.getSource()).setBorder(null);
    }

    private Command<Account> getIOCommand(String message, Account account) {
        return new Command<>(message, Command.HandleType.ACCOUNT, account);
    }
}
