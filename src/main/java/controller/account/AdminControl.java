package controller.account;

import model.db.ProductTable;
import model.existence.Product;

import java.util.ArrayList;

public class AdminControl extends AccountControl{
    private static AdminControl adminControl = null;

    public static AdminControl getController() {
        if (adminControl == null)
            adminControl = new AdminControl();

        return adminControl;
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

}
