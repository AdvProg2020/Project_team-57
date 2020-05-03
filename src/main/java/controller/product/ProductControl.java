package controller.product;

import controller.Control;
import model.db.ProductTable;
import model.existence.Product;
import notification.Notification;

import java.util.ArrayList;

public class ProductControl extends Control {
    private static ProductControl productControl = null;


    public Product getProductById(String productId) {
        try {
            return ProductTable.getProductByID(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Notification removeProductById(String productId) {
        try {
            ProductTable.removeProductByID(productId);
            return Notification.REMOVE_PRODUCT_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public void setProductLists(ArrayList<String> productName, ArrayList<String> productId) {
        try {
            for (Product product : ProductTable.getAllProducts()) {
                productName.add(product.getName());
                productId.add(product.getID());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ProductControl getController(){
        if (productControl == null)
            productControl = new ProductControl();
        return productControl;
    }
}
