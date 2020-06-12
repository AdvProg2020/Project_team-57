package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controller.Control;
import controller.product.ProductControl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.existence.Category;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CategoryProcessor implements Initializable {
    public ProductControl productControl = ProductControl.getController();

    public Pane mainPane;
    public TreeTableView<Category> categoriesTableView;
    public JFXButton addSubCategoryButton, editCategoryButton, deleteCategoryButton;
    
    private static Stage addSubCategoryStage = null, editCategoryStage = null;
    private static Category parentCategory = null;
    private static String categorySubMenuName = null;

    public JFXTextField nameTextField, featuresTextField;
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
                featuresTextField.setText(parentCategory.getFeatures());
                addCategoryButton.setText("Edit");
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
        if(addSubCategoryStage != null) {
            if(categoriesTableView.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You haven't selected Any Category");
                alert.setTitle("Jesus");
                alert.setHeaderText("Hey You");
                alert.show();
            } else {
                parentCategory = categoriesTableView.getSelectionModel().getSelectedItem().getValue();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("addSubCategoryMenu.fxml"));
                Parent root = loader.load();
                addSubCategoryStage = new Stage();
                addSubCategoryStage.setScene(new Scene(root));
                categorySubMenuName = subStageName;
                addSubCategoryStage.show();
            }
        }
    }

    public void deleteCategoryMouseClicked(MouseEvent mouseEvent) {
        //Todo
    }

    public void addCategory(MouseEvent mouseEvent) {
        switch (categorySubMenuName) {
            case "Add Category Menu" :
                //Todo
                break;
            case "Edit Category Menu" :
                //Todo
                break;
            default:
                System.out.println("Shit. Error In Add Category Button");
        }
    }
}
