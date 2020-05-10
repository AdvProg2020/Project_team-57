package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AdminControl;
import controller.product.ProductControl;
import model.existence.Product;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.menu.ProductMenu;

import javax.swing.plaf.FontUIResource;
import java.util.HashMap;

public class ProductProcessor extends ListicOptionProcessor{
    private static ProductProcessor productProcessor = null;
    private static ProductControl productControl = ProductControl.getController();
    private static AdminControl adminControl = AdminControl.getController();

    private ProductProcessor(){
        this.functionsHashMap = new HashMap<>();
        functionsHashMap.put("Remove Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeProduct(objects);
            }
        });
        functionsHashMap.put("Edit Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editProduct(objects);
            }
        });
        functionsHashMap.put("Accept Adding Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return acceptAddingProduct(objects);
            }
        });
        functionsHashMap.put("Decline Adding Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return declineAddingProduct(objects);
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
        //TODO(OTHERS)
        if(json.contains("Manage All Products Listic Menu") || json.contains("Manage Add Product Requests Listic Menu")) {
            productMenu.setProduct(productControl.getProductById(ID));
        } else {
            productMenu.setProduct(productControl.getEditedProductByID(ID));
        }
        return productMenu;
    }

    public Menu editProduct(Object... objects){
        //Todo
        Object[] parameters = objects.clone();
        ProductMenu productMenu = (ProductMenu)objects[0];
        Product product = (Product)objects[1];
        EditProductProcessor.getInstance(productMenu, product);
        return EditProductProcessor.getInstance().editProductMenuManage(product);
    }

    public void viewBuyers(Object... objects){
        //Todo
    }

    public Menu removeProduct(Object... objects){
        Object[] parameters = objects.clone();
        System.out.println(productControl.removeProductById(((Product)parameters[1]).getID()).getMessage());
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu acceptAddingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        System.out.println(adminControl.approveProductByID(((Product)parameters[1]).getID()).getMessage());
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu declineAddingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        productControl.removeProductById(((Product)parameters[1]).getID());
        System.out.println("Request Declined Successfully.");
        return ((ProductMenu)parameters[0]).getParentMenu();
    }
}
