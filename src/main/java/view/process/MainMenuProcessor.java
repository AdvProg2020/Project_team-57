package view.process;

import controller.Control;
import view.menu.Menu;

import java.util.HashMap;

public class MainMenuProcessor extends Processor {
    private static MainMenuProcessor mainMenuProcessor = null;

    private MainMenuProcessor(){
        functionsHashMap = new HashMap<>();
        functionsHashMap.put("Account Menu", new FunctioningOption() {
            public Menu doTheThing() {
                return iOManage();
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
