package view;

import com.jfoenix.controls.JFXButton;
import controller.account.AccountControl;
import controller.account.AdminControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.existence.Account;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

import static model.existence.Account.AccountType.*;

public class TableViewProcessor<T> {

    public static enum TableViewType {
        CUSTOMERS(CUSTOMER), VENDORS(VENDOR), ADMINS(ADMIN), DISCOUNTS;

        Account.AccountType accountType;

        TableViewType(Account.AccountType accountType) {
            this.accountType = accountType;
        }

        TableViewType() {
        }

        public Account.AccountType getAccountType() {
            return accountType;
        }
    }

    public Circle imageCircle;
    public Label nameLabel;
    public JFXButton showProfileButton;
    public JFXButton deleteUserButton;
    public JFXButton approveUserButton;
    public Label typeLabel;
    public JFXButton addAdminButton;
    public BorderPane mainBorderPane;
    public TableView<T> tableView;
    private TableViewProcessor parentProcessor;
    private TableViewType tableViewType;
    private T selectedItem;

    public void initProcessor(TableViewType tableViewType) {
        this.tableViewType = tableViewType;
        initColumns();
        updateTable();
        tableView.getSelectionModel().selectFirst();
        selectedItem = tableView.getSelectionModel().getSelectedItem();
        initOptions();
    }

    private void initOptions() {
        switch (tableViewType) {
            case ADMINS:
            case VENDORS:
            case CUSTOMERS:
                mainBorderPane.setLeft(initAccountOptions());
                break;
            case DISCOUNTS:
                mainBorderPane.setLeft(initDiscountOptions());
                break;
        }
    }

    private Pane initDiscountOptions() {
        return null;
    }

    private Pane initAccountOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewAccountOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(tableViewType != TableViewType.ADMINS) {
                root.getChildren().remove(processor.addAdminButton);
            }
            if(selectedItem != null) {
                Account account = (Account)selectedItem;
                processor.nameLabel.setText(account.getFirstName() + " " + account.getLastName());
                processor.typeLabel.setText(account.getType());
                processor.imageCircle.setFill
                        (new ImagePattern(AccountControl.getController().getProfileImageByUsername(account.getUsername())));
                switch (account.getType()) {
                    case "Admin" :
                        processor.showProfileButton.setDisable(false);
                        processor.approveUserButton.setDisable(account.isApproved());
                        processor.deleteUserButton.setDisable(true);
                        break;
                    default:
                        processor.showProfileButton.setDisable(false);
                        processor.approveUserButton.setDisable(account.isApproved());
                        processor.deleteUserButton.setDisable(false);
                }
            } else {
                processor.terminateAccountOptions();
            }
            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void terminateAccountOptions() {
        imageCircle.setFill(new ImagePattern
                (AccountControl.getController().getProfileImageByUsername("1")));
        showProfileButton.setDisable(true);
        approveUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
        nameLabel.setText("Username");
        typeLabel.setText("Type");
    }

    private void initColumns() {
        switch (tableViewType) {
            case ADMINS:
            case VENDORS:
            case CUSTOMERS:
                initAccountColumns();
                break;
            case DISCOUNTS:
                initDiscountColumns();
                break;
        }
    }

    private void initDiscountColumns() {
        TableColumn<T, String> codeColumn = makeColumn("Code", "code", 0.20);
        TableColumn<T, Date> startDateColumn = makeColumn("Start Date", "startDate", 0.20);
        TableColumn<T, Date> finishDateColumn = makeColumn("Finish Date", "finishDate", 0.20);
        TableColumn<T, Double> percentColumn = makeColumn("Percentage", "discountPercent", 0.08);
        TableColumn<T, Double> maxValColumn = makeColumn("Max Value", "maxDiscount", 0.18);
        TableColumn<T, Integer> maxRep = makeColumn("Max Repetition", "maxRepetition", 0.10);
        tableView.getColumns().addAll(codeColumn, startDateColumn, finishDateColumn, percentColumn, maxValColumn, maxRep);
    }

    private void initAccountColumns() {
        TableColumn<T, String> usernameColumn = makeColumn("Username", "username", 0.23);
        TableColumn<T, String> firstNameColumn = makeColumn("First Name", "firstName", 0.30);
        TableColumn<T, String> lastNameColumn = makeColumn("Last Name", "lastName", 0.30);
        TableColumn<T, String> approvalColumn = makeColumn("Approval", "isApproved", 0.15);
        tableView.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn, approvalColumn);
    }

    private<E> TableColumn<T, E> makeColumn(String text, String property, double sizePercentage){
        TableColumn<T, E> column = new TableColumn<>(text);
        column.prefWidthProperty().bind(tableView.widthProperty().multiply(sizePercentage));
        column.setResizable(false);
        column.setSortable(false);
        column.setEditable(false);
        column.setStyle("-fx-alignment: CENTER");
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    public void updateTable() {
        ObservableList<T> tableList = FXCollections.observableArrayList();
        tableView.getItems().remove(0, tableView.getItems().size());
        switch (tableViewType) {
            case ADMINS:
            case VENDORS:
            case CUSTOMERS:
                tableList.addAll((ArrayList<T>)AccountControl.getController().getModifiedAccounts(tableViewType.getAccountType()));
                break;
            case DISCOUNTS:
                tableList.addAll((T) AdminControl.getController().getAllDiscounts());
                break;
        }
        tableView.getItems().addAll(tableList);
    }

    public void updateSelectedItem() {
        if(tableView.getSelectionModel().getSelectedItem() != null)
            selectedItem = tableView.getSelectionModel().getSelectedItem();
        else
            tableView.getSelectionModel().selectFirst();
        initOptions();
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

    public void showProfile(ActionEvent actionEvent) {
        try {
            Account selectedAccount = (Account) parentProcessor.selectedItem;
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

    public void deleteUser(ActionEvent actionEvent) {
        Account selectedAccount = (Account) parentProcessor.selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete " + selectedAccount.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AdminControl.getController().deleteUserWithUsername(selectedAccount.getUsername()).getAlert().show();
        }
        updateTable();
        updateSelectedItem();
    }

    public void approveUser(ActionEvent actionEvent) {
        Account selectedAccount = (Account) parentProcessor.selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Approve " + selectedAccount.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AccountControl.getController().modifyApprove(selectedAccount.getUsername(), 1).getAlert().show();
        }
        updateTable();
        updateSelectedItem();
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
                updateTable();
                updateSelectedItem();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParentProcessor(TableViewProcessor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }

}
