package view.process;

import controller.Control;
import controller.IOControl;
import model.existence.Account;
import view.Menu;

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
        int input;

        ArrayList<String> availableTypes = new ArrayList<String>();

        if(!ioControl.isThereAdmin())
            availableTypes.add("Admin");

        availableTypes.add("Vendor");
        availableTypes.add("Customer");

        while(flag){
            System.out.println("Please Enter The Type Of Your Account :");

            for(int i = 0; i < availableTypes.size(); i++)
                System.out.println((i + 1) + ". " + availableTypes.get(i));

            System.out.println((availableTypes.size() + 1) + ". Cancel");

            try {
                input = Integer.parseInt(Menu.getScanner().nextLine().trim());

                if(input == availableTypes.size() + 1)
                    return Menu.makeMenu("IO Menu");
                else if(input < 0 || input > availableTypes.size())
                    throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");

                type = availableTypes.get(input - 1);
                flag = false;

            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (Menu.InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
        }

        account.setType(type);

        System.out.println("Please Enter Your UserName :");
        account.setUsername(Menu.getScanner().nextLine().trim());

        System.out.println("Please Enter Your PassWord :");
        account.setPassword(Menu.getScanner().nextLine().trim());

        System.out.println(ioControl.register(account).getMessage());
        return Menu.makeMenu("IO Menu");
    }

    public Menu login(){
        Account account = new Account();

        System.out.println("Please Enter Your UserName :");
        account.setUsername(Menu.getScanner().nextLine().trim());

        System.out.println("Please Enter Your PassWord :");
        account.setPassword(Menu.getScanner().nextLine().trim());

        System.out.println(ioControl.login(account).getMessage());
        return MainMenuProcessor.getInstance().iOManage();
    }

}
