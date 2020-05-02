package view.process;

import view.Menu;
import view.process.person.AdminProcessor;
import view.process.person.CustomerProcessor;
import view.process.person.VendorProcessor;

import java.util.HashMap;
import java.util.Scanner;

public abstract class Processor {
    protected static Scanner scanner = Menu.getScanner();
    private static HashMap<String, Processor> processesHashMap;
    protected HashMap<String, FunctioningOption> functionsHashMap;

    public static void initProcessHashMaps(){
        processesHashMap = new HashMap<String, Processor>();
        processesHashMap.put("MainMenuProcessor", MainMenuProcessor.getInstance());
        processesHashMap.put("IOProcessor", IOProcessor.getInstance());
        processesHashMap.put("AdminProcessor", AdminProcessor.getInstance());
        processesHashMap.put("VendorProcessor", VendorProcessor.getInstance());
        processesHashMap.put("CustomerProcessor", CustomerProcessor.getInstance());

    }

    public static Processor findProcessorWithName(String name){
        return processesHashMap.get(name);
    }

    public boolean isThereFunctionWithName(String name){
        return functionsHashMap.containsKey(name);
    }

    public Menu executeTheFunctionWithName(String name) {
        return functionsHashMap.get(name).doTheThing();
    }
}
