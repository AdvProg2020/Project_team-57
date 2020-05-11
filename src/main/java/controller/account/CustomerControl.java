package controller.account;

import controller.Control;
import model.db.CartTable;
import model.db.ProductTable;
import model.existence.Product;
import notification.Notification;
import view.menu.Menu;

import java.sql.SQLException;
import java.util.ArrayList;

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
            if(CartTable.isThereCartProductForUsername(username, id)) {
                CartTable.deleteCartProduct(username, id);
            }
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
            if(CartTable.isThereCartProductForUsername(username, id)) {
                CartTable.deleteCartProduct(username, id);
            }
            CartTable.addToCartUnCountable(username, id, amount);
            return Notification.ADD_TO_CART;
        } catch (SQLException e) {
            e.printStackTrace();
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public ArrayList<String> getCartProductNames() {
        ArrayList<String> cartProducts = new ArrayList<>();
        try {
            for(Product product : CartTable.getAllCartWithUsername(Control.getUsername()))
            {
                cartProducts.add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cartProducts;
    }

    public ArrayList<String> getCartProductIDs() {
        ArrayList<String> cartProducts = new ArrayList<>();
        try {
            for(Product product : CartTable.getAllCartWithUsername(Control.getUsername()))
            {
                cartProducts.add(product.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cartProducts;
    }

    public Product getCartProductByID(String ID) {
        try {
            return CartTable.getCartProductByID(Control.getUsername(), ID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Notification increaseCount(String productID, String command) {
        try {
            int input = Integer.parseInt(command);
            if(input < 0) {
                try {
                    if(CartTable.getCartProductByID(Control.getUsername(), productID).getCount() + input <= ProductTable.getProductByID(productID).getCount())
                    {
                        CartTable.increaseCartProductCountable(Control.getUsername(), productID, input);
                        return Notification.INCREASED;
                    }
                    return Notification.MORE_THAN_INVENTORY_COUNTABLE;
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) { } catch (NullPointerException e) { }
        return Notification.INVALID_COUNT;
    }

    public Notification increaseAmount(String productID, String command) {
        try {
            double input = Double.parseDouble(command);
            if(input < 0)
            {
                try {
                    if(CartTable.getCartProductByID(Control.getUsername(), productID).getAmount() + input <= ProductTable.getProductByID(productID).getAmount())
                    {
                        CartTable.increaseCartProductUnCountable(Control.getUsername(), productID, input);
                        return Notification.INCREASED;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return Notification.MORE_THAN_INVENTORY_UNCOUNTABLE;
            }
        } catch (NumberFormatException e) {} catch (NullPointerException e) {}
        return Notification.INVALID_AMOUNT;
    }

    public double calculateCartTotalPrice() {
        double totalPrice = 0;
        try {
            for (Product product : CartTable.getAllCartWithUsername(Control.getUsername())) {
                if(product.isCountable())
                    totalPrice += product.getPrice() * product.getCount();
                else
                    totalPrice += product.getPrice() * product.getAmount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return totalPrice;
    }
}
