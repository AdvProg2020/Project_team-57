package view.process.person;


public class CustomerProcessor extends AccountProcessor {
    private static CustomerControl customerControl = CustomerControl.getController();
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
