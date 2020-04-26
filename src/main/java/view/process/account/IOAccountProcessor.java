package view.process.account;

import controller.user.IOAccountControl;
import view.FunctioningOption;
import view.process.MainMenuProcessor;
import view.process.Processor;

import java.util.HashMap;
import java.util.Scanner;

public class IOAccountProcessor extends Processor {
    private static IOAccountControl accountControl = IOAccountControl.getInstance();
    private static IOAccountProcessor iOAccountProcessor;

    private IOAccountProcessor(){
        this.functionHashMap = new HashMap<String, FunctioningOption>();
        functionHashMap.put("register", new FunctioningOption(){
            public String dosth() {
                return register();
            }
        });

        functionHashMap.put("login", new FunctioningOption() {
            public String dosth() {
                return login();
            }
        });


    }

    public static IOAccountProcessor getInstance(){
        if(iOAccountProcessor == null)
             iOAccountProcessor = new IOAccountProcessor();

        return IOAccountProcessor.iOAccountProcessor;
    }

    public String register(){
        String username, password;
        int type;

        HashMap<Integer, String> types = new HashMap<Integer, String>();
        types.put(1, "manager");
        types.put(2, "seller");
        types.put(3, "buyer");

        System.out.println("please enter your username :");
        username = scanner.nextLine().trim();

        System.out.println("please enter your password :");
        password = scanner.nextLine();

        System.out.println("please enter your type : \n1. manager \n2. seller \n3. buyer");

        try {
            type = Integer.parseInt(scanner.nextLine().trim());

            if(checkNumber(type = Integer.parseInt(scanner.nextLine().trim()), 0, 4))
                System.out.println(accountControl.register(username, password, types.get(type)).getMessage());

        } catch (NumberFormatException e){
            System.out.println("what you have typed is not a number.");
        }

        return "IOAccount Menu";
    }

    public boolean checkNumber(int number, int minimum, int maximum){
        if(number < minimum || number > maximum) {
            System.out.println("number is not valid");
            return false;
        }

        return true;
    }

    public String login(){
        String username, password;

        System.out.println("please enter your username: ");
        username = scanner.nextLine().trim();

        System.out.println("please enter your password: ");
        password = scanner.nextLine().trim();

        System.out.println(accountControl.login(username, password).getMessage());
        return MainMenuProcessor.getInstance().executeTheFunctionWithName("user menu");
    }

}
