package view.process;

import com.google.gson.GsonBuilder;
import controller.product.ProductControl;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.menu.ProductMenu;

import javax.swing.plaf.FontUIResource;
import java.util.HashMap;

public class ProductProcessor extends ListicOptionProcessor{
    private static ProductProcessor productProcessor = null;
    private static ProductControl productControl = ProductControl.getController();

    private ProductProcessor(){
        this.functionsHashMap = new HashMap<>();
        functionsHashMap.put("Edit Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeProduct(objects);
            }
        });

    }

    public static ProductProcessor getInstance(){
        if(productProcessor == null)
            productProcessor = new ProductProcessor();

        return productProcessor;
    }

    public static ProductMenu setMenu(String json, String ID){
        ProductMenu productMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ProductMenu.class);
        productMenu.setProduct(productControl.getProductById(ID));
        return productMenu;
    }

    public void editProduct(Object... objects){
        //Todo
    }

    public void viewBuyers(Object... objects){
        //Todo
    }

    public Menu removeProduct(Object... objects){
        Object[] parameters = objects.clone();
        System.out.println(productControl.removeProductById((String)parameters[1]).getMessage());
        return (Menu)parameters[0];
    }
}
