package view;

import com.jfoenix.controls.*;
import controller.account.AccountControl;
import controller.account.AdminControl;
import controller.account.CustomerControl;
import controller.product.ProductControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.existence.*;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;

import static model.existence.Account.AccountType.*;

public class TableViewProcessor<T> extends Processor {

    public JFXButton showDiscountButton;
    public JFXButton deleteDiscountButton;
    public JFXButton addDiscountButton;
    public Label codeLabel;
    public JFXButton customerSearchButton;
    public Label discountAddedUsersCountLabel;
    public JFXListView<String> discountCustomersListView;
    public JFXTextField discountCustomerSearchField;
    public Label commentTitle;
    public JFXButton showCommentButton;
    public JFXButton approveCommentButton;
    public JFXButton deleteCommentButton;
    public Label commenterUsername;
    public ImageView commenterProfileButton;
    public ImageView commentedProductMenuButton;
    public JFXTextField commentTitleField;
    public JFXTextArea commentContentArea;
    public JFXCheckBox commenterBoughtCheckBox;
    public ImageView showCommentBackButton;
    public Label offNameLabel;
    public JFXButton showEditedOffButton;
    public JFXButton approveEditButton;
    public JFXButton unApproveEditButton;
    public Label offVendorUsernameLabel;
    public Rectangle offImageRectangle;
    public JFXButton showPreviousOffButton;
    public JFXButton showOffButton;
    public JFXButton approveOffButton;
    public JFXButton deleteOffButton;


    public static enum TableViewType {
        CUSTOMERS(CUSTOMER), VENDORS(VENDOR), ADMINS(ADMIN),
        DISCOUNTS, DISCOUNT_CUSTOMERS, ADMIN_COMMENTS, ADMIN_OFFS;

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
    private TableViewType tableViewType;
    private T selectedItem;
    private String searchedUsername;
    private Pane tableViewPane;

