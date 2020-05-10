package view.process;

import view.menu.ListicMenu;
import view.menu.Menu;

import java.util.HashMap;

public class RequestProcessor extends Processor{
    private static RequestProcessor processor = null;


    private RequestProcessor() {
        this.functionsHashMap = new HashMap<>();
        this.functionsHashMap.put("Manage Vendors Registration Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageRegistration();
            }
        });
        this.functionsHashMap.put("Add Product Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageAddProductRequests();
            }
        });
        this.functionsHashMap.put("Edit Product Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageEditProductRequests();
            }
        });
    }

    public static RequestProcessor getInstance() {
        if(processor == null)
            processor = new RequestProcessor();
        return processor;
    }

    public Menu manageRegistration()
    {
        return ListicMenu.makeListicMenu("Manage Register Requests Listic Menu");
    }

    public Menu manageAddProductRequests(){
        return ListicMenu.makeListicMenu("Manage Add Product Requests Listic Menu");
    }

    public Menu manageEditProductRequests()
    {
        return ListicMenu.makeListicMenu("Manage Edit Product Reuests Listic Menu");
    }


}
