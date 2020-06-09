package view;

import com.jfoenix.controls.JFXButton;
import controller.account.AccountControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import model.existence.Account;

import java.net.URL;
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

    private static Account.AccountType accountType = Account.AccountType.ADMIN;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        updateAccountTable();
    }

    private void updateAccountTable() {
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

    public static void setAccountType(Account.AccountType accountType) {
        ViewAccountsProcessor.accountType = accountType;
    }
}
