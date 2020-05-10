package controller.account;


import model.db.ProductTable;
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

}
