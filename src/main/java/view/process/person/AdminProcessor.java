package view.process.person;


import controller.account.AdminControl;
import view.menu.Menu;
import view.menu.ProductListicMenu;
import view.process.FunctioningOption;

public class AdminProcessor extends AccountProcessor {
    private static AdminControl adminControl = AdminControl.getController();
    private static AdminProcessor adminProcessor = null;

    private AdminProcessor(){
        super();
        functionsHashMap.put("Manage All Products", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                return manageAllProducts();
            }
        });
    }

    public static AccountProcessor getInstance(){
        if(adminProcessor == null)
            adminProcessor = new AdminProcessor();

        return adminProcessor;
    }

    public Menu manageAllProducts()
    {
        ProductListicMenu menu = ProductListicMenu.makeProductListicMenu("Manage All Products Listic Menu");
        adminControl.setProductLists(menu.getProductNames(), menu.getProductIDs());
        return menu;
    }
}
