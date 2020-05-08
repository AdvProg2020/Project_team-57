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
    }

    public static RequestProcessor getInstance() {
        if(processor == null)
            processor = new RequestProcessor();
        return processor;
    }

    public Menu manageRegistration()
    {
        return ListicMenu.makeListicMenu("Manage Registration Requests Menu");
    }

}
