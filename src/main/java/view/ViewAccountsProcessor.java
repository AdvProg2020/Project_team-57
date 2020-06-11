package view;

import com.jfoenix.controls.JFXButton;
import controller.Control;
import controller.account.AccountControl;
import controller.account.AdminControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewAccountsProcessor implements Initializable {

    public Circle imageCircle;
    public Label nameLabel;
    public JFXButton showProfileButton;
    public JFXButton deleteUserButton;
    public JFXButton approveUserButton;
    public Label typeLabel;
    public TableColumn<Account, String> usernameColumn;
    public TableColumn<Account, String> firstNameColumn;
    public TableColumn<Account, String> lastNameColumn;
    public TableColumn<Account, String> approvalColumn;
    public TableView<Account> accountsTableView;
    public Pane optionsPane;
    public JFXButton addAdminButton;
    private Account selectedAccount = null;
    private Stage myStage;

    private Account.AccountType accountType = Account.AccountType.ADMIN;

    public ViewAccountsProcessor() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
    }

    public void initProcessor(Account.AccountType accountType) {
        this.accountType = accountType;
        updateAccountTable();
        if(accountType != Account.AccountType.ADMIN)
            optionsPane.getChildren().remove(addAdminButton);
        accountsTableView.getSelectionModel().selectFirst();
        updateSelectedAccount();
    }

    public void updateAccountTable() {
        ObservableList<Account> accounts = FXCollections.observableArrayList();
        accountsTableView.getItems().remove(0, accountsTableView.getItems().size());
        accounts.addAll(AccountControl.getController().getModifiedAccounts(accountType));
        accountsTableView.getItems().addAll(accounts);
    }

    private void initColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setResizable(false);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setResizable(false);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setResizable(false);
        approvalColumn.setCellValueFactory(new PropertyValueFactory<>("isApproved"));
        approvalColumn.setResizable(false);
    }

    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }

    public void updateSelectedAccount() {
        if(accountsTableView.getSelectionModel().getSelectedItem() != null) {
            selectedAccount = accountsTableView.getSelectionModel().getSelectedItem();
            nameLabel.setText(selectedAccount.getFirstName() + " " + selectedAccount.getLastName());
            typeLabel.setText(selectedAccount.getType());
            showOptions(selectedAccount);
            imageCircle.setFill
                    (new ImagePattern(AccountControl.getController().getProfileImageByUsername(selectedAccount.getUsername())));
            return;
        }
        terminateOptions();
    }

    private void terminateOptions() {
        showProfileButton.setDisable(true);
        approveUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
        nameLabel.setText("Username");
        typeLabel.setText("Type");
    }

    private void showOptions(Account account) {
        switch (accountType) {
            case ADMIN:
                showProfileButton.setDisable(false);
                approveUserButton.setDisable(account.isApproved());
                deleteUserButton.setDisable(true);
                break;
            default:
                showProfileButton.setDisable(false);
                approveUserButton.setDisable(account.isApproved());
                deleteUserButton.setDisable(false);
        }
    }

    public Stage getMyStage() {
        return myStage;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    public void onMouse(MouseEvent mouseEvent) {
        ((JFXButton)mouseEvent.getSource()).setStyle("-fx-border-color: #0277bd;" +
                "-fx-border-width: 0 0 3 0;" +
                "-fx-background-radius: 0 0 0 0;" +
                "-fx-cursor: hand;");
    }

    public void outMouse(MouseEvent mouseEvent) {
        ((JFXButton)mouseEvent.getSource()).setStyle("-fx-background-color: #ffffff;" +
                "-fx-background-radius: 10 10 10 10;");
    }

    public void deleteUser(ActionEvent actionEvent) {
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete " + selectedAccount.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AdminControl.getController().deleteUserWithUsername(selectedAccount.getUsername()).getAlert().show();
        }
        updateAccountTable();
        accountsTableView.getSelectionModel().selectFirst();
        updateSelectedAccount();
    }

    public void approveUser(ActionEvent actionEvent) {
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Approve " + selectedAccount.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AccountControl.getController().modifyApprove(selectedAccount.getUsername(), 1).getAlert().show();
        }
        updateAccountTable();
    }

    public void addNewAdmin(ActionEvent actionEvent) {
        try {
            SignUpProcessor.setIsNormal(false);
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("SignUp.fxml"));
            Parent root = loader.load();
            SignUpProcessor signUpProcessor = loader.getController();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            signUpProcessor.setMyStage(newStage);
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Login Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle("Register New Admin");
            newStage.show();
            newStage.setOnCloseRequest(event -> {
                updateAccountTable();
                accountsTableView.getSelectionModel().selectFirst();
                updateSelectedAccount();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProfile(ActionEvent actionEvent) {
        try {
            ProfileProcessor.setAccount(selectedAccount);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileMenu.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Profile Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle(selectedAccount.getUsername() + " Profile");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