    public void initProcessor(TableViewType tableViewType) {
        this.tableViewType = tableViewType;
        this.tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                 selectedItem = newSelection;
                updateSelectedItem();
            }
        });
        initColumns();
        updateTable();
        initOptions();
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
            case DISCOUNT_CUSTOMERS:
                initDiscountCustomersColumns();
                break;
            case ADMIN_COMMENTS:
                initAdminCommentsColumns();
                break;
            case ADMIN_OFFS:
                initAdminOffsColumns();
                break;
        }
    }

    private void initAdminOffsColumns() {
        TableColumn<T, String> offName = makeColumn("Off Name", "offName", 0.23);
        TableColumn<T, String> vendorUsername = makeColumn("Vendor Username", "vendorUsername", 0.23);
        TableColumn<T, Double> offPercent = makeColumn("Off Percentage", "offPercent", 0.34);
        TableColumn<T, String> status = makeColumn("Approval", "statStr", 0.15);
        tableView.getColumns().addAll(offName, vendorUsername, offPercent, status);
    }

    private void initAdminCommentsColumns() {
        TableColumn<T, String> usernameColumn = makeColumn("Username", "customerUsername", 0.23);
        TableColumn<T, String> firstNameColumn = makeColumn("Title", "title", 0.23);
        TableColumn<T, String> lastNameColumn = makeColumn("Content", "content", 0.34);
        TableColumn<T, String> isAdded = makeColumn("Approval", "statStr", 0.15);
        tableView.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn, isAdded);
    }

    private void initDiscountCustomersColumns() {
        TableColumn<T, String> usernameColumn = makeColumn("Username", "username", 0.26);
        TableColumn<T, String> firstNameColumn = makeColumn("First Name", "firstName", 0.30);
        TableColumn<T, String> lastNameColumn = makeColumn("Last Name", "lastName", 0.31);
        TableColumn<T, JFXCheckBox> isAdded = makeColumn("Added", "checkBox", .10);
        tableView.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn, isAdded);
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
                tableList.addAll((ArrayList<T>)AdminControl.getController().getAllDiscounts());
                break;
            case DISCOUNT_CUSTOMERS:
                tableList.addAll(getAllCustomersForDiscount());
                break;
            case ADMIN_COMMENTS:
                tableList.addAll((ArrayList<T>)AdminControl.getController().getAllUnApprovedComments());
                break;
            case ADMIN_OFFS:
                tableList.addAll((ArrayList<T>)AdminControl.getController().getAllUnApprovedOffs());
        }
        tableView.getItems().addAll(tableList);
        tableView.getSelectionModel().selectFirst();
        selectedItem = tableView.getSelectionModel().getSelectedItem();
    }

    private ArrayList<T> getAllCustomersForDiscount() {
        ArrayList<Account> customers;
        if(searchedUsername != null && searchedUsername.length() > 0) {
            customers = AccountControl.getController().getModifiedAccounts(CUSTOMER, searchedUsername);
        } else
            customers = AccountControl.getController().getModifiedAccounts(CUSTOMER);

        customers.forEach(customer -> {
            customer.getCheckBox().setOnAction(event -> {
                SaleProcessor processor = ((SaleProcessor)parentProcessor);
                if(customer.getCheckBox().isSelected()) {
                    if(!processor.isAccountAddedInDiscount(customer.getUsername())) {
                        processor.addUserToDiscount(customer.getUsername());
                        this.updateSelectedItem();
                    }
                } else {
                    if(processor.isAccountAddedInDiscount(customer.getUsername())) {
                        processor.removeUserFromDiscount(customer.getUsername());
                        this.updateSelectedItem();
                    }
                }
            });
            for (String discountAddedUser : ((SaleProcessor) parentProcessor).getDiscountAddedUsers()) {
                if(customer.getUsername().equals(discountAddedUser)) {
                    customer.getCheckBox().setSelected(true);
                }
            }
            customer.getCheckBox().setOnMouseEntered(event -> simpleButtonOnMouse(event));
            customer.getCheckBox().setOnMouseExited(event -> simpleButtonOutMouse(event));
        });

        return (ArrayList<T>) customers;
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
            case DISCOUNT_CUSTOMERS:
                mainBorderPane.setLeft(initDiscountCustomersOptions());
                break;
            case ADMIN_COMMENTS:
                mainBorderPane.setLeft(initAdminCommentsOptions());
                break;
            case ADMIN_OFFS:
                mainBorderPane.setLeft(initAdminOffsOptions());
                break;
        }
    }

    private Pane initAdminOffsOptions() {
        FXMLLoader loader;
        try {
            if(selectedItem != null) {
                Off off = (Off) selectedItem;
                loader = new FXMLLoader(
                        (off.getStatus() == 2) ?
                                Main.class.getResource("TableViewAdminOffsOptions.fxml")
                                :
                                Main.class.getResource("TableViewAdminEditingOffsOptions.fxml")
                );
                Pane root = loader.load();
                TableViewProcessor processor = loader.getController();
                processor.offNameLabel.setText(off.getOffName());
                processor.offVendorUsernameLabel.setText(off.getVendorUsername());
                if(!ProductControl.getController().doesOffHaveImage(off.getOffID()))
                    processor.offImageRectangle.setStrokeWidth(0);
                processor.offImageRectangle.setFill(new ImagePattern(ProductControl.getController().getOffImageByID(off.getOffID())));
                return root;
            } else {
                loader = new FXMLLoader(Main.class.getResource("TableViewAdminOffsOptions.fxml"));
                Pane root = loader.load();
                return root;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
    }

    private Node initAdminCommentsOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewCommentOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(selectedItem != null) {
                Comment comment = (Comment) selectedItem;
                processor.showCommentButton.setDisable(false);
                processor.approveCommentButton.setDisable(false);
                processor.deleteCommentButton.setDisable(false);
                processor.commenterProfileButton.setDisable(false);
                processor.commentedProductMenuButton.setDisable(false);
                processor.commentTitle.setText(comment.getTitle());
                processor.commenterUsername.setText(comment.getCustomerUsername());
            } else {
                processor.terminateOptions();
            }
            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pane initDiscountCustomersOptions() {
        tableView.getSelectionModel().clearSelection();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewDiscountCustomersOption.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.discountCustomersListView.getItems().addAll
                    (((SaleProcessor)parentProcessor).getDiscountAddedUsers());
            processor.discountAddedUsersCountLabel.setText("" + processor.discountCustomersListView.getItems().size());
            processor.discountCustomerSearchField.setText
                    ((searchedUsername == null ? "" : searchedUsername));
            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pane initDiscountOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewDiscountOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(selectedItem != null) {
                Discount discount = (Discount)selectedItem;
                processor.showDiscountButton.setDisable(false);
                processor.deleteDiscountButton.setDisable(false);
                processor.codeLabel.setText(discount.getCode());
            } else {
                processor.terminateOptions();
            }
            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                processor.terminateOptions();
            }
            return root;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void terminateOptions() {
        switch (((TableViewProcessor)parentProcessor).tableViewType) {
            case ADMINS:
            case VENDORS:
            case CUSTOMERS:
                terminateAccountOptions();
                break;
            case DISCOUNTS:
                terminateDiscountOptions();
                break;
            case ADMIN_COMMENTS:
                terminateAdminCommentsOptions();
                break;
        }
    }

    private void terminateAdminCommentsOptions() {
        showCommentButton.setDisable(true);
        approveCommentButton.setDisable(true);
        deleteCommentButton.setDisable(true);
        commenterProfileButton.setDisable(true);
        commentedProductMenuButton.setDisable(true);
        commentTitle.setText("Comment Title");
        commenterUsername.setText("Customer Username");
    }

    private void terminateDiscountOptions() {
        showDiscountButton.setDisable(true);
        deleteDiscountButton.setDisable(true);
        codeLabel.setText("Discount Code");
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

    public void updateSelectedItem() {
        if(tableView.getSelectionModel().getSelectedItem() != null)
            selectedItem = tableView.getSelectionModel().getSelectedItem();
        else
            tableView.getSelectionModel().selectFirst();
        initOptions();
    }


    //Graphics

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

    public void simpleButtonOnMouse(MouseEvent mouseEvent) {
        ((Parent)mouseEvent.getSource()).setStyle("-fx-cursor: hand;");
    }

    public void simpleButtonOutMouse(MouseEvent mouseEvent) {
        ((Parent)mouseEvent.getSource()).setStyle(null);
    }

    //Inside Methods

    public void showProfile(ActionEvent actionEvent) {
        try {
            Account selectedAccount = (Account) ((TableViewProcessor)parentProcessor).selectedItem;
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
        Account selectedAccount = (Account) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete " + selectedAccount.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AdminControl.getController().deleteUserWithUsername(selectedAccount.getUsername()).getAlert().show();
        }
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
    }

    public void approveUser(ActionEvent actionEvent) {
        Account selectedAccount = (Account) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Approve " + selectedAccount.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AccountControl.getController().modifyApprove(selectedAccount.getUsername(), 1).getAlert().show();
        }
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
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
                ((TableViewProcessor)parentProcessor).updateTable();
                ((TableViewProcessor)parentProcessor).updateSelectedItem();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDiscount(ActionEvent actionEvent) {
        Discount discount = (Discount)((TableViewProcessor)parentProcessor).tableView.getSelectionModel().getSelectedItem();
        if(canOpenSubStage("Show Discount " + discount.getID(), this)) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DiscountMenu.fxml"));
            try {
                Parent root = loader.load();
                SaleProcessor processor = loader.getController();
                processor.setDiscount(discount);
                processor.discountInfoMouseClicked(null);
                AdminControl.getController().addDiscountToHashMap(processor.getDiscount());
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Show Discount " + discount.getID());
                newStage.setResizable(false);
                processor.parentProcessor = this.parentProcessor;
                //System.out.println("Opening : " + processor);
                processor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteDiscount(ActionEvent actionEvent) {
        Discount selectedDiscount = (Discount) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete " + selectedDiscount.getCode() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AdminControl.getController().removeDiscountByID(selectedDiscount.getID());
            ((TableViewProcessor) parentProcessor).updateTable();
            ((TableViewProcessor) parentProcessor).updateSelectedItem();
        }
    }

    public void addNewDiscount(ActionEvent actionEvent) {
        if(canOpenSubStage("Add New Discount", this)) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DiscountMenu.fxml"));
            try {
                Parent root = loader.load();
                SaleProcessor processor = loader.getController();
                processor.discountInfoMouseClicked(null);
                AdminControl.getController().addDiscountToHashMap(processor.getDiscount());
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Add New Discount");
                newStage.setResizable(false);
                processor.parentProcessor = this.parentProcessor;
                this.parentProcessor = this;
                addSubStage(newStage);
                processor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void customerSearch() {
        ((TableViewProcessor)parentProcessor).searchedUsername = discountCustomerSearchField.getText();
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
    }

    public void discountCustomerSearchKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER)
            customerSearch();
    }

    public void showCommenterProfile(MouseEvent mouseEvent) {
        try {
            Account commenterAccount = AccountControl.getController().getAccountByUsername(((Comment)(((TableViewProcessor)parentProcessor).selectedItem)).getCustomerUsername());
            ProfileProcessor.setAccount(commenterAccount);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileMenu.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Profile Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle(commenterAccount.getUsername() + " Profile");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCommentedProductMenu(MouseEvent mouseEvent) {
        System.out.println("TODO");
        //TODO
    }

    public void modifyCommentApproval(ActionEvent actionEvent) {
        Comment selectedComment = (Comment) ((TableViewProcessor)parentProcessor).selectedItem;
        String alertStr;
        boolean approve;
        if(((JFXButton)actionEvent.getSource()).getText().equals(approveCommentButton.getText())){
            alertStr = "Are You Sure You Want To Approve " + selectedComment.getTitle() + "?";
            approve = true;
        } else {
            alertStr = "Are You Sure You Want To Delete " + selectedComment.getTitle()+ "?";
            approve = false;
        }
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, alertStr, ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            AdminControl.getController().modifyCommentApproval(selectedComment.getCommentID(), approve);
            if(((TableViewProcessor<T>) parentProcessor).tableViewPane != null) {
                ((TableViewProcessor<T>) parentProcessor).mainBorderPane.setCenter(((TableViewProcessor<T>) parentProcessor).tableViewPane);
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
            }
        }
    }

    public void showComment(ActionEvent actionEvent) {
        ((TableViewProcessor) parentProcessor).tableViewPane = (Pane) ((TableViewProcessor) parentProcessor).mainBorderPane.getCenter();
        Comment comment = (Comment) ((TableViewProcessor) parentProcessor).selectedItem;
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewShowComment.fxml"));
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.commentTitleField.setText(comment.getTitle());
            processor.commentContentArea.setText(comment.getContent());
            processor.commenterBoughtCheckBox.setSelected
                    (CustomerControl.getController().isProductPurchasedByCustomer(comment.getProductID(), comment.getCustomerUsername()));
            processor.showCommentBackButton.setOnMouseClicked(event -> {
                ((TableViewProcessor) parentProcessor).mainBorderPane.setCenter(((TableViewProcessor) parentProcessor).tableViewPane);
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
                ((TableViewProcessor<T>) parentProcessor).tableViewPane = null;
            });
            ((TableViewProcessor<T>) parentProcessor).mainBorderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //OffsRequests

    public void showOff(ActionEvent actionEvent) {
        Off off = (Off) ((TableViewProcessor)parentProcessor).tableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffMenu.fxml"));
        try {
            Parent root = loader.load();
            SaleProcessor processor = loader.getController();
            processor.setOff(off);
            processor.offInfoPaneMouseClick(null);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Show Discount " + off.getOffName());
            newStage.setResizable(false);
            processor.parentProcessor = this.parentProcessor;
            processor.setMyStage(newStage);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void approveOff(ActionEvent actionEvent) {
        
    }

    public void deleteOff(ActionEvent actionEvent) {

    }


}
