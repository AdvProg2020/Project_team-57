package view.process;

import controller.Control;
import controller.IOControl;
import controller.account.AccountControl;
import controller.account.AdminControl;
import controller.account.CustomerControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import model.existence.Account;
import model.existence.Discount;
import model.existence.Log;
import model.existence.Off;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.person.AdminProcessor;
import view.process.person.VendorProcessor;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

public class ListicProcessor extends Processor {
    private static ProductControl productControl = ProductControl.getController();
    private static AccountControl accountControl = AccountControl.getController();
    private static VendorControl vendorControl = VendorControl.getController();
    private static IOControl ioControl = IOControl.getController();
    private static CustomerControl customerControl = CustomerControl.getController();
    private static AdminControl adminControl = AdminControl.getController();
    private static ListicProcessor processor = null;

    public ListicProcessor() {
        this.functionsHashMap = new HashMap<>();
        this.functionsHashMap.put("Open Functioning Option", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getListicOptionMenu(objects);
            }
        });
        this.functionsHashMap.put("Add Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addProduct(objects);
            }
        });
        this.functionsHashMap.put("Create Another Manager Profile", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addManagerProfile(objects);
            }
        });
        this.functionsHashMap.put("Show Total Price", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return showCartTotalPrice(objects);
            }
        });
        this.functionsHashMap.put("Add Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                AdminProcessor.newCategory();
                return Menu.makeMenu("Add Category Menu");
            }
        });
        this.functionsHashMap.put("Filtering", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return filtering(objects);
            }
        });
        this.functionsHashMap.put("Sorting", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return sorting();
            }
        });
        this.functionsHashMap.put("Create Discount Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                AdminProcessor.newDiscount();
                return Menu.makeMenu("Create Discount Code Menu");
            }
        });
        this.functionsHashMap.put("Add Off", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                VendorProcessor.setOff(new Off());
                return Menu.makeMenu("Add Off Menu");
            }
        });
        this.functionsHashMap.put("Purchase", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return Menu.makeMenu("Receiving Information Menu");
            }
        });
        this.functionsHashMap.put("Purchase Without Discount", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                customerControl.setHasDiscount(false);
                System.out.println(customerControl.purchase().getMessage());
                return ListicMenu.makeListicMenu("View Cart Listic Menu");
            }
        });
        this.functionsHashMap.put("View Discount Code Percent", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return showLogStat();
            }
        });
        this.functionsHashMap.put("Show Log Status", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return showLogStat();
            }
        });
    }

    public static ListicProcessor getInstance()
    {
        if(processor == null)
            processor = new ListicProcessor();
        return processor;
    }

    public Menu getListicOptionMenu(Object... objects)
    {
        Object[] allObjects = objects.clone();
        String primaryKey = (String) allObjects[0];
        Menu parentMenu = (Menu) allObjects[1];
        if(parentMenu.getName().equals("Manage All Products"))
            return ListicOptionMenu.makeMenu("Admin Product Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Vendors Registration Requests"))
            return ListicOptionMenu.makeMenu("Register Request Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Products"))
            return ListicOptionMenu.makeMenu("Vendor Product Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Add Product Requests"))
            return ListicOptionMenu.makeMenu("Add Product Request Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Edit Product Requests"))
            return ListicOptionMenu.makeMenu("Edit Product Request Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage All Users"))
            return ListicOptionMenu.makeMenu("User Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("View All Admins"))
            return ListicOptionMenu.makeMenu("Admin Profile Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Products Menu") || parentMenu.getName().equals("Off Products Menu")) {
            productControl.addSeenToProduct(primaryKey);
            return ListicOptionMenu.makeMenu("Common Product Menu", parentMenu, primaryKey);
        }
        else if(parentMenu.getName().equals("View Cart"))
            return ListicOptionMenu.makeMenu("Cart Product Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Categories"))
            return ListicOptionMenu.makeMenu("Admin Category Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Categories") || parentMenu.getName().equals("Current Categories"))
            return ListicOptionMenu.makeMenu("Filtering Category Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Current Name Filters"))
            return ListicOptionMenu.makeMenu("Filtering Name Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Discount Codes"))
            return ListicOptionMenu.makeMenu("View Discount Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Add Customers To Discount Code"))
            return ListicOptionMenu.makeMenu("Discount User Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("View Discount Codes"))
            return ListicOptionMenu.makeMenu("Customer View Discount Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Offs"))
            return ListicOptionMenu.makeMenu("Vendor Off Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Add Products To Off"))
            return ListicOptionMenu.makeMenu("Off Product Menu", parentMenu,primaryKey);
        else if(parentMenu.getName().equals("Manage Add Off Requests"))
            return ListicOptionMenu.makeMenu("Add Off Request Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Edit Off Requests"))
            return ListicOptionMenu.makeMenu("Edit Off Request Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Offs Menu")) {
            productControl.setListicOffID(primaryKey);
            return ListicMenu.makeListicMenu("Off Products Listic Menu");
        }
        else if(parentMenu.getName().equals("Comparison Products Menu")) {
            if (!primaryKey.equals(productControl.getComparingProducts()[0].getID())) {
                productControl.setSecondComparingProduct(primaryKey);
                System.out.println("Successfully Added The Comparing Product");
                return Menu.makeMenu("Comparison Menu");
            } else {
                System.out.println("You Must Choose Different Products To Compare");
                return parentMenu;
            }
        }
        else if(parentMenu.getName().equals("Select Discount")) {
            customerControl.setHasDiscount(true);
            customerControl.setDiscount(primaryKey);
            System.out.println(customerControl.purchase().getMessage());
            return ListicMenu.makeListicMenu("View Cart Listic Menu");
        }
        else if(parentMenu.getName().equals("View Buy Logs")) {
            AccountControl.setCurrentLogID(primaryKey);
            return ListicMenu.makeListicMenu("View Log Products Listic Menu");
        }
        else if(parentMenu.getName().equals("View Log Products") || parentMenu.getName().equals("View Sell Log Products"))
            return ListicOptionMenu.makeMenu("View Log Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("View Sell Logs"))
        {
            AccountControl.setCurrentLogID(primaryKey);
            return ListicMenu.makeListicMenu("View Sell Log Products Listic Menu");
        }
        else if(parentMenu.getName().equals("Manage Comment Requests"))
            return ListicOptionMenu.makeMenu("Admin Adding Comment Request Menu");
        else if(parentMenu.getName().equals("Comment Menu"))
            return ListicOptionMenu.makeMenu("Comment Menu");
        //TODO(OTHERS)
        return null;
    }

    public Menu showLogStat() {
        Log log = null;
        if(Control.getType().equals("Customer"))
            log = customerControl.getCurrentLog();
        else
            log = vendorControl.getCurrentLog();
        System.out.println("0. Back");
        System.out.println(new Date(log.getDate().getTime()).toString() + ":");
        if(Control.getType().equals("Customer"))
        {
            if(log.getDiscountPercent() != 0)
            {
                System.out.println("Discount Code Percent : " + log.getDiscountPercent());
            }
            else
                System.out.println("No Discount Code Used");
            System.out.println("Final Total Price: " + log.getFinalPrice());
        }
        else {
            System.out.println("Final Total Price: " + log.getVendorFinalPrice());
        }
        String stat = "";
        switch (log.getStatus())
        {
            case 1 :
                stat = "Delivery Preparation";
                break;
            case 2 :
                stat = "Sent";
                break;
            case 3 :
                stat = "Delivered";
                break;
        }
        //System.out.println("stat : " );
        System.out.println("Shopping Status: " + stat);
        while (true)
        {
            if(scanner.nextLine().trim().equals("0")) {
                if(Control.getType().equals("Customer"))
                    return ListicMenu.makeListicMenu("View Log Products Listic Menu");
                return ListicMenu.makeListicMenu("View Sell Log Products Listic Menu");
            }
            System.out.println("Invalid Input Dude \uD83D\uDE32");
        }
    }

    public Menu addProduct(Object... objects)
    {
        Object[] allObjects = objects.clone();
        AddProductProcessor.getInstance((ListicMenu) allObjects[0]);
        Menu menu = Menu.makeMenu("Add Product Menu");
        return menu;
    }

    public Menu addManagerProfile(Object... objects)
    {
        Account account = new Account();
        account.setType("Admin");
        System.out.println("Please Enter Your UserName :");
        account.setUsername(scanner.nextLine().trim());

        System.out.println("Please Enter Your PassWord :");
        account.setPassword(scanner.nextLine().trim());

        System.out.println(ioControl.register(account).getMessage());
        return ((ListicMenu) objects[0]);
    }

    public Menu showCartTotalPrice(Object... objects)
    {
        ListicMenu parentMenu = (ListicMenu) objects[0];
        System.out.println("Total Price: \n" +
                customerControl.calculateCartTotalPrice() + "$");
        return parentMenu;
    }

    public Menu filtering(Object... objects)
    {
        return Menu.makeMenu("Filter Menu");
    }

    public Menu sorting(){ return Menu.makeMenu("Sorting Menu"); }

    public static void initListicMenu(ListicMenu listicMenu)
    {
        if(listicMenu.getName().equals("Manage All Products"))
            initProductListicMenu(listicMenu);
        else if(listicMenu.getName().equals("Manage Vendors Registration Requests"))
            initRegisterRequestListicMenu(listicMenu);
        else if(listicMenu.getName().equals("Manage Products"))
            initManageProducts(listicMenu);
        else if(listicMenu.getName().equals("Manage Add Product Requests"))
            initManageAddProductRequests(listicMenu);
        else if(listicMenu.getName().equals("Manage Edit Product Requests"))
            initManageEditProductRequests(listicMenu);
        else if(listicMenu.getName().equals("Manage All Users"))
            initManageAllUsers(listicMenu);
        else if(listicMenu.getName().equals("View All Admins"))
            initViewAllAdmins(listicMenu);
        else if(listicMenu.getName().equals("Products Menu") || listicMenu.getName().equals("Off Products Menu"))
            initProductsMenu(listicMenu);
        else if(listicMenu.getName().equals("View Cart"))
            initViewCart(listicMenu);
        else if(listicMenu.getName().equals("Manage Categories") || listicMenu.getName().equals("Categories"))
            initCategories(listicMenu);
        else if(listicMenu.getName().equals("Current Categories"))
            initCurrentCategories(listicMenu);
        else if(listicMenu.getName().equals("Current Name Filters"))
            initCurrentNameFilters(listicMenu);
        else if(listicMenu.getName().equals("Manage Discount Codes"))
            initManageDiscountCodes(listicMenu);
        else if(listicMenu.getName().equals("Add Customers To Discount Code"))
            initAddCustomersToDiscount(listicMenu);
        else if(listicMenu.getName().equals("View Discount Codes") || listicMenu.getName().equals("Select Discount"))
            initViewDiscountCodes(listicMenu);
        else if(listicMenu.getName().equals("Manage Offs"))
            initManageOffs(listicMenu);
        else if(listicMenu.getName().equals("Add Products To Off"))
            initProductsForOff(listicMenu);
        else if(listicMenu.getName().equals("Manage Add Off Requests"))
            initManageAddOffRequest(listicMenu);
        else if(listicMenu.getName().equals("Manage Edit Off Requests"))
            initManageEditOffRequests(listicMenu);
        else if(listicMenu.getName().equals("Offs Menu"))
            initOffs(listicMenu);
        else if(listicMenu.getName().equals("Comparison Products Menu"))
            initComparisonProducts(listicMenu);
        else if(listicMenu.getName().equals("View Buy Logs"))
            initBuyLogs(listicMenu);
        else if(listicMenu.getName().equals("View Log Products"))
            initLogProducts(listicMenu);
        else if(listicMenu.getName().equals("View Sell Logs"))
            initSellLogs(listicMenu);
        else if(listicMenu.getName().equals("View Sell Log Products"))
            initSellLogProducts(listicMenu);
        else if(listicMenu.getName().equals("Manage Comment Requests"))
            initManageComments(listicMenu);
        else if(listicMenu.getName().equals("Comments Menu"))
            initComments(listicMenu);
        //TODO(OTHERS)
    }

    private static void initComments(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getShowingCommentTitles());
        listicMenu.setListicOptionPrimaryKeys(productControl.getShowingCommentIDs());
    }

    private static void initManageComments(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(adminControl.getAllUnApprovedCommentTitles());
        listicMenu.setListicOptionPrimaryKeys(adminControl.getAllUnApprovedCommentIDs());
    }

    private static void initSellLogProducts(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(vendorControl.getAllProductsNamesInSpecificLog());
        listicMenu.setListicOptionPrimaryKeys(vendorControl.getAllProductsIdsInSpecificLog());
    }

    private static void initSellLogs(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(vendorControl.getALlVendorLogsName());
        listicMenu.setListicOptionPrimaryKeys(vendorControl.getALlVendorLogsID());
    }

    private static void initLogProducts(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(customerControl.getProductOfLogNames());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getProductOfLogIDs());
    }

    private static void initBuyLogs(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(customerControl.getAllLogesNames());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getAllLogesIDs());
    }

    private static void initComparisonProducts(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getAllComparingProductNames());
        listicMenu.setListicOptionPrimaryKeys(productControl.getAllComparingProductIDs());
    }

    private static void initOffs(ListicMenu listicMenu) {
        productControl.initFilter();
        productControl.initSort();
        listicMenu.setListicOptionNames(customerControl.getAllShowingOffNames());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getAllShowingOffIDs());
    }

    private static void initManageEditOffRequests(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(adminControl.getAllEditingOffNames());
        listicMenu.setListicOptionPrimaryKeys(adminControl.getAllEditingOffIDs());
    }

    private static void initManageAddOffRequest(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(adminControl.getAllUnApprovedOffNames());
        listicMenu.setListicOptionPrimaryKeys(adminControl.getAllUnApprovedOffIDs());
    }

    private static void initProductsForOff(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(vendorControl.getNonOffProductsNames());
        listicMenu.setListicOptionPrimaryKeys(vendorControl.getNonOffProductsIDs());
    }

    private static void initManageOffs(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(vendorControl.getAllOffNames());
        listicMenu.setListicOptionPrimaryKeys(vendorControl.getAllOffIDs());
    }

    private static void initViewDiscountCodes(ListicMenu listicMenu) {
        customerControl.setHasDiscount(false);
        listicMenu.setListicOptionNames(customerControl.getDiscountCodes());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getDiscountIDs());
    }

    private static void initAddCustomersToDiscount(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(accountControl.getAllCustomerNames());
        listicMenu.setListicOptionPrimaryKeys(accountControl.getAllCustomerNames());
    }

    private static void initManageDiscountCodes(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(adminControl.getAllDiscountCodes());
        listicMenu.setListicOptionPrimaryKeys(adminControl.getAllDiscountIDs());
    }

    private static void initCurrentNameFilters(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(customerControl.getCurrentNameFilters());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getCurrentNameFilters());
    }

    private static void initCurrentCategories(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(customerControl.getCurrentCategories());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getCurrentCategories());
    }

    private static void initCategories(ListicMenu listicMenu) {
        //System.out.println("WTF!!!");
        listicMenu.setListicOptionNames(adminControl.getAllCategoryNames());
        listicMenu.setListicOptionPrimaryKeys(adminControl.getAllCategoryNames());
    }

    private static void initViewCart(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(customerControl.getCartProductNames());
        listicMenu.setListicOptionPrimaryKeys(customerControl.getCartProductIDs());
    }

    private static void initProductsMenu(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getAllShowingProductNames());
        listicMenu.setListicOptionPrimaryKeys(productControl.getAllShowingProductIDs());
    }

    private static void initViewAllAdmins(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(accountControl.getAdminsUsernames());
        listicMenu.setListicOptionPrimaryKeys(accountControl.getAdminsUsernames());
    }

    private static void initManageAllUsers(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(accountControl.getAllUsernames());
        listicMenu.setListicOptionPrimaryKeys(accountControl.getAllUsernames());
    }

    private static void initManageEditProductRequests(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getAllEditingProductNames());
        listicMenu.setListicOptionPrimaryKeys(productControl.getAllEditingProductIDs());
    }

    private static void initManageAddProductRequests(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getAllUnApprovedProductNames());
        listicMenu.setListicOptionPrimaryKeys(productControl.getAllUnApprovedProductIDs());
    }

    private static void initManageProducts(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(vendorControl.getVendorProductNames());
        listicMenu.setListicOptionPrimaryKeys(vendorControl.getVendorProductIDs());
    }

    private static void initRegisterRequestListicMenu(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(accountControl.getUnapprovedUsernames());
        listicMenu.setListicOptionPrimaryKeys(accountControl.getUnapprovedUsernames());
    }

    private static void initProductListicMenu(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getAllProductNames());
        listicMenu.setListicOptionPrimaryKeys(productControl.getAllProductIDs());
    }
}
