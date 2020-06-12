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
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.existence.Category;
import notification.Notification;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoryProcessor implements Initializable {
    public ProductControl productControl = ProductControl.getController();
    public AdminControl adminControl = AdminControl.getController();

    public Pane mainPane;
    public TreeTableView<Category> categoriesTableView;
    public JFXButton addSubCategoryButton, editCategoryButton, deleteCategoryButton;
    
    private static Stage categoriesStage = null, addSubCategoryStage = null;
    private static Category parentCategory = null;
    private static String categorySubMenuName = null;

    public JFXTextField nameTextField, parentNameTextField, featuresTextField;
    public JFXButton addCategoryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Todo
        String locationFile = location.getFile();

        if(locationFile.contains("CategoriesMenu")) {
            categoriesTableView.setRoot(productControl.getCategoryTableRoot());

            if (!Control.getType().equals("Admin")) {
                mainPane.getChildren().remove(addCategoryButton);
                mainPane.getChildren().remove(editCategoryButton);
                mainPane.getChildren().remove(deleteCategoryButton);
            }
        } else if(locationFile.contains("addSubCategoryMenu")) {
            if(categorySubMenuName.equals("Edit Category Menu")) {
                nameTextField.setText(parentCategory.getName());
                parentNameTextField.setText(parentCategory.getParentCategory());
                featuresTextField.setText(parentCategory.getFeatures());
                addCategoryButton.setText("Edit");
            } else if(categorySubMenuName.equals("Add Category Menu")) {
                parentNameTextField.setText(parentCategory.getName());
                parentNameTextField.setDisable(true);
                parentNameTextField.setEditable(false);
            }
        }
    }


    public void addSubCategoryMouseClicked(MouseEvent mouseEvent) throws IOException {
        setSubStage("Add Category Menu");
    }

    public void editCategoryMouseClicked(MouseEvent mouseEvent) throws IOException {
        setSubStage("Edit Category Menu");
    }

    public void setSubStage(String subStageName) throws IOException {
        if(addSubCategoryStage == null) {
            if(categoriesTableView.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You haven't selected Any Category");
                alert.setTitle("Jesus");
                alert.setHeaderText("Hey You");
                alert.show();
            } else {
                categorySubMenuName = subStageName;
                parentCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("addSubCategoryMenu.fxml"));
                Parent root = loader.load();
                addSubCategoryStage = new Stage();
                addSubCategoryStage.setScene(new Scene(root));
                addSubCategoryStage.setResizable(false);
                addSubCategoryStage.setOnCloseRequest(event -> addSubCategoryStage = null);
                addSubCategoryStage.show();
            }
        }
    }


    public void deleteCategoryMouseClicked(MouseEvent mouseEvent) {
        Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();
        Notification notification = adminControl.removeCategory(selectedCategory);
        notification.getAlert().show();

        if(notification.equals(Notification.CATEGORY_DELETED))
            categoriesTableView.setRoot(productControl.getCategoryTableRoot());
    }


    public void addCategory(MouseEvent mouseEvent) {
        switch (categorySubMenuName) {
            case "Add Category Menu" :
                addCreatedCategory();
                break;
            case "Edit Category Menu" :
                editCategory();
                break;
            default:
                System.out.println("Shit. Error In Add Category Button");
        }
    }

    private void addCreatedCategory() {
        Category category = createCategoryWithFields();

        Notification notification = adminControl.addCategory(category);

        if(notification.equals(Notification.CATEGORY_ADDED)) {
            Optional<ButtonType> optionalButtonType = notification.getAlert().showAndWait();
            if(optionalButtonType.get() == ButtonType.OK) {
                closeSubStage();
            }
        } else {
            notification.getAlert().show();
        }

    }

    private void editCategory() {
        Category category = createCategoryWithFields();

        Alert alert = null;
        alert = editField(parentCategory, category, "Name", alert, nameTextField);
        alert = editField(parentCategory, category, "Parent Name", alert, parentNameTextField);
        alert = editField(parentCategory, category, "Features", alert, featuresTextField);

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

        if(!alert.getTitle().equals("Modify Successful"))
            textField.setStyle("-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;");

        if(previousAlert == null || previousAlert.getTitle().equals("Modify Successful"))
            return alert;
        else
            return previousAlert;
    }

    private Category createCategoryWithFields() {
        Category category = new Category();
        category.setName(nameTextField.getText());
        category.setFeatures(featuresTextField.getText());
        category.setParentCategory(parentNameTextField.getText());
        return category;
    }


    private void closeSubStage() {
        addSubCategoryStage.close();
        addSubCategoryStage = null;
        parentCategory = null;
        categorySubMenuName = null;
        updateTreeTableOutSideOfStage();
    }

    private void updateTreeTableOutSideOfStage() {
        ObservableList<Node> nodes = categoriesStage.getScene().getRoot().getChildrenUnmodifiable();
        TreeTableView<Category> treeTableView = (TreeTableView<Category>) nodes.get(0);
        treeTableView.setRoot(productControl.getCategoryTableRoot());
    }

    public static void setCategoriesStage(Stage categoriesStage) {
        CategoryProcessor.categoriesStage = categoriesStage;
    }
}
