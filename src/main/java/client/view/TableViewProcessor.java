package client.view;

import client.api.Client;
import client.api.Command;
import com.jfoenix.controls.*;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseButton;
import server.controller.Control;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.account.CustomerControl;
import server.controller.account.VendorControl;
import server.controller.product.ProductControl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import server.model.existence.*;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import server.model.existence.Log.ProductOfLog;
import server.server.Response;

import static server.model.existence.Account.AccountType.*;

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
    public JFXButton approveEditButton;
    public JFXButton unApproveEditButton;
    public Label offVendorUsernameLabel;
    public Rectangle offImageRectangle;
    public JFXButton showPreviousOffButton;
    public JFXButton showOffButton;
    public JFXButton approveOffButton;
    public JFXButton deleteOffButton;
    public JFXButton editOffButton;
    public JFXButton addNewOffButton;
    public JFXButton showLogProduct;
    public Label logDateLabel;
    public Pane optionPane;
    public Circle imageCircle;
    public Label nameLabel;
    public JFXButton showProfileButton;
    public JFXButton deleteUserButton;
    public JFXButton approveUserButton;
    public Label typeLabel;
    public JFXButton addAdminButton;
    public BorderPane mainBorderPane;
    public TableView<T> tableView;
    public JFXTextField logInitialPrice;
    public JFXTextField logFinalPrice;
    public JFXTextField logOffPercent;
    //End
    public JFXTextField productInitialPriceField;
    public JFXTextField productFinalPriceField;
    public JFXTextField productOffPriceField;
    public Label countLabel;
    public Label discountPercentLabel;
    public JFXTextField discountPercentField;
    public Label vendorUsernameLabel;
    public JFXTextField vendorUsernameField;
    public JFXTextField productQuantityField;
    public JFXButton showDiscountCustomerButton;
    public Label repetitionLeftLabel;
    public Label usedDiscountLabel;
    public Pane invoicePane;
    public Rectangle rightRectangle;
    public Rectangle leftRectangle;
    public ImageView logsImageView;
    private TableViewType tableViewType;
    private T selectedItem;
    private String searchedUsername;
    private Pane tableViewPane;
    private Log selectedLog;

    public static enum TableViewType {
        CUSTOMERS(CUSTOMER), VENDORS(VENDOR), ADMINS(ADMIN),
        DISCOUNTS, DISCOUNT_CUSTOMERS, ADMIN_COMMENTS, ADMIN_OFFS,
        VENDOR_OFFS, LOGS, PRODUCTS_OF_LOG, CUSTOMER_DISCOUNTS,
        PRODUCT_BUYERS;

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

    public void initProcessor(TableViewType tableViewType) {
        this.tableViewType = tableViewType;
        setTableViewModifiedFeatures();
        initColumns();
        updateTable();
        initOptions();
    }

    private void setTableViewModifiedFeatures() {
        this.tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                updateSelectedItem();
            }
        });

        if(tableViewType == TableViewType.DISCOUNT_CUSTOMERS) {
            tableView.setRowFactory(tv -> {
                TableRow<T> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 ) {
                        selectedItem = row.getItem();
                        Account account = (Account)selectedItem;
                        ((SaleProcessor)parentProcessor).addUserToDiscount(account.getUsername());
                        updateSelectedItem();
                        updateTable();
                    }
                });
                return row;
            });
        }

        this.tableView.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });
    }

    private void initColumns() {
        switch (tableViewType) {
            case ADMINS:
            case VENDORS:
            case CUSTOMERS:
                initAccountColumns();
                break;
            case CUSTOMER_DISCOUNTS:
            case DISCOUNTS:
                initDiscountColumns();
                break;
            case DISCOUNT_CUSTOMERS:
                //TODO(FOR CHECKBOX)
                initDiscountCustomersColumns();
                break;
            case ADMIN_COMMENTS:
                initAdminCommentsColumns();
                break;
            case ADMIN_OFFS:
                initAdminOffsColumns();
                break;
            case VENDOR_OFFS:
                initVendorOffsColumns();
                break;
            case LOGS:
                initVendorSellLogsColumns();
                break;
            case PRODUCTS_OF_LOG:
                initProductsOfLogColumns();
                break;
            case PRODUCT_BUYERS:
                initProductBuyersColumns();
                break;
        }
    }

    private void initProductBuyersColumns() {
        TableColumn<T, String> usernameColumn = makeColumn("Username", "username", 0.23);
        TableColumn<T, String> firstNameColumn = makeColumn("First Name", "firstName", 0.25);
        TableColumn<T, String> lastNameColumn = makeColumn("Last Name", "lastName", 0.25);
        TableColumn<T, String> email = makeColumn("Email", "email", 0.25);
        tableView.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn, email);
    }

    private void initProductsOfLogColumns() {
        TableColumn<T, String> productName = makeColumn("Product Name", "productName", 0.30);
        TableColumn<T, String> vendorUsername = makeColumn("Vendor Username", "vendorUsername", 0.26);
        TableColumn<T, String> quantity = makeColumn("Quantity", "quantityStr", 0.18);
        TableColumn<T, Double> initPrice = makeColumn("Initial Price", "initPrice", 0.23);
        tableView.getColumns().addAll(productName, vendorUsername, quantity, initPrice);
    }

    private void initVendorSellLogsColumns() {
        TableColumn<T, String> customerUsername = makeColumn("Customer Username", "customerUsername", 0.315);
        TableColumn<T, Date> logDate = makeColumn("Sell Date", "date", 0.25);
        TableColumn<T, Integer> productsCount = makeColumn("Products", "productsCount", 0.23);
        TableColumn<T, String> status = makeColumn("Status", "statStr", 0.17);
        tableView.getColumns().addAll(customerUsername, logDate, productsCount, status);
    }

    private void initVendorOffsColumns() {
        TableColumn<T, String> offName = makeColumn("Off Name", "offName", 0.315);
        TableColumn<T, Date> offFinishDate = makeColumn("Finish Date", "finishDate", 0.25);
        TableColumn<T, Double> offPercent = makeColumn("Off Percentage", "offPercent", 0.23);
        TableColumn<T, String> status = makeColumn("Approval", "statStr", 0.17);
        tableView.getColumns().addAll(offName, offFinishDate, offPercent, status);
    }

    private void initAdminOffsColumns() {
        TableColumn<T, String> offName = makeColumn("Off Name", "offName", 0.30);
        TableColumn<T, String> vendorUsername = makeColumn("Vendor Username", "vendorUsername", 0.265);
        TableColumn<T, Double> offPercent = makeColumn("Off Percentage", "offPercent", 0.23);
        TableColumn<T, String> status = makeColumn("Approval", "statStr", 0.17);
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
        TableColumn<T, String> usernameColumn = makeColumn("Username", "username", 0.30);
        TableColumn<T, String> firstNameColumn = makeColumn("First Name", "firstName", 0.32);
        TableColumn<T, String> lastNameColumn = makeColumn("Last Name", "lastName", 0.33);
        tableView.getColumns().addAll(usernameColumn, firstNameColumn, lastNameColumn);
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
//        tableView.getItems().remove(0, tableView.getItems().size());
        tableView.getItems().removeAll(tableView.getItems());
        switch (tableViewType) {
            case ADMINS:
            case VENDORS:
            case CUSTOMERS:
                tableList.addAll((ArrayList<T>)getModifiedAccounts(tableViewType.getAccountType()));
                break;
            case DISCOUNTS:
                tableList.addAll((ArrayList<T>)getAllDiscounts());
                break;
            case DISCOUNT_CUSTOMERS:
                //TODO(FOR CHECKBOX)

                tableList.addAll((ArrayList<T>)getAllCustomersForDiscount());
                break;
            case ADMIN_COMMENTS:
                tableList.addAll((ArrayList<T>)getAllUnApprovedComments());
                break;
            case ADMIN_OFFS:
                tableList.addAll((ArrayList<T>)getAllUnApprovedOffs());
                break;
            case VENDOR_OFFS:
                tableList.addAll((ArrayList<T>) getAllOffs());
                break;
            case LOGS:
                if(Control.getType().equals("Vendor"))
                    tableList.addAll((ArrayList<T>) VendorControl.getController().getAllVendorLogs());
                else
                    tableList.addAll((ArrayList<T>) CustomerControl.getController().getAllLogs());
                break;
            case PRODUCTS_OF_LOG:
                tableList.addAll((ArrayList<T>) selectedLog.getAllProducts());
                break;
            case CUSTOMER_DISCOUNTS:
                tableList.addAll((ArrayList<T>) getDiscounts());
                break;
            case PRODUCT_BUYERS:
                tableList.addAll((ArrayList<T>) VendorControl.getController().getProductBuyers());
                break;
        }
        tableView.getItems().addAll(tableList);
        tableView.getSelectionModel().selectFirst();
        selectedItem = tableView.getSelectionModel().getSelectedItem();
    }

    private ArrayList<Discount> getDiscounts() {
        Command command = new Command("get customer discounts", Command.HandleType.SALE);
        Response<Discount> response = client.postAndGet(command, Response.class, (Class<Discount>)Discount.class);
        return new ArrayList<>(response.getData());
    }

    private ArrayList<Off> getAllUnApprovedOffs() {
        ArrayList<Off> offs = new ArrayList<>();

        Command command = new Command("get all unapproved offs", Command.HandleType.SALE);
        Response<Off> response = client.postAndGet(command, Response.class, (Class<Off>)Off.class);
        offs.addAll(response.getData());
        return offs;
    }

    private ArrayList<Off> getAllOffs() {
        Command command = new Command("get vendor offs", Command.HandleType.SALE);
        Response<Off> response = client.postAndGet(command, Response.class, (Class<Off>)Off.class);
        return new ArrayList<>(response.getData());
    }

    private ArrayList<Comment> getAllUnApprovedComments() {
        Command command = new Command("get all unapproved comments", Command.HandleType.PRODUCT);
        Response<Comment> response = client.postAndGet(command, Response.class, (Class<Comment>)Comment.class);
        return new ArrayList<>(response.getData());
    }

    private ArrayList<Account> getAllCustomersForDiscount() {
        ArrayList<Account> customers = new ArrayList<>();
        Command<String> command;
        if(searchedUsername != null && searchedUsername.length() > 0) {
            command = new Command<>("get all customers with search", Command.HandleType.ACCOUNT, searchedUsername);
        } else {
            command = new Command<>("get all customers", Command.HandleType.ACCOUNT);
        }
        Response<Account> response = client.postAndGet(command, Response.class, (Class<Account>)Account.class);
        customers.addAll(response.getData());
        return customers;
    }

    private ArrayList<Discount> getAllDiscounts() {
        Command command = new Command("get all discounts", Command.HandleType.SALE);
        Response<Discount> response = client.postAndGet(command, Response.class, (Class<Discount>)Discount.class);
        return new ArrayList<>(response.getData());
    }

    private ArrayList<Account> getModifiedAccounts(Account.AccountType accountType) {
/*        String type = "";
        switch (accountType) {
            case ADMIN:
                type = "admin";
                break;
            case CUSTOMER:
                type = "customer";
                break;
            case VENDOR:
                type = "vendor";
                break;
        }*/
        Command<Account.AccountType> command = new Command<>("modified accounts", Command.HandleType.ACCOUNT, accountType);
        Response<Account> response = client.postAndGet(command, Response.class, (Class<Account>) Account.class);
        ArrayList<Account> accounts = new ArrayList<>(response.getData());
        return accounts;
    }

    //TODO(FOR CHECKBOX)
