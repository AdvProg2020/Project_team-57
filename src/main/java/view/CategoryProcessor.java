package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.account.AdminControl;
import controller.product.ProductControl;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.db.CategoryTable;
import model.existence.Category;
import notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoryProcessor extends Processor implements Initializable {
    public ProductControl productControl = ProductControl.getController();
    public AdminControl adminControl = AdminControl.getController();

    public Pane mainPane;
    public TreeTableView<Category> categoriesTableView;
    public JFXButton addSubCategoryButton, editCategoryButton, deleteCategoryButton;
    
    private Category category, oldCategory;

    public JFXTextField nameTextField, parentNameTextField, featuresTextField;
    public JFXButton addCategoryButton;

    //Todo
    private static String errorTextFieldStyle = "-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Todo
        String locationFile = location.getFile();

        if(locationFile.contains("CategoriesMenu")) {
            initCategoriesTableRow();
            categoriesTableView.setRoot(productControl.getCategoryTableRoot());

            if (!Control.getType().equals("Admin")) {
                mainPane.getChildren().remove(addCategoryButton);
                mainPane.getChildren().remove(editCategoryButton);
                mainPane.getChildren().remove(deleteCategoryButton);
            }
            categoriesTableView.getSelectionModel().selectFirst();
            initButtons();
        }
    }

    private void initCategoriesTableRow() {
        categoriesTableView.setRowFactory(tv -> {
            TreeTableRow<Category> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                initButtons();
            });
            return row ;
        });
    }

    private void setSubStageFields() {
        if(category.getName() == null) {
            parentNameTextField.setText(category.getParentCategory());
            parentNameTextField.setDisable(true);
            parentNameTextField.setEditable(false);
        } else {
            nameTextField.setText(category.getName());
            parentNameTextField.setText(category.getParentCategory());
            featuresTextField.setText(category.getFeatures());
            addCategoryButton.setText("Edit");
        }
    }

    public void initButtons() {
        if(categoriesTableView.getSelectionModel().getSelectedItem() != null) {
            if (categoriesTableView.getSelectionModel().getSelectedItem().getValue().getName().equals("All Products")) {
                deleteCategoryButton.setDisable(true);
                editCategoryButton.setDisable(true);
            } else {
                deleteCategoryButton.setDisable(false);
                editCategoryButton.setDisable(false);
            }
        }
    }


    public void addSubCategoryMouseClicked(MouseEvent mouseEvent) throws IOException {
        Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();
        setSubStage("Add SubCategory " + selectedCategory.getName());
    }

    public void editCategoryMouseClicked(MouseEvent mouseEvent) throws IOException {
        Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();
        setSubStage("Edit Category " + selectedCategory.getName());
    }

    public void setSubStage(String subStageName) throws IOException {
        if(canOpenSubStage(subStageName, this)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addSubCategoryMenu.fxml"));
            Parent root = loader.load();

            CategoryProcessor categoryProcessor = loader.getController();
            categoryProcessor.parentProcessor = this;
            Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();

            if(subStageName.substring(0, 15).equals("Add SubCategory")) {
                categoryProcessor.category = new Category();
                categoryProcessor.category.setParentCategory(selectedCategory.getName());
            } else {
                categoryProcessor.oldCategory = selectedCategory;
                categoryProcessor.category = selectedCategory.clone();
            }

            categoryProcessor.setSubStageFields();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle(subStageName);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("Manage Category Menu.png")));

            categoryProcessor.setMyStage(stage);
            addSubStage(stage);
            stage.show();
        }
    }


    public void deleteCategoryMouseClicked(MouseEvent mouseEvent) {
            Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();
            Notification notification = adminControl.removeCategory(selectedCategory);
            notification.getAlert().show();

            if(notification.equals(Notification.CATEGORY_DELETED)) {
                categoriesTableView.setRoot(productControl.getCategoryTableRoot());
                categoriesTableView.getSelectionModel().selectFirst();
                initButtons();
            }
    }


    public void addCategory(MouseEvent mouseEvent) {
        if(category.getName() == null) {
            addCreatedCategory();
        } else {
            editCategory();
        }
    }

    private void addCreatedCategory() {
        createCategoryWithFields();

        Notification notification = adminControl.addCategory(category);

        if(notification.equals(Notification.CATEGORY_ADDED)) {
            Optional<ButtonType> optionalButtonType = notification.getAlert().showAndWait();
            if(optionalButtonType.get() == ButtonType.OK) {
                closeSubStage();
            }
        } else {
            notification.getAlert().show();

            switch (notification) {
                case INVALID_CATEGORY_NAME:
                case DUPLICATE_CATEGORY_NAME:
                    nameTextField.setStyle(errorTextFieldStyle);
                    break;
                case INVALID_FEATURES:
                    featuresTextField.setStyle(errorTextFieldStyle);
                    break;
                case PARENT_CATEGORY_NOT_FOUND:
                    parentNameTextField.setStyle(errorTextFieldStyle);
                    break;
                default:
                    System.out.println("Serious Shit In Adding Category");
            }

            category = new Category();
        }

    }

    private void editCategory() {
        createCategoryWithFields();

        Alert alert = null;
        alert = editField(oldCategory, category, "Name", alert, nameTextField);
        alert = editField(oldCategory, category, "Parent Name", alert, parentNameTextField);
        alert = editField(oldCategory, category, "Features", alert, featuresTextField);

        if(alert.getTitle().equals("Modify Successful")) {
            Optional<ButtonType> optionalButtonType = alert.showAndWait();
            if(optionalButtonType.get() == ButtonType.OK) {
                closeSubStage();
            }
        } else {
            alert.show();
        }
    }

    private Alert editField(Category oldCategory, Category newCategory, String fieldName,
                            Alert previousAlert, JFXTextField textField) {
        Alert alert = adminControl.editCategory(oldCategory, newCategory, fieldName).getAlert();

        if(alert.getTitle().equals("Modify Successful"))
            textField.setStyle("");
        else
            textField.setStyle(errorTextFieldStyle);

        if(previousAlert == null || previousAlert.getTitle().equals("Modify Successful"))
            return alert;
        else
            return previousAlert;
    }

    private void createCategoryWithFields() {
        category.setName(nameTextField.getText());
        category.setFeatures(featuresTextField.getText());
        category.setParentCategory(parentNameTextField.getText());
    }


    private void closeSubStage() {
        myStage.close();
        parentProcessor.removeSubStage(myStage);
        ((CategoryProcessor)parentProcessor).categoriesTableView.getSelectionModel().selectFirst();
        ((CategoryProcessor)parentProcessor).initButtons();
        ((CategoryProcessor)parentProcessor).categoriesTableView.setRoot(productControl.getCategoryTableRoot());
    }

    public void textFieldMouseClicked(MouseEvent mouseEvent) {
        JFXTextField textField = (JFXTextField)mouseEvent.getSource();
        textField.setStyle("");
    }
}
