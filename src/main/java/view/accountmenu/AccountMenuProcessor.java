package view.accountmenu;

import controller.AccountControl;
import view.FunctioningOption;

import java.util.HashMap;
import java.util.Scanner;

public class AccountMenuProcessor {
    private static AccountControl accountControl = AccountControl.getInstance();
    private static AccountMenuProcessor accountMenuProcessor;
    private static Scanner scanner = new Scanner(System.in);
    private HashMap<String, FunctioningOption> functionHashMap;

    public boolean isThereFunctionWithName(String functionName){
        return functionHashMap.containsKey(functionName.toLowerCase());
    }

    public void executeTheFunctionWithName(String functionName){
        functionHashMap.get(functionName.toLowerCase()).dosth();
    }

    private AccountMenuProcessor(){
        this.functionHashMap = new HashMap<String, FunctioningOption>();
        functionHashMap.put("register", new FunctioningOption(){
            public void dosth() {
                register();
            }
        });

        functionHashMap.put("login", new FunctioningOption() {
            public void dosth() {
                login();
            }
        });


    }

    public static AccountMenuProcessor getInstance(){
        if(accountMenuProcessor == null)
            return new AccountMenuProcessor();

        return AccountMenuProcessor.accountMenuProcessor;
    }

    public void register(){
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
    }

    public boolean checkNumber(int number, int minimum, int maximum){
        if(number < minimum || number > maximum) {
            System.out.println("number is not valid");
            return false;
        }

        return true;
    }

    public void login(){
        String username, password;

        System.out.println("please enter your username: ");
        username = scanner.nextLine().trim();

        System.out.println("please enter your password: ");
        password = scanner.nextLine().trim();

        System.out.println(accountControl.login(username, password).getMessage());
    }

}
