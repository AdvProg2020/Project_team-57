package view.process;

import view.menu.Menu;
import view.process.person.AdminProcessor;
import view.process.person.CustomerProcessor;
import view.process.person.VendorProcessor;

import java.util.ArrayList;
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
        processesHashMap.put("ListicProcessor", ListicProcessor.getInstance());
        processesHashMap.put("ProductProcessor", ProductProcessor.getInstance());
        processesHashMap.put("RegisterRequestProcessor", RegisterProcessor.getInstance());
        processesHashMap.put("RequestProcessor", RequestProcessor.getInstance());

    }

    public static Processor findProcessorWithName(String name){
        return processesHashMap.get(name);
    }

    public boolean isThereFunctionWithName(String name){
        return functionsHashMap.containsKey(name);
    }

    public Menu executeTheFunctionWithName(String name, Object... objects) {
        return functionsHashMap.get(name).doTheThing(objects);
    }

    protected String setOptionsForArrayList(ArrayList<String> options){
        int option = 0;

        for(int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));

        System.out.println((options.size() + 1) + ". Cancel");

        try {
            option = Integer.parseInt(scanner.nextLine().trim());

            if(option == options.size() + 1)
                return "Cancel";
            else if(option <= 0 || option > options.size())
                throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");

            return options.get(option - 1);

        } catch (NumberFormatException e) {
            System.out.println("Please Enter An Integer");
        } catch (NullPointerException e) {
            System.out.println("Please Enter An Integer");
        } catch (Menu.InputIsBiggerThanExistingNumbers e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
