package view.process;

import view.Menu;

import java.util.HashMap;

public abstract class Processor {
    private static HashMap<String, Processor> processesHashMap;
    protected HashMap<String, FunctioningOption> functionsHashMap;

    public static void initProcessHashMaps(){
        processesHashMap = new HashMap<String, Processor>();
        processesHashMap.put("MainMenuProcessor", MainMenuProcessor.getInstance());
        processesHashMap.put("IOAccountProcessor", IOAccountProcessor.getInstance());
        //TODO
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
