package controller.account;

public class Vendor extends AccountControl{
    private static Vendor vendorControl = null;

    public static Vendor getController() {
        if (vendorControl == null)
            vendorControl = new Vendor();

        return vendorControl;
    }

}
