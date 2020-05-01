package view.process;

import controller.IOControl;
import model.existence.Account;
import view.Menu;

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

        while(flag){
            flag = false;
            System.out.println("Please Enter The Type Of Your Account :" +
                    "\n1. Admin \n2. Vendor \n3. Customer");

            try {
                input = Integer.parseInt(Menu.getScanner().nextLine().trim());

                switch (input) {
                    case 1:
                        type = "Admin";
                        break;
                    case 2:
                        type = "Vendor";
                        break;
                    case 3:
                        type = "Customer";
                        break;
                    default:
                        flag = true;
                        throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (Menu.InputIsBiggerThanExistingNumbers e) {
                e.printStackTrace();
            }
        }

        account.setType(type);

        System.out.println("Please Enter Your UserName :");
        account.setUsername(Menu.getScanner().nextLine().trim());

        System.out.println("Please Enter Your PassWord :");
        account.setPassword(Menu.getScanner().nextLine().trim());

        System.out.println(ioControl.register(account).getMessage());
        return Menu.makeMenu("IOAccount Menu");
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
