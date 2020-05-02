package view.process.person;

import controller.Control;
import controller.account.AccountControl;
import model.existence.Account;
import view.Menu;
import view.process.FunctioningOption;
import view.process.Processor;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountProcessor extends Processor {
    private static AccountControl accountControl = AccountControl.getController();

    protected AccountProcessor(){
        functionsHashMap = new HashMap<String, FunctioningOption>();
        functionsHashMap.put("View Personal Info", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                return showPersonalInfo();
            }
        });
        functionsHashMap.put("Edit Field", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                return editField();
            }
        });
        functionsHashMap.put("Change Password", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                return changePassword();
            }
        });
        functionsHashMap.put("Edit Credit", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                return editCredit();
            }
        });

    }

    public Menu showPersonalInfo(){
        Account account = accountControl.getAccount();
        createCustomLine();

        printWithNullChecking("UserName", account.getUsername());
        createCustomLine();

        printWithNullChecking("PassWord", "************");
        createCustomLine();

        printWithNullChecking("Account Type", account.getType());
        createCustomLine();

        printWithNullChecking("FirstName", account.getFirstName());
        createCustomLine();

        printWithNullChecking("LastName", account.getLastName());
        createCustomLine();

        printWithNullChecking("Email", account.getEmail());
        createCustomLine();

        if(Control.getType().equals("Vendor")){
            printWithNullChecking("Brand", account.getBrand());
            createCustomLine();
        }

        System.out.format("| %-15s | %-35f | %n", "Credit", account.getCredit());
        createCustomLine();

        return Menu.makeMenu(Control.getType() + " Menu");
    }

    private void createCustomLine(){
        System.out.println("+-----------------+-------------------------------------+");
    }

    private void printWithNullChecking(String fieldName, String fieldValue){
        if(fieldValue == null)
            System.out.format("| %-15s | %-35s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-15s | %-35s | %n", fieldName, fieldValue);
    }

    public Menu editField(){
        String fieldName = null, fieldValue = null;
        boolean flag = true;

        ArrayList<String> availableFields = new ArrayList<String>();
        availableFields.add("FirstName");
        availableFields.add("LastName");
        availableFields.add("Email");

        if(Control.getType().equals("Vendor"))
            availableFields.add("Brand");

        while(flag) {
            System.out.println("Please Enter The Field You Wanna Edit :");

            fieldName = setOptionsForArrayList(availableFields);

            if(fieldName == "Cancel")
                return Menu.makeMenu(Control.getType() + " Menu");
            else if(fieldName != null)
                flag = false;
        }

        System.out.println("Please Enter The New Value Of Your Field :");
        fieldValue = Menu.getScanner().nextLine().trim();

        System.out.println(accountControl.editField(fieldName, fieldValue).getMessage());
        return Menu.makeMenu(Control.getType() + " Menu");
    }

    public Menu changePassword(){
        String oldPassWord, newPassWord;

        System.out.println("Please Enter Your Old PassWord :");
        oldPassWord = Menu.getScanner().nextLine().trim();

        System.out.println("Please Enter Your New PassWord :");
        newPassWord = Menu.getScanner().nextLine().trim();

        System.out.println(accountControl.changePassword(oldPassWord, newPassWord).getMessage());
        return Menu.makeMenu(Control.getType() + " Menu");
    }

    public Menu editCredit(){
        double transactionMoney = 0;
        String creditOption = null;
        boolean flag = true;

        ArrayList<String> creditOptions = new ArrayList<String>();
        creditOptions.add("Add Money");
        creditOptions.add("Reduce Money");

        while(flag) {
            System.out.println("Do You Wanna Add Money or Reduce Money ?");

            creditOption = setOptionsForArrayList(creditOptions);

            if(creditOption == "Cancel")
                return Menu.makeMenu(Control.getType() + " Menu");
            else if(creditOption != null)
                flag = false;

        }

        flag = true;
        while(flag){
            System.out.println("Please Enter The Transaction Money :");

            try {
                transactionMoney = Double.parseDouble(scanner.nextLine().trim());
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            }
        }

        if(creditOption.equals("Add Money")){
            System.out.println(accountControl.addMoney(transactionMoney).getMessage());
        } else {
            System.out.println(accountControl.getMoney(transactionMoney).getMessage());
        }

        return Menu.makeMenu(Control.getType() + " Menu");
    }

}
