package view.process;

import com.google.gson.GsonBuilder;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.HashMap;

public class FilteringProcessor extends Processor {
    private static FilteringProcessor filteringProcessor = null;

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

    public static ListicOptionMenu setMenu(String json, String filterName) {
        ListicOptionMenu filteringMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ListicOptionMenu.class);
        filteringMenu.setOption(filterName);
        return filteringMenu;
    }

    public Menu addFilter(Object... objects) {
        //TODO
        return null;
    }

    public Menu removeFilter(Object... objects) {
        //TODO
        return null;
    }
}
