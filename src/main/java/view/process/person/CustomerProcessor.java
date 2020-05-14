package view.process.person;


import controller.account.CustomerControl;
import view.menu.ListicMenu;
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
    }

    public static CustomerProcessor getInstance(){
        if(customerProcessor == null)
            customerProcessor = new CustomerProcessor();

        return customerProcessor;
    }

}
