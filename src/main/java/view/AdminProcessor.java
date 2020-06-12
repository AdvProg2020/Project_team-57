package view;

import com.jfoenix.controls.JFXButton;
import controller.Control;
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
    private Stage myStage;
    private ArrayList<Stage> subStages = new ArrayList<>();
    private ArrayList<JFXButton> buttons = new ArrayList<>();
    private AdminProcessor parentProcessor;


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

    public void personalInfo(MouseEvent mouseEvent) {

    }

    public void marketStats(MouseEvent mouseEvent) {
    }

    public void setOptions(ActionEvent actionEvent) {
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
        if (canOpenSubStage(title)) {
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

/*    private void openManageAccountsStage(String title, Account.AccountType accountType) {
        if(canOpenSubStage(title)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewAccounts.fxml"));
                Parent root = loader.load();
                ViewAccountsProcessor viewAccountsProcessor = loader.getController();
                viewAccountsProcessor.initProcessor(accountType);
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                viewAccountsProcessor.setMyStage(newStage);
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("view accounts icon.png")));
                newStage.setResizable(false);
                newStage.setTitle(title);
                parentProcessor.addSubStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private boolean canOpenSubStage(String title) {
        for (Stage subStage : parentProcessor.getSubStages()) {
            if(subStage.getTitle().equals(title)){
                return false;
            }
        }
        return true;
    }

    public void setMyStage(Stage myStage) {
        this.subStages = new ArrayList<>();
        this.myStage = myStage;
        this.myStage.setOnCloseRequest(event -> {
            for (Stage subStage : this.subStages) {
                subStage.close();
            }
        });
    }

    public void addSubStage(Stage subStage) {
        this.subStages.add(subStage);
        subStage.setOnCloseRequest(event -> {
            this.removeSubStage(subStage);
        });
    }

    private void removeSubStage(Stage subStage) {
        this.subStages.removeIf(stage -> {
            return stage.getTitle().equals(subStage.getTitle());
        });
    }

    public ArrayList<Stage> getSubStages() {
        return subStages;
    }

    public void setParentProcessor(AdminProcessor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }

    public void manageCategories(MouseEvent mouseEvent) {
        if(canOpenSubStage("Manage Categories")) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("CategoriesMenu.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.getIcons().add(new Image(getClass().getResourceAsStream("categories icon.png")));
                newStage.setResizable(false);
                newStage.setTitle("Manage Categories");
                parentProcessor.addSubStage(newStage);
                CategoryProcessor.setCategoriesStage(newStage);
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
