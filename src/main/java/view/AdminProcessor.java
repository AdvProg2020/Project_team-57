package view;

import com.jfoenix.controls.JFXButton;
import controller.Control;
import controller.account.AdminControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.existence.Account;
import model.existence.Comment;
import model.existence.Discount;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminProcessor extends AccountProcessor implements Initializable {

    public JFXButton dashboardButton;
    public BorderPane mainPane;
    public JFXButton accountsButton;
    public JFXButton productsButton;
    public JFXButton offsButton;
    public Label usernameLabel;
    public JFXButton mainMenuButton;
    public Pane manageCustomers;
    public Pane manageVendors;
    public Pane manageAdmins;
    private ArrayList<JFXButton> buttons = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(location.toString().contains("AdminMenu")) {
            initButtons();
            selectThisButton(dashboardButton);
            initLabelsForUsername();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
            Parent subRoot = null;
            try {
                subRoot = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loader.setController(this);
            mainPane.setCenter(subRoot);
            AdminControl.getController().createDiscountAddedUsers();
        }
    }

    private void initLabelsForUsername() {
        usernameLabel.setText(Control.getUsername());
    }

    private void initButtons() {
        buttons.add(dashboardButton);
        buttons.add(accountsButton);
        buttons.add(productsButton);
        buttons.add(offsButton);
        buttons.add(mainMenuButton);
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

    public void marketStats(MouseEvent mouseEvent) {
    }

    public void setOptions(ActionEvent actionEvent) {
        //System.out.println(this);
        JFXButton selectedButton = (JFXButton) actionEvent.getSource();
        selectThisButton(selectedButton);
        try {
            if (selectedButton.equals(dashboardButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
                Parent subRoot = loader.load();
                ((AdminProcessor)loader.getController()).setParentProcessor(this);
                mainPane.setCenter(subRoot);
            } else if (selectedButton.equals(accountsButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminAccounts.fxml"));
                Parent subRoot = loader.load();
                ((AdminProcessor)loader.getController()).setParentProcessor(this);
                mainPane.setCenter(subRoot);
            } else if(selectedButton.equals(productsButton)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminProducts.fxml"));
                Parent subRoot = loader.load();
                ((AdminProcessor)loader.getController()).setParentProcessor(this);
                mainPane.setCenter(subRoot);
            } else if(selectedButton.equals(offsButton)) {
                //System.out.println(this);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminOffs.fxml"));
                Parent subRoot = loader.load();
                ((AdminProcessor)loader.getController()).setParentProcessor(this);
                mainPane.setCenter(subRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void manageAccounts(MouseEvent mouseEvent) {
        String title = "";
        Account.AccountType accountType = Account.AccountType.ADMIN;
        switch (((Pane)mouseEvent.getSource()).getId()) {
            case "manageCustomers" :
                accountType = Account.AccountType.CUSTOMER;
                title = "Customers";
                break;
            case "manageVendors" :
                accountType = Account.AccountType.VENDOR;
                title = "Vendors";
                break;
            case "manageAdmins" :
                accountType = Account.AccountType.ADMIN;
                title = "Admins";
                break;
        }
        title += " View";
        openManageAccountsStage(title, accountType);
    }

    private void openManageAccountsStage(String title, Account.AccountType accountType) {
        if (canOpenSubStage(title, parentProcessor)) {
            try {
                TableViewProcessor.TableViewType tableViewType;
                switch (accountType) {
                    case ADMIN:
                        tableViewType = TableViewProcessor.TableViewType.ADMINS;
                        break;
                    case VENDOR:
                        tableViewType = TableViewProcessor.TableViewType.VENDORS;
                        break;
                    default:
                        tableViewType = TableViewProcessor.TableViewType.CUSTOMERS;
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Account> tableViewProcessor = loader.getController();
                tableViewProcessor.initProcessor(tableViewType);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("view accounts icon.png")));
                newStage.setResizable(false);
                newStage.setTitle(title);
                parentProcessor.addSubStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void manageCategories(MouseEvent mouseEvent) {
        if(canOpenSubStage("Manage Categories", parentProcessor)) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("CategoriesMenu.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                //newStage.getIcons().add(new Image(getClass().getResourceAsStream("categories icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Manage Categories");
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("Manage Categories Menu.png")));
                parentProcessor.addSubStage(newStage);

                //Sepehr's Section
                    CategoryProcessor categoryProcessor = loader.getController();
                    categoryProcessor.setMyStage(newStage);
                //Sepehr's Section

                //CategoryProcessor.setCategoriesStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void manageDiscounts(MouseEvent mouseEvent) {
        if (canOpenSubStage("Manage Discounts", parentProcessor)) {
            try {
                //System.out.println(parentProcessor);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Discount> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(parentProcessor);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.DISCOUNTS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                //newStage.getIcons().add(new Image(getClass().getResourceAsStream("view accounts icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Manage Discounts");
                parentProcessor.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void manageComments(MouseEvent mouseEvent) {
        if (canOpenSubStage("Manage Comment Requests", parentProcessor)) {
            try {
                //System.out.println(parentProcessor);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("TableViewMenu.fxml"));
                Parent root = loader.load();
                TableViewProcessor<Comment> tableViewProcessor = loader.getController();
                tableViewProcessor.setParentProcessor(parentProcessor);
                tableViewProcessor.initProcessor(TableViewProcessor.TableViewType.ADMIN_COMMENTS);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                //newStage.getIcons().add(new Image(getClass().getResourceAsStream("view accounts icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Manage Comment Requests");
                parentProcessor.addSubStage(newStage);
                tableViewProcessor.setMyStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
