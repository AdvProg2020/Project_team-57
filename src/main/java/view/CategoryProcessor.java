package view;

import com.jfoenix.controls.JFXButton;
import controller.Control;
import controller.product.ProductControl;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryProcessor implements Initializable {
    public ProductControl productControl = ProductControl.getController();

    public Pane mainPane;
    public TreeTableView categoriesTableView;
    public JFXButton addCategoryButton, deleteCategoryButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Todo
        categoriesTableView.setRoot(productControl.getCategoryTableRoot());

        if(!Control.getType().equals("Admin")) {
            mainPane.getChildren().remove(addCategoryButton);
            mainPane.getChildren().remove(deleteCategoryButton);
        }
    }
}
