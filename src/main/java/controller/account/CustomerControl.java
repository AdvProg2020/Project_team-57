package controller.account;

import controller.Control;
import model.db.*;
import model.existence.*;
import notification.Notification;

import java.sql.Date;
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
            DiscountTable.updateDiscountCodesTime();
            //DiscountTable.updateDiscountCodesRep();
            for (Discount discountCode : DiscountTable.getCustomerDiscountCodes(Control.getUsername())) {
                if(discountCode.getMaxRepetition() > discountCode.getCustomersWithRepetition().get(Control.getUsername()))
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

    //-------------------------------------------------PURCHASE-------------------------------------------------//
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
                    offPrice += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100))
                            * product.getPrice() * product.getCount();
                    offPrice += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100))
                            * product.getPrice() * product.getAmount();
                } else {
                    offPrice += product.getPrice() * product.getAmount();
                    offPrice += product.getPrice() * product.getCount();
                }
                initPrice += product.getPrice() * product.getAmount();
                initPrice += product.getPrice() * product.getCount();
            }
            //TODO
            //System.out.println("init price : " + initPrice + "$ off price : " + offPrice + "$");
            finalPrice = calculateFinalPrice(hasDiscount, discount, offPrice);
            //System.out.println("fin pri : " + finalPrice);
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
                return Notification.CANT_AFFORD_CART;
            AccountTable.changeCredit(customer.getUsername(),((-1) * finalPrice));
            giveCreditToVendors(customer.getUsername());
            createLog(customer);
            if(hasDiscount) {
                //System.out.println("Step 1");
                DiscountTable.addRepetitionToDiscount(discount, customer.getUsername());
/*                if(discount.getMaxRepetition() <= discount.getCustomersWithRepetition().get(customer.getUsername()))
                    DiscountTable.removeDiscountCodeForUsername(discount.getID(),customer.getUsername());*/
            }
            reduceProductFromStock(customer.getUsername());
            CartTable.deleteCustomerCart(customer.getUsername());
            hasDiscount = false;
            return Notification.PURCHASED_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    private void reduceProductFromStock(String username) {
        try {
            for (Product product : CartTable.getAllCartWithUsername(username)) {
                if(product.isCountable())
                    ProductTable.reduceProductCount(product.getID(), product.getCount());
                else
                    ProductTable.reduceProductAmount(product.getID(), product.getAmount());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void giveCreditToVendors(String customerUsername) {
        try {
            for (Product product : CartTable.getAllCartWithUsername(customerUsername)) {
                double price = 0;
                if(OffTable.isThereProductInOff(product.getID())) {
                    price += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100)) * product.getPrice() * product.getAmount();
                    price += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent()/100)) * product.getPrice() * product.getCount();
                } else {
                    price += product.getPrice() * product.getCount();
                    price += product.getPrice() * product.getAmount();
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
        //System.out.println("Discount Val : " + discountVal);
        if(discountVal <= discount.getMaxDiscount()) {
            //System.out.println("off - dis : " + (offPrice - discountVal));
            return offPrice - discountVal;
        }
        //System.out.println("off - dis : " + (offPrice - discount.getMaxDiscount()));
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

    private void createLog(Account customer) throws SQLException, ClassNotFoundException {
        Log log = new Log();
        String logID = "";
        do {
            logID = generateLogID();
        } while (LogTable.isThereLogWithID(logID));

        log.setLogID(logID);
        log.setCustomerUsername(customer.getUsername());
        if(hasDiscount)
            log.setDiscountPercent(discount.getDiscountPercent());
        else
            log.setDiscountPercent(0);

        log.setStatus(1);
        log.setDate(new Date(System.currentTimeMillis()));

        ArrayList<Log.ProductOfLog> logProducts = new ArrayList<>();
        for (Product product : CartTable.getAllCartWithUsername(customer.getUsername())) {
            logProducts.add(new Log.ProductOfLog(product));
        }

        log.setAllProducts(logProducts);
        LogTable.addLog(log);
    }

    private String generateLogID()
    {
        char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
        StringBuilder ID = new StringBuilder("l");
        for(int i = 0; i < 7; ++i)
        {
            ID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);
        }
        return ID.toString();
    }

    public Discount getCustomerDiscountByID(String discountID) {
        try {
            return DiscountTable.getDiscountByID(discountID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getAllLogesNames() {
        try {
            ArrayList<String> allLogNames = new ArrayList<>();
            for (Log customerLog : LogTable.getAllCustomerLogs(Control.getUsername())) {
                java.util.Date date = new java.util.Date(customerLog.getDate().getTime());
                allLogNames.add(date.toString());
            }
            return allLogNames;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getAllLogesIDs() {
        try {
            ArrayList<String> allLogIDs = new ArrayList<>();
            for (Log customerLog : LogTable.getAllCustomerLogs(Control.getUsername())) {
                allLogIDs.add(customerLog.getLogID());
            }
            return allLogIDs;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getProductOfLogNames() {
        try {
            ArrayList<String> allProductOfLogNames = new ArrayList<>();
            for (Log.ProductOfLog productOfLog : LogTable.getCustomerLogByID(getCurrentLogID()).getAllProducts()) {
                allProductOfLogNames.add(ProductTable.getProductByID(productOfLog.getProductID()).getName());
            }
            return allProductOfLogNames;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getProductOfLogIDs() {
        try {
            ArrayList<String> allProductOfLogIDs = new ArrayList<>();
            for (Log.ProductOfLog productOfLog : LogTable.getCustomerLogByID(getCurrentLogID()).getAllProducts()) {
                allProductOfLogIDs.add(productOfLog.getProductID());
            }
            return allProductOfLogIDs;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Log getCurrentLog(){
        try {
            return LogTable.getCustomerLogByID(getCurrentLogID());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Log();
    }

    public int getScore(String productID){
        try {
            if (ProductTable.didScore(Control.getUsername(), productID))
                return ProductTable.getScore(Control.getUsername(), productID);
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public Notification setScore(String productID, int score){
        try {
            if (ProductTable.didScore(Control.getUsername(), productID)){
                ProductTable.updateScore(Control.getUsername(), productID, score);
                ProductTable.updateProductsAvgScore(productID);
                return Notification.UPDATE_SCORE;
            }
            ProductTable.setScore(Control.getUsername(), productID, score);
            ProductTable.updateProductsAvgScore(productID);
            return Notification.SET_SCORE;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }
}
