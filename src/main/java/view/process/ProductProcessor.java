package view.process;

import com.google.gson.GsonBuilder;
import controller.product.ProductControl;
import view.menu.ProductMenu;

public class ProductProcessor extends ListicOptionProcessor{
    private static ProductProcessor productProcessor = null;
    private static ProductControl productControl = ProductControl.getController();

    private ProductProcessor(){

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

    public void editProduct(){

    }

    public void viewBuyers(){

    }

    public void removeProduct(String productID){
        System.out.println(productControl.removeProductById(productID).getMessage());
    }
}
