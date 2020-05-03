package controller.account;

public class CustomerControl extends AccountControl{
    private static CustomerControl customerControl = null;

    public static CustomerControl getController() {
        if (customerControl == null)
            customerControl = new CustomerControl();

        return customerControl;
    }

}
