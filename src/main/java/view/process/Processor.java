package view.process;

import view.FunctioningOption;

import java.util.HashMap;
import java.util.Scanner;

public abstract class Processor {
    protected static HashMap<String, Processor> processorsHashMap = new HashMap<String, Processor>();
    protected static Scanner scanner = new Scanner(System.in);
    protected HashMap<String, FunctioningOption> functionHashMap;

    public static Processor findProcessorWithName(String name){
        return processorsHashMap.get(name);
    }

    public static void initProcessorsHashMap() {
        //TODO
    }

    public boolean isThereFunctionWithName(String functionName){
        return functionHashMap.containsKey(functionName.toLowerCase());
    }

    public String executeTheFunctionWithName(String functionName){
        return functionHashMap.get(functionName.toLowerCase()).dosth();
    }

}
