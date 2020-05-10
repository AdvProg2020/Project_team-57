package view.process;

import controller.account.AccountControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.HashMap;

public class ListicProcessor extends Processor {
    private static ProductControl productControl;
    private static AccountControl accountControl;
    private static VendorControl vendorControl;
    private static ListicProcessor processor = null;

    public ListicProcessor() {
        productControl = ProductControl.getController();
        accountControl = AccountControl.getController();
        vendorControl = VendorControl.getController();
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
        {
            return ListicOptionMenu.makeMenu("Admin Product Menu", parentMenu, primaryKey);
        }
        else if(parentMenu.getName().equals("Manage Vendors Registration Requests"))
        {
            return ListicOptionMenu.makeMenu("Register Request Menu", parentMenu, primaryKey);
        }
        else if(parentMenu.getName().equals("Manage Products"))
        {
            return ListicOptionMenu.makeMenu("Vendor Product Menu", parentMenu, primaryKey);
        }
        else if(parentMenu.getName().equals("Manage Add Product Requests"))
        {
            return ListicOptionMenu.makeMenu("Add Product Request Menu", parentMenu, primaryKey);
        }
        else if(parentMenu.getName().equals("Manage Edit Product Requests"))
        {
            return ListicOptionMenu.makeMenu("Edit Product Request Menu", parentMenu, primaryKey);
        }
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
        //TODO(OTHERS)
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
