package controller.account;


import model.db.EditingProductTable;
import model.db.ProductTable;
import model.db.VendorTable;
import model.existence.Product;
import notification.Notification;

import java.sql.SQLException;

public class AdminControl extends AccountControl{
    private static AdminControl adminControl = null;

    public static AdminControl getController() {
        if (adminControl == null)
            adminControl = new AdminControl();

        return adminControl;
    }

    public Notification approveProductByID(String id){
        try {
            ProductTable.setProductStatus(id, 1);
            return Notification.ACCEPT_ADDING_PRODUCT;
        } catch (SQLException throwable) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException throwable) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification acceptEditingProductByID(String editingProductID) {
        try {
            Product editingProduct = EditingProductTable.getEditingProductWithID(editingProductID);
            EditingProductTable.removeProductById(editingProductID);
            editingProduct.setStatus(1);
            ProductTable.removeProductByID(editingProduct.getID());
            if(editingProduct.isCountable())
                VendorTable.addCountableProduct(editingProduct, editingProduct.getSellerUserName());
            else
                VendorTable.addUnCountableProduct(editingProduct, editingProduct.getSellerUserName());
            return Notification.ACCEPT_EDITING_PRODUCT;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }
}
