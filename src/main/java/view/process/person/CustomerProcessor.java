package view.process.person;


import controller.account.CustomerControl;
import model.existence.Discount;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.FunctioningOption;

public class CustomerProcessor extends AccountProcessor {
    private static CustomerControl customerControl = CustomerControl.getController();
    private static CustomerProcessor customerProcessor = null;

    private CustomerProcessor(){
        super();
        this.functionsHashMap.put("View Cart", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("View Cart Listic Menu");
            }
        });
        this.functionsHashMap.put("View Discount Codes", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("View Customer Discount Codes Listic Menu");
            }
        });
        functionsHashMap.put("Address", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("Address");
            }
        });
        functionsHashMap.put("Email", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("Email");
            }
        });
        functionsHashMap.put("Postal Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("Postal Code");
            }
        });
        functionsHashMap.put("PhoneNumber (If You Are Hot 😂)", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("PhoneNumber");
            }
        });
        functionsHashMap.put("Confirm", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeMenu("Select Discount Listic Menu");
            }
        });

    }

    public static CustomerProcessor getInstance(){
        if(customerProcessor == null)
            customerProcessor = new CustomerProcessor();

        return customerProcessor;
    }

    public static Menu ReceiveBullShitDontSave(String information) {
        System.out.println("0. Back");
        System.out.println("Please Enter Your " + information + " :");
        String command = scanner.nextLine().trim();
        return Menu.makeMenu("Receiving Information Menu");
    }

}
