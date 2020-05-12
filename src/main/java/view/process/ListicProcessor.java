package view.process;

import controller.IOControl;
import controller.account.AccountControl;
import controller.account.AdminControl;
import controller.account.CustomerControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import model.existence.Account;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.person.AdminProcessor;

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
                return addCategory(objects);
            }
        });
    }

    public static ListicProcessor getInstance()
    {
        if(processor == null)
            processor = new ListicProcessor();
        return processor;
    }

    public ListicOptionMenu getListicOptionMenu(Object... objects)
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
        else if(parentMenu.getName().equals("Products Menu"))
            return ListicOptionMenu.makeMenu("Common Product Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("View Cart"))
            return ListicOptionMenu.makeMenu("Cart Product Menu", parentMenu, primaryKey);
        else if(parentMenu.getName().equals("Manage Categories"))
            return ListicOptionMenu.makeMenu("Admin Category Menu", parentMenu, primaryKey);
        //TODO(OTHERS)
        return null;
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

    public Menu addCategory(Object... objects)
    {
        AdminProcessor.newCategory();
        return Menu.makeMenu("Add Category Menu");
    }


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
        else if(listicMenu.getName().equals("Products Menu"))
            initProductsMenu(listicMenu);
        else if(listicMenu.getName().equals("View Cart"))
            initViewCart(listicMenu);
        else if(listicMenu.getName().equals("Manage Categories"))
            initManageCategories(listicMenu);
        //TODO(OTHERS)
    }

    private static void initManageCategories(ListicMenu listicMenu) {
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
