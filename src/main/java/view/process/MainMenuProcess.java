package view.process;

import controller.user.IOAccountControl;
import view.FunctioningOption;

import java.util.HashMap;

public class MainMenuProcess extends Processor{
    private static IOAccountControl accountControl = IOAccountControl.getInstance();
    private static MainMenuProcess mainMenuProcess;

    private MainMenuProcess() {
        this.functionHashMap = new HashMap<String, FunctioningOption>();

        functionHashMap.put("user menu", new FunctioningOption() {
            public String dosth() {
                return whichMenuToGo();
            }
        });
    }

    public static MainMenuProcess getInstance(){
        if(mainMenuProcess == null)
            mainMenuProcess = new MainMenuProcess();

        return MainMenuProcess.mainMenuProcess;
    }

    private String whichMenuToGo(){
        return "";
        //TODO
    }
}