/*    private ArrayList<T> getAllCustomersForDiscount() {
        ArrayList<Account> customers = new ArrayList<>();
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
    }*/

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
                //TODO(FOR CHECKBOX)
                mainBorderPane.setLeft(initDiscountCustomersOptions());
                break;
            case ADMIN_COMMENTS:
                mainBorderPane.setLeft(initAdminCommentsOptions());
                break;
            case ADMIN_OFFS:
                mainBorderPane.setLeft(initAdminOffsOptions());
                break;
            case VENDOR_OFFS:
                mainBorderPane.setLeft(initVendorOffsOptions());
                break;
            case LOGS:
                mainBorderPane.setLeft(initLogsOptions());
                break;
            case PRODUCTS_OF_LOG:
                mainBorderPane.setLeft(initProductOfLogsOptions());
                break;
            case CUSTOMER_DISCOUNTS:
                mainBorderPane.setLeft(initCustomerDiscounts());
                break;
            case PRODUCT_BUYERS:
                mainBorderPane.setLeft(initProductBuyersOptions());
                break;
        }
    }

    private Pane initProductBuyersOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewProductBuyerOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(selectedItem != null) {
                Account account = (Account)selectedItem;
                processor.nameLabel.setText(account.getFirstName() + " " + account.getLastName());
                processor.imageCircle.setFill
                        (new ImagePattern(AccountControl.getController().getProfileImageByUsername(account.getUsername())));
                processor.showProfileButton.setDisable(false);
            }
            return root;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    private Pane initCustomerDiscounts() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewCustomerDiscountOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(selectedItem != null) {
                Discount discount = (Discount)selectedItem;
                processor.showDiscountCustomerButton.setDisable(false);
                String username = getUsername();
                processor.usedDiscountLabel.setText("" + discount.getCustomersWithRepetition().get(username));
                processor.repetitionLeftLabel.setText("" + (discount.getMaxRepetition() - discount.getCustomersWithRepetition().get(username)));
            }
            return root;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    private Pane initProductOfLogsOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewLogProductOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(Control.getType() != null && Control.getType().equals("Vendor")) {
                Stop[] stops = new Stop[] {
                        new Stop(0, Color.valueOf("#360033")),
                        new Stop(1, Color.valueOf("#127183"))
                };
                LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
                processor.optionPane.setBackground(new Background(backgroundFill));
            } else {
                processor.optionPane.setStyle("-fx-background-color: #ffd180;");
            }
            if(selectedItem != null) {
                ProductOfLog productOfLog = (ProductOfLog) selectedItem;
                processor.productInitialPriceField.setText(getSmoothDoubleFormat(productOfLog.getInitPrice()) + " $");
                if(Control.getType() != null) {
                    processor.productFinalPriceField.setText((Control.getType().equals("Vendor")) ?
                            "" + productOfLog.getOffPrice() + " $" : "" + (productOfLog.getOffPrice() * (1 - (selectedLog.getDiscountPercent()/100.0))) + " $");
                    if(Control.getType().equals("Vendor")) {
                        processor.invoicePane.getChildren().removeAll(processor.discountPercentLabel, processor.vendorUsernameLabel,
                                processor.vendorUsernameField, processor.discountPercentField);
                    } else {
                        processor.vendorUsernameField.setText(productOfLog.getVendorUsername());
                        processor.discountPercentField.setText(getSmoothDoubleFormat(selectedLog.getDiscountPercent()) + " %");
                    }
                }
                processor.productOffPriceField.setText(getSmoothDoubleFormat(((1.0 - (productOfLog.getOffPrice()/productOfLog.getInitPrice())) * 100)) +" %");
                processor.countLabel.setText("" + (productOfLog.isCountable() ?
                        "Count" : "Amount"));
                String quantity = "" + ((!productOfLog.isCountable()) ? getSmoothDoubleFormat(productOfLog.getAmount()) : productOfLog.getCount());
                processor.productQuantityField.setText(quantity);
            }
            return root;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    private Pane initLogsOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewLogsOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            if(Control.getType() != null && Control.getType().equals("Vendor")) {
                Stop[] stops = new Stop[] {
                        new Stop(0, Color.valueOf("#360033")),
                        new Stop(1, Color.valueOf("#127183"))
                };
                LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
                processor.optionPane.setBackground(new Background(backgroundFill));
            } else {
                processor.rightRectangle.setFill(Color.valueOf("#f57c00"));
                processor.leftRectangle.setFill(Color.valueOf("#f57c00"));
                processor.logsImageView.setImage(new Image(IMAGE_FOLDER_URL + "Icons\\customer invoice.png"));
                processor.optionPane.setStyle("-fx-background-color: #ffd180;");
            }
            if(selectedItem != null) {
                Log log = (Log)selectedItem;
                java.util.Date date = new java.util.Date(log.getDate().getTime());
                processor.logDateLabel.setText(date.toString());
                processor.logInitialPrice.setText(getSmoothDoubleFormat(log.getInitialPrice()) + " $");
                if(Control.getType() != null) {
                    processor.logFinalPrice.setText((Control.getType().equals("Vendor")) ?
                            "" + log.getVendorFinalPrice() + " $" : "" + log.getCustomerFinalPrice() + " $");
                }
                processor.logOffPercent.setText(getSmoothDoubleFormat(((1.0 - (log.getVendorFinalPrice()/log.getInitialPrice())) * 100)) +" %");
            } else {
                processor.terminateOptions();
            }
            return root;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    private Pane initVendorOffsOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewVendorOffsOptions.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            Stop[] stops = new Stop[] {
                    new Stop(0, Color.valueOf("#360033")),
                    new Stop(1, Color.valueOf("#127183"))
            };
            LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
            BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
            processor.optionPane.setBackground(new Background(backgroundFill));
            if(selectedItem != null) {
                Off off = (Off)selectedItem;
                processor.editOffButton.setDisable(false);
                processor.deleteOffButton.setDisable(false);
                processor.offNameLabel.setText(off.getOffName());
                setOffPicture(processor.offImageRectangle, off);
                return root;
            } else {
                processor.offImageRectangle.setStrokeWidth(0);
                processor.offImageRectangle.setFill(new ImagePattern(client.getImage(getOffImageCommand("", false))));
                processor.terminateOptions();
            }
            return root;
        } catch (IOException e) {
            //:)
        }
        return null;
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
                processor.setParentProcessor(this);
                processor.offNameLabel.setText(off.getOffName());
                processor.offVendorUsernameLabel.setText(off.getVendorUsername());
                setOffPicture(processor.offImageRectangle, off);
                    return root;
            } else {
                loader = new FXMLLoader(Main.class.getResource("TableViewAdminOffsOptions.fxml"));
                Pane root = loader.load();
                TableViewProcessor processor = loader.getController();
                processor.setParentProcessor(this);
                processor.offImageRectangle.setStrokeWidth(0);
                processor.offImageRectangle.setFill(new ImagePattern(ProductControl.getController().getOffImageByID("")));
                processor.terminateOptions();
                return root;
            }
        } catch (IOException e) {
            //:)
        }
        return new Pane();
    }

    private Pane initAdminCommentsOptions() {
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

    //TODO(FOR CHECKBOX)
    private Pane initDiscountCustomersOptions() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewDiscountCustomersOption.fxml"));
        try {
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.setParentProcessor(this);
            processor.discountCustomersListView.setCellFactory(param -> {
                ListCell<String> listCell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String string, boolean empty) {
                        super.updateItem(string, empty);
                        if(!empty)
                            setText(string);
                    }
                };
                listCell.setOnMouseClicked(event -> {
                    if(event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY) && listCell.getItem() != null) {
                        String selectedCustomer = ((ListCell<String>)event.getSource()).getItem();
                        if(selectedCustomer != null) {
                            ((SaleProcessor)parentProcessor).removeUserFromDiscount(selectedCustomer);
                            updateSelectedItem();
                            updateTable();
                        }
                    }
                });
                return listCell;
            });
            processor.discountCustomersListView.getItems().addAll
                    (((SaleProcessor)parentProcessor).getDiscountAddedUsernames());
            processor.discountAddedUsersCountLabel.setText("" + processor.discountCustomersListView.getItems().size());
            processor.discountCustomerSearchField.setText
                    ((searchedUsername == null ? "" : searchedUsername));
            return root;
        } catch (IOException e) {
            //:)
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
            //:)
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
                        (new ImagePattern(getProfileImage(account.getUsername())));
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
            //:)
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
            case ADMIN_OFFS:
                terminateOffOptions();
                break;
            case VENDOR_OFFS:
                terminateVendorOffOptions();
                break;
            case LOGS:
                terminateVendorSellLogOptions();
                break;
        }
    }

    private void terminateVendorOffOptions() {
        editOffButton.setDisable(true);
        deleteOffButton.setDisable(true);
        offNameLabel.setText("Off Name");
    }

    private void terminateVendorSellLogOptions() {
        showLogProduct.setDisable(true);
        logDateLabel.setText("Log Date");
    }

    private void terminateOffOptions() {
        offNameLabel.setText("Off Name");
        offVendorUsernameLabel.setText("Off Vendor");
        showOffButton.setDisable(true);
        approveOffButton.setDisable(true);
        deleteOffButton.setDisable(true);
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
            if (tableView.getSelectionModel().getSelectedItem() != null)
                selectedItem = tableView.getSelectionModel().getSelectedItem();
            else
                tableView.getSelectionModel().selectFirst();

            initOptions();
    }

    private void setOffPicture(Rectangle offImageRectangle, Off off) {
        boolean isEditing = isOffEditing(off.getOffID());
        if(!doesOffHaveImage(off.getOffID(), isEditing))
            offImageRectangle.setStrokeWidth(0);
        offImageRectangle.setFill(new ImagePattern(client.getImage(getOffImageCommand(off.getOffID(), isEditing))));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileMenu.fxml"));
            Parent root = loader.load();
            ProfileProcessor profileProcessor = loader.getController();
            profileProcessor.init(selectedAccount, "ProfileMenu");
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Profile Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle(selectedAccount.getUsername() + " Profile");
            parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.show();
        } catch (IOException e) {
            //:)
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
            WelcomeProcessor.setIsNormal(false);
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("SignUpMenu.fxml"));
            Parent root = loader.load();
            WelcomeProcessor signUpProcessor = loader.getController();
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
            //:)
        }
    }

    public void showDiscount(ActionEvent actionEvent) {
        Discount discount = (Discount)((TableViewProcessor)parentProcessor).selectedItem;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("DiscountMenu.fxml"));
        try {
            Parent root = loader.load();
            SaleProcessor processor = loader.getController();
            processor.setDiscount(discount);
            processor.discountInfoMouseClicked(null);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Show Discount " + discount.getID());
            newStage.setResizable(false);
            processor.parentProcessor = this.parentProcessor;
            processor.setMyStage(newStage);
            parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Discounts Icon.png")));
            newStage.show();
        } catch (IOException e) {
            //:)
        }
    }

    public void deleteDiscount(ActionEvent actionEvent) {
        Discount selectedDiscount = (Discount) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete " + selectedDiscount.getCode() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            Command<String> command = new Command<>("delete discount", Command.HandleType.SALE, selectedDiscount.getID());
            client.postAndGet(command, Response.class, (Class<Object>)Object.class);
            ((TableViewProcessor) parentProcessor).updateTable();
            ((TableViewProcessor) parentProcessor).updateSelectedItem();
        }
    }

    public void addNewDiscount(ActionEvent actionEvent) {
        if(canOpenSubStage("Add New Discount", parentProcessor)) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DiscountMenu.fxml"));
            try {
                Parent root = loader.load();
                SaleProcessor processor = loader.getController();
                processor.setDiscount(new Discount());
                processor.discountInfoMouseClicked(null);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Add New Discount");
                newStage.setResizable(false);
                processor.parentProcessor = this.parentProcessor;
                parentProcessor.addSubStage(newStage);
                parentProcessor.parentProcessor.addSubStage(newStage);
                processor.setMyStage(newStage);
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("Discounts Icon.png")));
                newStage.show();
            } catch (IOException e) {
                //:)
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
            Account commenterAccount = getAccountByUsername(((Comment)(((TableViewProcessor)parentProcessor).selectedItem)).getCustomerUsername());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileMenu.fxml"));
            Parent root = loader.load();
            ProfileProcessor profileProcessor = loader.getController();
            profileProcessor.init(commenterAccount, "ProfileMenu");
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Profile Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle(commenterAccount.getUsername() + " Profile");
            parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCommentedProductMenu(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductMenu.fxml"));
            Parent root = loader.load();
            ProductProcessor productProcessor = loader.getController();
            productProcessor.setParentProcessor(parentProcessor);
            Product product = getProductByID(((Comment)(((TableViewProcessor)parentProcessor).selectedItem)).getProductID(),"product");
            productProcessor.initProcessor(product, ProductProcessor.ProductMenuType.ADMIN);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(product.getName() + " Menu");
            productProcessor.setMyStage(stage);
            parentProcessor.parentProcessor.addSubStage(stage);
            stage.setResizable(false);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("Product Icon.png")));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            modifyTheCommentApproval(selectedComment.getCommentID(), approve);
            if(((TableViewProcessor<T>) parentProcessor).tableViewPane != null) {
                ((TableViewProcessor<T>) parentProcessor).mainBorderPane.setCenter(((TableViewProcessor<T>) parentProcessor).tableViewPane);
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
            }
        }
    }

    private void modifyTheCommentApproval(String commentID, boolean approve) {
        Command<String> command = new Command<>("modify comment approval", Command.HandleType.PRODUCT, commentID, Boolean.toString(approve));
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    public void showComment(ActionEvent actionEvent) {
        Comment comment = (Comment) ((TableViewProcessor) parentProcessor).selectedItem;
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewShowComment.fxml"));
            Pane root = loader.load();
            TableViewProcessor processor = loader.getController();
            processor.commentTitleField.setText(comment.getTitle());
            processor.commentContentArea.setText(comment.getContent());
            processor.commenterBoughtCheckBox.setSelected
                    (isProductPurchasedByCustomer(comment.getProductID(), comment.getCustomerUsername()));
            processor.showCommentBackButton.setOnMouseClicked(event -> {
                ((TableViewProcessor) parentProcessor).mainBorderPane.setCenter(((TableViewProcessor) parentProcessor).tableViewPane);
                ((TableViewProcessor) parentProcessor).updateTable();
                ((TableViewProcessor) parentProcessor).updateSelectedItem();
            });
            ((TableViewProcessor<T>) parentProcessor).mainBorderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTableViewPane(Pane tableViewPane) {
        this.tableViewPane = tableViewPane;
    }

    //OffsRequests

    public void showOff(ActionEvent actionEvent) {
        Off off = (Off) ((TableViewProcessor)parentProcessor).tableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffMenu.fxml"));
        try {

            if(isOffEditing(off.getOffID()))
                off = getEditingOffByID(off.getOffID());
            Parent root = loader.load();
            SaleProcessor processor = loader.getController();
            processor.setParentProcessor(this.parentProcessor);
            processor.setOff(off);
            processor.getOffImageFile();
            processor.offInfoPaneMouseClick(null);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Show Off " + off.getOffName());
            newStage.setResizable(false);
            processor.setMyStage(newStage);
            parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.getIcons().add(new Image(Main.class.getResourceAsStream("Offs Icon.png")));
            newStage.show();
        } catch (IOException e) {
            //:)
        }
    }

    private Off getEditingOffByID(String offID) {
        Command<String> command = new Command<>("get edit off", Command.HandleType.SALE, offID);
        Response<Off> response = client.postAndGet(command, Response.class, (Class<Off>)Off.class);
        return response.getDatum();
    }

    public void approveOff(ActionEvent actionEvent) {
        Off selectedOff = (Off) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Approve " + selectedOff.getOffName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            client.postAndGet(new Command<String>("approve off", Command.HandleType.SALE, selectedOff.getOffID()), Response.class, (Class<Object>)Object.class);
        }
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
    }

    public void deleteOff(ActionEvent actionEvent) {
        Off selectedOff = (Off) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Delete " + selectedOff.getOffName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            Command<String> command = new Command<>("delete off", Command.HandleType.SALE, selectedOff.getOffID());
            client.postAndGet(command, Response.class, (Class<Object>)Object.class);
            //AdminControl.getController().modifyOffApprove(selectedOff.getOffID(), false);
        }
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
    }

    public void editOff(ActionEvent actionEvent) {
        Off off = (Off) ((TableViewProcessor)parentProcessor).selectedItem;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffMenu.fxml"));
        try {
            if(isOffEditing(off.getOffID()))
                off = getEditingOffByID(off.getOffID());
            Parent root = loader.load();
            SaleProcessor processor = loader.getController();
            processor.setParentProcessor(this.parentProcessor);
            processor.setOff(off);
            processor.setEditing(true);
            processor.getOffImageFile();
            processor.offInfoPaneMouseClick(null);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Show Off " + off.getOffName());
            newStage.setResizable(false);
            processor.setMyStage(newStage);
            this.parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.getIcons().add(new Image(Main.class.getResourceAsStream("Offs Icon.png")));
            newStage.show();
        } catch (IOException e) {
            //:)
        }
    }

    public void addNewOff(ActionEvent actionEvent) {
        if(canOpenSubStage("Add New Off", parentProcessor.parentProcessor)) {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffMenu.fxml"));
                Parent root = loader.load();
                SaleProcessor processor = loader.getController();
                processor.setParentProcessor(parentProcessor);
                processor.offInfoPaneMouseClick(null);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Add New Off");
                newStage.setResizable(false);
                parentProcessor.parentProcessor.addSubStage(newStage);
                processor.setMyStage(newStage);
                newStage.getIcons().add(new Image(Main.class.getResourceAsStream("Offs Icon.png")));
                newStage.show();
            } catch (IOException e) {
                //:);
            }
        }
    }

    public void approveEdit(ActionEvent actionEvent) {
        Off selectedOff = (Off) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Approve " + selectedOff.getOffName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            client.postAndGet(new Command<String>("approve editing off", Command.HandleType.SALE, selectedOff.getOffID()), Response.class, (Class<Object>)Object.class);
//            AdminControl.getController().modifyOffEditingApprove(selectedOff.getOffID(), true);
        }
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
    }

    public void unApproveEdit(ActionEvent actionEvent) {
        Off selectedOff = (Off) ((TableViewProcessor)parentProcessor).selectedItem;
        Optional<ButtonType> buttonType = new Alert
                (Alert.AlertType.CONFIRMATION, "Are You Sure You Want To Unapprove " + selectedOff.getOffName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            client.postAndGet(new Command<String>("unapprove editing off", Command.HandleType.SALE, selectedOff.getOffID()), Response.class, (Class<Object>)Object.class);
//            AdminControl.getController().modifyOffEditingApprove(selectedOff.getOffID(), false);
        }
        ((TableViewProcessor)parentProcessor).updateTable();
        ((TableViewProcessor)parentProcessor).updateSelectedItem();
    }

    public void showPreviousOff(ActionEvent actionEvent) {
        Off off = (Off) ((TableViewProcessor)parentProcessor).selectedItem;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffMenu.fxml"));
        try {
            off = getOffByID(off.getOffID(), "off");
            Parent root = loader.load();
            SaleProcessor processor = loader.getController();
            processor.setParentProcessor(this.parentProcessor);
            off.setStatus(1);
            processor.setOff(off);
            processor.setPreviousOff(true);
            processor.getOffImageFile();
            processor.offInfoPaneMouseClick(null);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Show Off " + off.getOffName());
            newStage.setResizable(false);
            processor.setMyStage(newStage);
            parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.getIcons().add(new Image(Main.class.getResourceAsStream("Offs Icon.png")));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLogProducts(ActionEvent actionEvent) {
        Log log = (Log) ((TableViewProcessor)parentProcessor).selectedItem;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TableViewMenu.fxml"));
        try {
            Parent root = loader.load();
            TableViewProcessor<ProductOfLog> processor = loader.getController();
            processor.setLog(log);
            processor.initProcessor(TableViewType.PRODUCTS_OF_LOG);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Log Products");
            this.parentProcessor.parentProcessor.addSubStage(stage);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("Product Icon.png")));
            stage.show();
        } catch (IOException e) {
            //:)
        }
    }

    private void setLog(Log log) {
        this.selectedLog = log;
    }

    public void showDiscountCustomer(ActionEvent actionEvent) {
        Discount discount = (Discount)((TableViewProcessor)parentProcessor).selectedItem;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("DiscountMenu.fxml"));
        try {
            Parent root = loader.load();
            SaleProcessor processor = loader.getController();
            processor.setDiscount(discount);
            processor.discountInfoMouseClicked(null);
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Show Discount " + discount.getCode());
            newStage.setResizable(false);
            processor.parentProcessor = this.parentProcessor.parentProcessor;
            processor.setMyStage(newStage);
            this.parentProcessor.parentProcessor.addSubStage(newStage);
            newStage.getIcons().add(new Image(Main.class.getResourceAsStream("discount menu customer.png")));
            newStage.show();
        } catch (IOException e) {
            //:)
        }
    }

}
