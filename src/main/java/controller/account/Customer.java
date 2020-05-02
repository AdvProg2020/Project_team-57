package controller.account;

public class Customer extends AccountControl{
    private static Customer customerControl = null;

    public static Customer getController() {
        if (customerControl == null)
            customerControl = new Customer();

        return customerControl;
    }

}
