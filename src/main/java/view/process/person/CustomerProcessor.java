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
                return viewCart();
            }
        });
    }

    public static CustomerProcessor getInstance(){
        if(customerProcessor == null)
            customerProcessor = new CustomerProcessor();

        return customerProcessor;
    }

    public Menu viewCart()
    {
        //System.out.println(":|");
        return ListicMenu.makeListicMenu("View Cart Listic Menu");
    }
}
