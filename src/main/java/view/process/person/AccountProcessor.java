package view.process.person;

import controller.Control;
import controller.account.AccountControl;
import model.existence.Account;
import view.Menu;
import view.process.FunctioningOption;
import view.process.Processor;

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

        System.out.format("| %-15s | %-35s | %n", "Username", account.getUsername());
        createCustomLine();

        System.out.format("| %-15s | %-35s | %n", "Password", "************");
        createCustomLine();

        System.out.format("| %-15s | %-35s | %n", "Account Type", account.getType());
        createCustomLine();

        System.out.format("| %-15s | %-35s | %n", "FirstName", account.getFirstName());
        createCustomLine();

        System.out.format("| %-15s | %-35s | %n", "LastName", account.getLastName());
        createCustomLine();

        System.out.format("| %-15s | %-35s | %n", "Email", account.getEmail());
        createCustomLine();

        if(Control.getType().equals("Vendor")){
            System.out.format("| %-15s | %-35s | %n", "Brand Name", account.getBrand());
            createCustomLine();
        }

        System.out.format("| %-15s | %-35f | %n", "Credit", account.getCredit());
        createCustomLine();

        return Menu.makeMenu(Control.getType() + " Menu");
    }

    private void createCustomLine(){
        System.out.println("+-----------------+-------------------------------------+");
    }

    public Menu editField(){
        String fieldName = null, fieldValue = null;
        boolean flag = true;
        int fieldNumber;

        while(flag) {
            flag = false;
            System.out.println("Please Enter The Field You Wanna Edit :" +
                    "\n1. FirstName" + "\n2. LastName" + "\n3. Email");
            if (Control.getType().equals("Vendor"))
                System.out.println("\n5. Brand Name");

            try {
                fieldNumber = Integer.parseInt(Menu.getScanner().nextLine().trim());

                if(fieldNumber == 1){
                    fieldName = "FirstName";
                } else if(fieldNumber == 2){
                    fieldName = "LastName";
                } else if(fieldNumber == 3){
                    fieldName = "Email";
                } else if(Control.getType().equals("Vendor") && fieldNumber == 4){
                    fieldName = "Brand";
                } else {
                    throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
                flag = true;
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
                flag = true;
            } catch (Menu.InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
                flag = true;
            }
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
        int input = 0;
        boolean flag = true;

        while(flag) {
            System.out.println("Do You Wanna Add Money or Reduce Money ?" +
                    "\n1. Add Money" + "\n2. Reduce Money");

            try {
                input = Integer.parseInt(Menu.getScanner().nextLine().trim());

                if(input != 1 && input != 2) {
                    throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                } else
                    flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (Menu.InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
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

        if(input == 1){
            System.out.println(accountControl.addMoney(transactionMoney).getMessage());
        } else {
            System.out.println(accountControl.getMoney(transactionMoney).getMessage());
        }

        return Menu.makeMenu(Control.getType() + " Menu");
    }

}
