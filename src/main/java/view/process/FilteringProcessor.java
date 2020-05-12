package view.process;

import com.google.gson.GsonBuilder;
import controller.Control;
import controller.account.CustomerControl;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.HashMap;

public class FilteringProcessor extends Processor {
    private static FilteringProcessor filteringProcessor = null;
    private static CustomerControl customerControl = CustomerControl.getController();

    private FilteringProcessor() {
        functionsHashMap = new HashMap<>();
        functionsHashMap.put("Add Filter", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addFilter(objects);
            }
        });
        functionsHashMap.put("Remove Filter", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeFilter(objects);
            }
        });

    }

    public static FilteringProcessor getInstance() {
        if(filteringProcessor == null)
            filteringProcessor = new FilteringProcessor();

        return filteringProcessor;
    }

    public static void setMenu(ListicOptionMenu filteringMenu, String filterName) {
        filteringMenu.setOption(filterName);
    }

    public Menu addFilter(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        String filterName = (String) objects[1];

        if(menu.getName().equals("Filtering Category Menu")) {
            System.out.println(customerControl.addToFilterCategoryList(filterName).getMessage());
        } else if(menu.getName().equals("Filtering Name Menu")) {
            System.out.println(customerControl.addToFilterNameList(filterName).getMessage());
        }

        return menu.getParentMenu();
    }

    public Menu removeFilter(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        String filterName = (String) objects[1];

        if(menu.getName().equals("Filtering Category Menu")) {
            System.out.println(customerControl.removeFromFilterCategoryList(filterName).getMessage());
        } else if(menu.getName().equals("Filtering Name Menu")) {
            System.out.println(customerControl.removeFromFilterNameList(filterName).getMessage());
        }

        return menu.getParentMenu();
    }
}
