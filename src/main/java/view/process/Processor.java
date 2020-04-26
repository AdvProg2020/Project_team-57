package view.process;

import view.FunctioningOption;
import view.process.account.BuyerProcessor;
import view.process.account.IOAccountProcessor;
import view.process.account.ManagerProcessor;
import view.process.account.SellerProcessor;

import java.util.HashMap;
import java.util.Scanner;

public abstract class Processor {
    protected static HashMap<String, Processor> processorsHashMap = new HashMap<String, Processor>();
    protected static Scanner scanner = new Scanner(System.in);
    protected HashMap<String, FunctioningOption> functionHashMap;

    public static Processor findProcessorWithName(String name){
        return processorsHashMap.get(name.toLowerCase());
    }

    public static void initProcessorsHashMap() {
        processorsHashMap.put("MainMenuProcessor", MainMenuProcessor.getInstance());
        processorsHashMap.put("IOAccountProcessor", IOAccountProcessor.getInstance());
        processorsHashMap.put("ManagerProcessor", ManagerProcessor.getInstance());
        processorsHashMap.put("SellerProcessor", SellerProcessor.getInstance());
        processorsHashMap.put("BuyerProcessor", BuyerProcessor.getInstance());
        //TODO
    }

    public boolean isThereFunctionWithName(String functionName){
        return functionHashMap.containsKey(functionName.toLowerCase());
    }

    public String executeTheFunctionWithName(String functionName){
        return functionHashMap.get(functionName.toLowerCase()).dosth();
    }

}
