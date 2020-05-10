package controller.account;

import model.db.CartTable;
import model.db.ProductTable;
import notification.Notification;

import java.sql.SQLException;

public class CustomerControl extends AccountControl{
    private static CustomerControl customerControl = null;

    public static CustomerControl getController() {
        if (customerControl == null)
            customerControl = new CustomerControl();

        return customerControl;
    }

    public Notification addToCartCountable(String username, String id, int count){
        try {
            if (ProductTable.getProductByID(id).getCount() < count)
                return Notification.MORE_THAN_INVENTORY_COUNTABLE;
            if (count < 0)
                return Notification.NEGATIVE_NUMBER;
            CartTable.addToCartCountable(username, id, count);
            return Notification.ADD_TO_CART;
        } catch (SQLException throwable) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification addToCartUnCountable(String username, String id, double amount){
        try {
            if (ProductTable.getProductByID(id).getAmount() < amount)
                return Notification.MORE_THAN_INVENTORY_UNCOUNTABLE;
            if (amount < 0)
                return Notification.NEGATIVE_NUMBER;
            CartTable.addToCartUnCountable(username, id, amount);
            return Notification.ADD_TO_CART;
        } catch (SQLException throwable) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

}
