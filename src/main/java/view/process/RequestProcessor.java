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
                return ListicMenu.makeListicMenu("Manage Register Requests Listic Menu");
            }
        });
        this.functionsHashMap.put("Add Product Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Add Product Requests Listic Menu");
            }
        });
        this.functionsHashMap.put("Edit Product Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Edit Product Requests Listic Menu");
            }
        });
        this.functionsHashMap.put("Add Off Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Add Off Requests Listic Menu");
            }
        });
        this.functionsHashMap.put("Edit Off Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Edit Off Requests Listic Menu");
            }
        });
        this.functionsHashMap.put("Manage Comment Requests", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Comments Listic Menu");
            }
        });
    }

    public static RequestProcessor getInstance() {
        if(processor == null)
            processor = new RequestProcessor();
        return processor;
    }

}
