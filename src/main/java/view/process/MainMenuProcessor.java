package view.process;

import controller.user.IOAccountControl;
import view.FunctioningOption;

import java.util.HashMap;

public class MainMenuProcessor extends Processor{
    private static IOAccountControl ioAccountControl = IOAccountControl.getInstance();
    private static MainMenuProcessor mainMenuProcessor;

    private MainMenuProcessor() {
        this.functionHashMap = new HashMap<String, FunctioningOption>();

        functionHashMap.put("user menu", new FunctioningOption() {
            public String dosth() {
                return whichMenuToGo();
            }
        });
    }

    public static MainMenuProcessor getInstance(){
        if(mainMenuProcessor == null)
            mainMenuProcessor = new MainMenuProcessor();

        return MainMenuProcessor.mainMenuProcessor;
    }

    private String whichMenuToGo(){
        if(ioAccountControl.isLogedIn())
            return ioAccountControl.getTypeOfClient() + " Menu";

        return "IOAccount Menu";
    }
}
