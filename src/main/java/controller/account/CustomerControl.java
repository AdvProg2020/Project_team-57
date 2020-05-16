package controller.account;

import controller.Control;
import model.db.*;
import model.existence.Account;
import model.existence.Discount;
import model.existence.Off;
import model.existence.Product;
import notification.Notification;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerControl extends AccountControl{
    private static CustomerControl customerControl = null;
    private boolean hasDiscount = false;
    private Discount discount = null;

    public static CustomerControl getController() {
        if (customerControl == null)
            customerControl = new CustomerControl();

        return customerControl;
    }

    public Notification addToCartCountable(String username, String id, int count){
        try {
            if (ProductTable.getProductByID(id).getCount() < count)
                return Notification.MORE_THAN_INVENTORY_COUNTABLE;
            if (count <= 0)
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
            if (amount <= 0)
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
            if(input > 0) {
                try {
                    if(CartTable.getCartProductByID(Control.getUsername(), productID).getCount() + input <= ProductTable.getProductByID(productID).getCount())
                    {
                        CartTable.modifyCartProductCounts(Control.getUsername(), productID, input);
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

    public Notification decreaseCount(String productID, String command) {
        try {
            int input = Integer.parseInt(command);
            if(input > 0) {
                try {
                    if(CartTable.getCartProductByID(Control.getUsername(), productID).getCount() - input > 0)
                    {
                        CartTable.modifyCartProductCounts(Control.getUsername(), productID, ((-1) * input));
                        return Notification.DECREASED;
                    }
                    return Notification.MORE_THAN_CART_COUNTABLE;
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
            //System.out.println("Command = " + command);
            double input = Double.parseDouble(command);
            if(input > 0)
            {
                try {
                    if(CartTable.getCartProductByID(Control.getUsername(), productID).getAmount() + input <= ProductTable.getProductByID(productID).getAmount())
                    {
                        CartTable.modifyCartProductAmount(Control.getUsername(), productID, input);
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

    public Notification decreaseAmount(String productID, String command) {
        try {
            //System.out.println("Command = " + command);
            double input = Double.parseDouble(command);
            if(input > 0)
            {
                try {
                    if(CartTable.getCartProductByID(Control.getUsername(), productID).getAmount() - input > 0)
                    {
                        CartTable.modifyCartProductAmount(Control.getUsername(), productID, ((-1) * input));
                        return Notification.DECREASED;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return Notification.MORE_THAN_CART_UNCOUNTABLE;
            }
        } catch (NumberFormatException e) {} catch (NullPointerException e) {}
        return Notification.INVALID_AMOUNT;
    }

    public double calculateCartTotalPrice() {
        double totalPrice = 0;
        try {
            for (Product product : CartTable.getAllCartWithUsername(Control.getUsername())) {
                double newPrice;
                if(!OffTable.isThereProductInOff(product.getID()))
                    newPrice = product.getPrice();
                else
                    newPrice = product.getPrice() -
                            (product.getPrice() * OffTable.getOffByProductID(product.getID()).getOffPercent() / 100);
                if(product.isCountable())
                    totalPrice += newPrice * product.getCount();
                else
                    totalPrice += newPrice * product.getAmount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return totalPrice;
    }

    public Notification removeFromCartByID(String id) {
        try {
            if(CartTable.isThereCartProductForUsername(Control.getUsername(), id))
            {
                CartTable.deleteCartProduct(Control.getUsername(), id);
                return Notification.CART_PRODUCT_REMOVED;
            }
            return Notification.NOT_YOUR_CART_PRODUCT;
        } catch (SQLException e) { } catch (ClassNotFoundException e) { }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<String> getDiscountCodes() {
        ArrayList<String> discountCodes = new ArrayList<>();
        try {
            DiscountTable.updateDiscountCodes();
            for (Discount discountCode : DiscountTable.getCustomerDiscountCodes(Control.getUsername())) {
                discountCodes.add(discountCode.getCode());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return discountCodes;
    }

    public ArrayList<String> getDiscountIDs() {
        ArrayList<String> discountIDs = new ArrayList<>();
        try {
            for (Discount discountCode : DiscountTable.getCustomerDiscountCodes(Control.getUsername())) {
                discountIDs.add(discountCode.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return discountIDs;
    }

    public ArrayList<String> getAllShowingOffNames(){
        ArrayList<String> allOffNames = new ArrayList<>();
        try {
            for (Off off : OffTable.getAllShowingOffs()) {
                allOffNames.add(off.getOffName());
            }
            return allOffNames;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getAllShowingOffIDs(){
        ArrayList<String> allOffIds = new ArrayList<>();
        try {
            for (Off off : OffTable.getAllShowingOffs()) {
                allOffIds.add(off.getOffID());
            }
            return allOffIds;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Notification purchase()
    {
        try {
            double initPrice = 0; double offPrice = 0; double finalPrice;
            for (Product product : CartTable.getAllCartWithUsername(Control.getUsername())) {
                if(product.getStatus() != 1)
                    return Notification.UNAVAILABLE_CART_PRODUCT;
                if(product.isCountable()) {
                    if(product.getCount() > ProductTable.getProductByID(product.getID()).getCount())
                        return Notification.CART_PRODUCT_OUT_OF_STOCK;
                } else {
                    if(product.getAmount() > ProductTable.getProductByID(product.getID()).getAmount())
                        return Notification.CART_PRODUCT_OUT_OF_STOCK;
                }
                if(OffTable.isThereProductInOff(product.getID())) {
                    offPrice += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100)) * product.getPrice();
                } else {
                    offPrice += product.getPrice();
                }
                initPrice += product.getPrice();
            }
            finalPrice = calculateFinalPrice(hasDiscount, discount, offPrice);
            return affordability(initPrice, offPrice, finalPrice);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    private Notification affordability(double initPrice, double offPrice, double finalPrice) {
        try {
            Account customer = AccountTable.getAccountByUsername(Control.getUsername());
            if(finalPrice > customer.getCredit())
                return Notification.CANT_AFFOARD_CART;
            AccountTable.changeCredit(customer.getUsername(),((-1) * finalPrice));
            giveCreditToVendors(customer.getUsername());
            if(hasDiscount) {
                DiscountTable.addRepetitionToDiscount(discount, customer.getUsername());
                if(discount.getMaxRepetition() <= discount.getCustomersWithRepetition().get(customer.getUsername()))
                    DiscountTable.removeDiscountCodeForUsername(discount.getID(),customer.getUsername());
            }
            DiscountTable.updateDiscountCodesTime();
            return Notification.PURCHASED_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    private void giveCreditToVendors(String customerUsername) {
        try {
            for (Product product : CartTable.getAllCartWithUsername(customerUsername)) {
                double price = 0;
                if(OffTable.isThereProductInOff(product.getID())) {
                    price = (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100)) * product.getPrice();
                } else {
                    price = product.getPrice();
                }
                AccountTable.changeCredit(product.getSellerUserName(), price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private double calculateFinalPrice(boolean hasDiscount, Discount discount, double offPrice) {
        if(!hasDiscount)
            return offPrice;
        double discountVal = (discount.getDiscountPercent()/100) * offPrice;
        if(discountVal <= discount.getMaxDiscount())
            return offPrice - discountVal;
        return offPrice - discount.getMaxDiscount();
    }

    public void setDiscount(String discountID) {
        try {
            this.discount = DiscountTable.getDiscountByID(discountID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }
}
