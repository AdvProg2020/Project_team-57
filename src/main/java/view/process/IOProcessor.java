package view.process;

import controller.IOControl;
import model.existence.Account;
import view.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;

public class IOProcessor extends Processor {
    private static IOControl ioControl = IOControl.getController();
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
        Account account = new Account();
        boolean flag = true;
        String type = null;

        ArrayList<String> availableTypes = new ArrayList<String>();

        if(!ioControl.isThereAdmin())
            availableTypes.add("Admin");

        availableTypes.add("Vendor");
        availableTypes.add("Customer");

        while(flag){
            System.out.println("Please Enter The Type Of Your Account :");

            type = setOptionsForArrayList(availableTypes);

            if(type == "Cancel")
                return Menu.makeMenu("IO Menu");
            else if(type != null)
                flag = false;
        }

        account.setType(type);

        System.out.println("Please Enter Your UserName :");
        account.setUsername(scanner.nextLine().trim());

        System.out.println("Please Enter Your PassWord :");
        account.setPassword(scanner.nextLine().trim());

        System.out.println(ioControl.register(account).getMessage());
        return Menu.makeMenu("IO Menu");
    }

    public Menu login(){
        Account account = new Account();

        System.out.println("Please Enter Your UserName :");
        account.setUsername(scanner.nextLine().trim());

        System.out.println("Please Enter Your PassWord :");
        account.setPassword(scanner.nextLine().trim());

        System.out.println(ioControl.login(account).getMessage());
        return MainMenuProcessor.getInstance().iOManage();
    }

}
