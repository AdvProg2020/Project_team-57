package view.process.person;


import controller.account.VendorControl;
import view.menu.ListicMenu;
import view.menu.Menu;
import view.process.FunctioningOption;


public class VendorProcessor extends AccountProcessor {
    private static VendorControl vendorControl = VendorControl.getController();
    private static VendorProcessor vendorProcessor = null;

    private VendorProcessor(){
        super();
        this.functionsHashMap.put("Manage Products", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageProducts();
            }
        });

    }

    public static VendorProcessor getInstance(){
        if(vendorProcessor == null)
            vendorProcessor = new VendorProcessor();

        return vendorProcessor;
    }

    public Menu manageProducts()
    {
        return ListicMenu.makeListicMenu("Manage Products Listic Menu");
    }
}
