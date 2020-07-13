package server.controller.account;

import server.controller.Control;
import server.controller.IOControl;
import server.model.db.*;
import server.model.existence.*;
import notification.Notification;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerControl extends AccountControl{
    private static CustomerControl customerControl = null;
    private boolean hasDiscount = false;
    private Discount discount = null;

    public static CustomerControl getController() {
        if (customerControl == null)
            customerControl = new CustomerControl();

        return customerControl;
    }

    public ArrayList<Product> getAllCartProducts(){
        try {
            DiscountTable.removeOutDatedDiscounts();
            OffTable.removeOutDatedOffs();
            return CartTable.getAllCartWithUsername(IOControl.getUsername());
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification addToCartCountable(/*String username, */String id, int count){
        try {
            if (ProductTable.getProductByID(id).getCount() < count)
                return Notification.MORE_THAN_INVENTORY_COUNTABLE;
            if (count <= 0)
                return Notification.NEGATIVE_NUMBER;
            if(CartTable.isThereCartProductForUsername(getUserNameForCart(), id)) {
                CartTable.deleteCartProduct(getUserNameForCart(), id);
            }
            CartTable.addToCartCountable(getUserNameForCart(), id, count);
            return Notification.ADD_TO_CART;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification addToCartUnCountable(/*String username, */String id, double amount){
        try {
            if (ProductTable.getProductByID(id).getAmount() < amount)
                return Notification.MORE_THAN_INVENTORY_UNCOUNTABLE;
            if (amount <= 0)
                return Notification.NEGATIVE_NUMBER;
            if(CartTable.isThereCartProductForUsername(getUserNameForCart(), id)) {
                CartTable.deleteCartProduct(getUserNameForCart(), id);
            }
            CartTable.addToCartUnCountable(getUserNameForCart(), id, amount);
            return Notification.ADD_TO_CART;
        } catch (SQLException e) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    private String getUserNameForCart() {
        if(Control.getType() != null && !Control.getType().equals("Customer")) {
            //:)
        } else if(Control.isLoggedIn()) {
            return Control.getUsername();
        } else {
            return "temp";
        }

        return null;
    }

    public Product getCartProductByID(String ID) {
        try {
            return CartTable.getCartProductByID(getUserNameForCart(), ID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new Product();
    }

/*    public Notification increaseCount(String productID, String command) {
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
                    //:)
                } catch (ClassNotFoundException e) {
                    //:)
                }
            }
        } catch (NumberFormatException e) { } catch (NullPointerException e) { }
        return Notification.INVALID_COUNT;
    }*/

/*    public Notification decreaseCount(String productID, String command) {
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
                    //:)
                } catch (ClassNotFoundException e) {
                    //:)
                }
            }
        } catch (NumberFormatException e) { } catch (NullPointerException e) { }
        return Notification.INVALID_COUNT;
    }*/

/*    public Notification increaseAmount(String productID, String command) {
        try {
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
                    //:)
                } catch (ClassNotFoundException e) {
                    //:)
                }
                return Notification.MORE_THAN_INVENTORY_UNCOUNTABLE;
            }
        } catch (NumberFormatException e) {} catch (NullPointerException e) {}
        return Notification.INVALID_AMOUNT;
    }*/

/*    public Notification decreaseAmount(String productID, String command) {
        try {
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
                    //:)
                } catch (ClassNotFoundException e) {
                    //:)
                }
                return Notification.MORE_THAN_CART_UNCOUNTABLE;
            }
        } catch (NumberFormatException e) {} catch (NullPointerException e) {}
        return Notification.INVALID_AMOUNT;
    }*/

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
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return totalPrice;
    }

    public Notification removeProductFromCartByID(String id) {
        try {
            if(CartTable.isThereCartProductForUsername(getUserNameForCart(), id))
            {
                CartTable.deleteCartProduct(getUserNameForCart(), id);
                return Notification.CART_PRODUCT_REMOVED;
            }
            return Notification.NOT_YOUR_CART_PRODUCT;
        } catch (SQLException e) { } catch (ClassNotFoundException e) { }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Discount> getDiscounts() {
        try {
            DiscountTable.removeOutDatedDiscounts();
            return DiscountTable.getCustomerDiscountCodes(Control.getUsername());
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
            finalPrice = calculateFinalPrice(hasDiscount, discount, offPrice);
            return affordability(initPrice, offPrice, finalPrice);

        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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
            int giftState = createLog(customer);
            if(hasDiscount) {
                DiscountTable.addRepetitionToDiscount(discount, customer.getUsername());
            }
            reduceProductFromStock(customer.getUsername());
            CartTable.deleteCustomerCart(customer.getUsername());
            hasDiscount = false;
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

    public void setDiscount(String discountID) {
        try {
            this.discount = DiscountTable.getDiscountByID(discountID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    private int createLog(Account customer) throws SQLException, ClassNotFoundException {
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
        int state = checkGift(log);
        LogTable.addLog(log);
        return state;
    }

    private int checkGift(Log log) {
        AdminControl adminControl = AdminControl.getController();
        if(log.getCustomerFinalPrice() >= 85000)
        {
            Discount discount = new Discount();
            discount.setCode("Super Code");
            HashMap<String, Integer> customer = new HashMap<>();
            customer.put(Control.getUsername(), 0);
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
            customer.put(Control.getUsername(), 0);
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
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new Discount();
    }

    public ArrayList<Log> getAllLogs() {
        try {
            return LogTable.getAllCustomerLogs(Control.getUsername());
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
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

    public int getScore(String productID){
        try {
            if (ProductTable.didScore(Control.getUsername(), productID))
                return ProductTable.getScore(Control.getUsername(), productID);
            return -1;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public boolean isProductPurchasedByCustomer(String productID, String customerUsername) {
        try {
            return LogTable.isProductPurchasedByCustomer(productID, customerUsername);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalPriceWithoutDiscount() {
        double offPrice = 0;
        try {
            for (Product product : CartTable.getAllCartWithUsername(getUserNameForCart())) {
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
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    public ArrayList<Discount> getAllAvailableCustomerDisCounts() {
        try {
            ArrayList<Discount> availableDiscounts = new ArrayList<>();

            for (Discount customerDiscountCode : DiscountTable.getCustomerDiscountCodes(getUsername())) {
                if(customerDiscountCode.canCustomerUseThisDiscount(getUsername())) {
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
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }
}
