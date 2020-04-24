package view.accountmenu;

import view.FunctioningOption;

import java.util.HashMap;
import java.util.Scanner;

public class AccountMenuProcessor {
    private static AccountMenuProcessor accountMenuProcessor;
    private static Scanner scanner = new Scanner(System.in);
    HashMap<String, FunctioningOption> functionHashMap;

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

    public AccountMenuProcessor getInstance(){
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
                if(checkRegisterError(username, password, types.get(type))){

                }

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

    public boolean checkRegisterError(String username, String password, String type) {
        return true;
    }

    public void login(){
        String username, password;

        System.out.println("please enter your username: ");
        username = scanner.nextLine().trim();

        System.out.println("please enter your password: ");
        password = scanner.nextLine().trim();

        if(checkLoginError(username, password)){

        }
    }

    public boolean checkLoginError(String username, String password) {
        return true;
    }
}
