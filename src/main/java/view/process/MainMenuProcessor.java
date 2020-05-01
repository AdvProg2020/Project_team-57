package view.process;

import view.Menu;

import java.util.HashMap;

public class MainMenuProcessor extends Processor {
    private static IOAccountController ioAccountController = IOAccountController.getInstance();
    private static MainMenuProcessor mainMenuProcessor = null;

    private MainMenuProcessor(){
        functionsHashMap = new HashMap<String, FunctioningOption>();
        functionsHashMap.put("User Menu", new FunctioningOption() {
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
        if(Controller.getLoginStatus()) {
            Menu menu = Menu.makeMenu(Controller.getType() + " Menu");
            menu.setName(Controller.getUserName());
            return menu;
        }

        return Menu.makeMenu("IOAccount Menu");
    }
}
