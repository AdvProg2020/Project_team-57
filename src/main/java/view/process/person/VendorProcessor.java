package view.process.person;


import controller.account.VendorControl;

public class VendorProcessor extends AccountProcessor {
    private static VendorControl vendorControl = VendorControl.getController();
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
