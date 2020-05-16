package view.process;

import controller.Control;
import controller.product.ProductControl;
import model.existence.Product;
import view.menu.ListicMenu;
import view.menu.Menu;

import java.util.HashMap;

public class MainMenuProcessor extends Processor {
    private static MainMenuProcessor mainMenuProcessor = null;
    private static ProductControl control = ProductControl.getController();
    private MainMenuProcessor(){
        functionsHashMap = new HashMap<>();
        functionsHashMap.put("Account Menu", new FunctioningOption() {
            public Menu doTheThing(Object... objects) {
                return iOManage();
            }
        });
        functionsHashMap.put("Products Menu", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                control.initFilter();
                control.initSort();
                control.setOffListic(false);
                return ListicMenu.makeListicMenu("Products Listic Menu");
            }
        });
        functionsHashMap.put("Offs Menu", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                control.setOffListic(true);
                return ListicMenu.makeListicMenu("Offs Listic Menu");
            }
        });
    }

    public static MainMenuProcessor getInstance(){
        if(mainMenuProcessor == null)
            mainMenuProcessor = new MainMenuProcessor();

        return mainMenuProcessor;
    }

    public Menu iOManage(){
        if(Control.isLoggedIn()) {
            Menu menu = Menu.makeMenu(Control.getType() + " Menu");
            menu.setName(Control.getUsername());
            return menu;
        }

        return Menu.makeMenu("IO Menu");
    }
}
