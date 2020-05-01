package view.process;

import view.Menu;

import java.util.HashMap;

public class IOProcessor extends Processor {
    private static IOAccountController ioAccountController = IOAccountController.getInstance();
    private static IOProcessor ioProcessor = null;

    private IOProcessor(){
        functionsHashMap = new HashMap<String, FunctioningOption>();
        functionsHashMap.put("Register", new FunctioningOption() {
            public Menu doTheThing() {
                return register();
            }
        });
        functionsHashMap.put("Login", new FunctioningOption() {
            public Menu doTheThing() {
                return login();
            }
        });

    }

    public static IOProcessor getInstance(){
        if(ioProcessor == null)
            ioProcessor = new IOProcessor();

        return ioProcessor;
    }

    public Menu register(){

    }

    public Menu login(){

    }

}
