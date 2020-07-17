package server.controller.account;

import server.controller.Control;
import server.controller.IOControl;
import server.model.db.*;
import server.model.existence.*;
import notification.Notification;
import server.server.Property;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerControl extends AccountControl{
    private static CustomerControl customerControl = null;

    public static CustomerControl getController() {
        if (customerControl == null)
            customerControl = new CustomerControl();

        return customerControl;
    }

    public ArrayList<Product> getAllCartProducts(String username){
        try {
            DiscountTable.removeOutDatedDiscounts();
            OffTable.removeOutDatedOffs();
            return CartTable.getAllCartWithUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Notification addToCartCountable(String username, String productID, int count){
        try {
            if (ProductTable.getProductByID(productID).getCount() < count)
                return Notification.MORE_THAN_INVENTORY_COUNTABLE;
            if (count <= 0)
                return Notification.NEGATIVE_NUMBER;
            if(CartTable.isThereCartProductForUsername(getUserNameForCart(username), productID)) {
                CartTable.deleteCartProduct(getUserNameForCart(username), productID);
            }
            CartTable.addToCartCountable(getUserNameForCart(username), productID, count);
            return Notification.ADD_TO_CART;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification addToCartUnCountable(String username, String productID, double amount){
        try {
            if (ProductTable.getProductByID(productID).getAmount() < amount)
                return Notification.MORE_THAN_INVENTORY_UNCOUNTABLE;
            if (amount <= 0)
                return Notification.NEGATIVE_NUMBER;
            if(CartTable.isThereCartProductForUsername(getUserNameForCart(username), productID)) {
                CartTable.deleteCartProduct(getUserNameForCart(username), productID);
            }
            CartTable.addToCartUnCountable(getUserNameForCart(username), productID, amount);
            return Notification.ADD_TO_CART;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    private String getUserNameForCart(String username) {
//        if(Control.getType() != null && !Control.getType().equals("Customer")) {
//            :)
        if(username != null && !username.isEmpty()) {
            return username;
        } else {
            return "temp";
        }

//        return null;
    }

    public Product getCartProductByID(String ID, String username) {
        try {
            return CartTable.getCartProductByID(getUserNameForCart(username), ID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Product();
    }

    public Notification removeProductFromCartByID(String username, String productID) {
        try {
            if(CartTable.isThereCartProductForUsername(getUserNameForCart(username), productID))
            {
                CartTable.deleteCartProduct(getUserNameForCart(username), productID);
                return Notification.CART_PRODUCT_REMOVED;
            }
            return Notification.NOT_YOUR_CART_PRODUCT;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Discount> getDiscounts(String username) {
        try {
            DiscountTable.removeOutDatedDiscounts();
            return DiscountTable.getCustomerDiscountCodes(username);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }


    public ArrayList<Off> getAllShowingOffs() {
        try {
            OffTable.removeOutDatedOffs();
            return OffTable.getAllShowingOffs();
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    //-------------------------------------------------PURCHASE-------------------------------------------------//
    public Notification purchase(String username, Property property)
    {
        try {
            double initPrice = 0; double offPrice = 0; double finalPrice;
            for (Product product : CartTable.getAllCartWithUsername(username)) {
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
            finalPrice = calculateFinalPrice(property.hasDiscount(), property.getDiscount(), offPrice);
            return affordability(initPrice, offPrice, finalPrice, username, property);

        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    private Notification affordability(double initPrice, double offPrice, double finalPrice, String username, Property property) {
        try {
            Account customer = AccountTable.getAccountByUsername(username);
            if(finalPrice > customer.getCredit())
                return Notification.CANT_AFFORD_CART;
            AccountTable.changeCredit(customer.getUsername(),((-1) * finalPrice));
            giveCreditToVendors(customer.getUsername());
            int giftState = createLog(customer, property);
            if(property.hasDiscount()) {
                DiscountTable.addRepetitionToDiscount(property.getDiscount(), customer.getUsername());
            }
            reduceProductFromStock(customer.getUsername());
            CartTable.deleteCustomerCart(customer.getUsername());
            property.setHasDiscount(false);
            switch (giftState)
            {
                case 1 :
                    return Notification.PURCHASED_SUPERBLY;
                case 2 :
                    return Notification.PURCHASED_GOODLY;
                default:
                    return Notification.PURCHASED_SUCCESSFULLY;
            }
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
    }

    private double calculateFinalPrice(boolean hasDiscount, Discount discount, double offPrice) {
        if(!hasDiscount)
            return offPrice;
        double discountVal = (discount.getDiscountPercent()/100) * offPrice;
        if(discountVal <= discount.getMaxDiscount()) {
            return offPrice - discountVal;
        }
        return offPrice - discount.getMaxDiscount();
    }

    private int createLog(Account customer, Property property) throws SQLException, ClassNotFoundException {
        Log log = new Log();
        String logID = "";
        do {
            logID = generateLogID();
        } while (LogTable.isThereLogWithID(logID));

        log.setLogID(logID);
        log.setCustomerUsername(customer.getUsername());
        if(property.hasDiscount())
            log.setDiscountPercent(property.getDiscount().getDiscountPercent());
        else
            log.setDiscountPercent(0);

        log.setStatus(1);
        log.setDate(new Date(System.currentTimeMillis()));

        ArrayList<Log.ProductOfLog> logProducts = new ArrayList<>();
        for (Product product : CartTable.getAllCartWithUsername(customer.getUsername())) {
            logProducts.add(new Log.ProductOfLog(product));
        }

        log.setAllProducts(logProducts);
        int state = checkGift(customer.getUsername(), log);
        LogTable.addLog(log);
        return state;
    }

    private int checkGift(String username, Log log) {
        AdminControl adminControl = AdminControl.getController();
        if(log.getCustomerFinalPrice() >= 85000)
        {
            Discount discount = new Discount();
            discount.setCode("Super Code");
            HashMap<String, Integer> customer = new HashMap<>();
            customer.put(username, 0);
            discount.setCustomersWithRepetition(customer);
            discount.setMaxRepetition(1);
            discount.setDiscountPercent(85);
            discount.setMaxDiscount(85000);
            discount.setStartDate(new Date(System.currentTimeMillis()));
            long days85 = (long) 7.344e+9;
            discount.setFinishDate(new Date(discount.getStartDate().getTime() + days85));
            adminControl.addDiscount(discount);
            return 1;
        }
        else if(log.getCustomerFinalPrice() >= 5000)
        {
            Discount discount = new Discount();
            discount.setCode("Good Customer");
            HashMap<String, Integer> customer = new HashMap<>();
            customer.put(username, 0);
            discount.setCustomersWithRepetition(customer);
            discount.setMaxRepetition(1);
            discount.setDiscountPercent(15);
            discount.setMaxDiscount(1000);
            discount.setStartDate(new Date(System.currentTimeMillis()));
            long oneMonth = (long) 2.628e+9;
            discount.setFinishDate(new Date(discount.getStartDate().getTime() + oneMonth));
            adminControl.addDiscount(discount);
            return 2;
        }
        return 0;
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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Discount();
    }

    public ArrayList<Log> getAllLogs(String username) {
        try {
            return LogTable.getAllCustomerLogs(username);
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Log getCurrentLog(){
        try {
            return LogTable.getCustomerLogByID(getCurrentLogID());
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new Log();
    }

    public boolean isProductPurchasedByCustomer(String productID, String customerUsername) {
        try {
            return LogTable.isProductPurchasedByCustomer(productID, customerUsername);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalPriceWithoutDiscount(String username) {
        double offPrice = 0;
        try {
            for (Product product : CartTable.getAllCartWithUsername(getUserNameForCart(username))) {
                if (OffTable.isThereProductInOff(product.getID())) {
                    offPrice += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent() / 100))
                            * product.getPrice() * product.getCount();
                    offPrice += (1 - (OffTable.getOffByProductID(product.getID()).getOffPercent() / 100))
                            * product.getPrice() * product.getAmount();
                } else {
                    offPrice += product.getPrice() * product.getAmount();
                    offPrice += product.getPrice() * product.getCount();
                }
            }
            return offPrice;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<Discount> getAllAvailableCustomerDisCounts(String username) {
        try {
            ArrayList<Discount> availableDiscounts = new ArrayList<>();
            for (Discount customerDiscountCode : DiscountTable.getCustomerDiscountCodes(username)) {
                if(customerDiscountCode.canCustomerUseThisDiscount(username)) {
                    availableDiscounts.add(customerDiscountCode);
                }
            }

            return availableDiscounts;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }

        return null;
    }

    public ArrayList<Product> getTempCartProducts() {
        try {
            OffTable.removeOutDatedOffs();
            return CartTable.getAllCartWithUsername("temp");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
