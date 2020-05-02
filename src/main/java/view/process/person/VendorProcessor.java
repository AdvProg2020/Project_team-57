package view.process.person;


import controller.account.Vendor;

public class VendorProcessor extends AccountProcessor {
    private static Vendor vendorControl = Vendor.getController();
    private static VendorProcessor vendorProcessor = null;

    private VendorProcessor(){
        super();

    }

    public static VendorProcessor getInstance(){
        if(vendorProcessor == null)
            vendorProcessor = new VendorProcessor();

        return vendorProcessor;
    }

}
