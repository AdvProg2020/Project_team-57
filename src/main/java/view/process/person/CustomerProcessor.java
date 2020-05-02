package view.process.person;


import controller.account.Customer;

public class CustomerProcessor extends AccountProcessor {
    private static Customer customer = Customer.getController();
    private static CustomerProcessor customerProcessor = null;

    private CustomerProcessor(){
        super();

    }

    public static CustomerProcessor getInstance(){
        if(customerProcessor == null)
            customerProcessor = new CustomerProcessor();

        return customerProcessor;
    }
}
