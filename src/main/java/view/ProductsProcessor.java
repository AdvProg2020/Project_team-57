package view;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductsProcessor implements Initializable {

    public ScrollPane productsScrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setScrollPane();
    }

    private void setScrollPane() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ProductsPage.fxml"));
            Parent root = loader.load();
            productsScrollPane.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
