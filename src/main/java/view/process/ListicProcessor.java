package view.process;

import controller.account.AccountControl;
import controller.product.ProductControl;
import view.menu.ListicMenu;
import view.menu.Menu;

public class ListicProcessor extends Processor {
    private static ProductControl productControl;
    private static AccountControl accountControl;
    private static ListicProcessor processor = null;

    public ListicProcessor() {
        productControl = ProductControl.getController();
        accountControl = AccountControl.getController();

        this.functionsHashMap.put("Open Listic Option", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                return getListicOptionMenu();
            }
        });
    }

    public static ListicProcessor getInstance()
    {
        if(processor == null)
            processor = new ListicProcessor();
        return processor;
    }

    public ListicOptionMenu getListicOptionMenu(String primaryKey, Menu parentMenu)
    {
        if(parentMenu.getName().equals("Manage All Products"))
        {
            return ListicOptionMenu.makeMenu("Admin Product Menu", primaryKey, parentMenu);
        }
        //TODO (Name ???)
        else if(parentMenu.getName().equals("Manage Vendors Registration Requests"))
        {
            return ListicOptionMenu.makeMenu("Register Request Menu", primaryKey, parentMenu);
        }
        return null;
    }

    public static void initListicMenu(ListicMenu listicMenu)
    {
        if(listicMenu.getName().equals("Manage All Products"))
            initProductListicMenu(listicMenu);
        else if(listicMenu.getName().equals("Manage Vendors Registration Requests"))
            initRegisterRequestListicMenu(listicMenu);
        //TODO(OTHERS)
    }

    private static void initRegisterRequestListicMenu(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(accountControl.getAllUsernames());
        listicMenu.setListicOptionPrimaryKeys(accountControl.getAllUsernames());
    }

    private static void initProductListicMenu(ListicMenu listicMenu) {
        listicMenu.setListicOptionNames(productControl.getAllProductNames());
        listicMenu.setListicOptionPrimaryKeys(productControl.getAllProductIDs());
    }
}
