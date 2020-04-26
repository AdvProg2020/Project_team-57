package view.process.account;

import controller.user.ManagerControl;
import view.FunctioningOption;

import java.util.HashMap;

public class ManagerProcessor extends UserProcessor {
    private static ManagerControl managerControl;
    private static ManagerProcessor managerProcessor;

    private ManagerProcessor() {
        this.functionHashMap = new HashMap<String, FunctioningOption>();

        //TODO
    }

    public static ManagerProcessor getInstance(){
        if(managerProcessor == null)
            managerProcessor = new ManagerProcessor();

        return ManagerProcessor.managerProcessor;
    }
}
