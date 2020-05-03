package controller.product;

import controller.Control;
import notification.Notification;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductControl extends Control {
    private static ProductControl productControl = null;


    public Product getProductById(String productId) {
        try {
            return ProductTable.getProductById(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Notification removeProductById(String productId) {
        try {
            ProductTable.removeProductById(productId);
            return Notification.REMOVE_PRODUCT_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public void setProductLists(ArrayList<String> productName, ArrayList<String> productId){
        for (Product product : ProductTable.getAllProducts()){
            productName.add(product.getName());
            productId.add(product.getId());
        }
    }

    public static ProductControl getController(){
        if (productControl == null)
            productControl = new ProductControl();
        return productControl;
    }
}
