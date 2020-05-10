package view.process.person;


import controller.account.AdminControl;
import view.menu.ListicMenu;
import view.menu.Menu;
import view.process.FunctioningOption;


public class AdminProcessor extends AccountProcessor {
    private static AdminControl adminControl = AdminControl.getController();
    private static AdminProcessor adminProcessor = null;

    private AdminProcessor(){
        super();
        functionsHashMap.put("Manage All Products", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageAllProducts();
            }
        });
        functionsHashMap.put("Manage All Users", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageAllUsers();
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
        return ListicMenu.makeListicMenu("Manage All Products Listic Menu");
    }

    public Menu manageAllUsers()
    {
        return ListicMenu.makeListicMenu("Manage All Users Listic Menu");
    }
}
