package controller.account;

public class VendorControl extends AccountControl{
    private static VendorControl vendorControl = null;

    public static VendorControl getController() {
        if (vendorControl == null)
            vendorControl = new VendorControl();

        return vendorControl;
    }

}
