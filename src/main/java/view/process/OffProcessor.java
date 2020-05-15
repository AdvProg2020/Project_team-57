package view.process;

import controller.account.AdminControl;
import controller.account.VendorControl;
import model.existence.Off;
import model.existence.Product;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.person.VendorProcessor;

import java.util.HashMap;

public class OffProcessor extends Processor{
    private static OffProcessor offProcessor = null;
    private static VendorControl vendorControl = VendorControl.getController();
    private static AdminControl adminControl = AdminControl.getController();

    private OffProcessor() {
        this.functionsHashMap = new HashMap<>();
        this.functionsHashMap.put("Add To Off", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addToOff(objects);
            }
        });
        this.functionsHashMap.put("Remove From Off", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeFromOff(objects);
            }
        });
        this.functionsHashMap.put("Accept Request", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return modifyRequest(true, objects);
            }
        });
        this.functionsHashMap.put("Decline Request", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return modifyRequest(false, objects);
            }
        });

    }

    public static OffProcessor getInstance() {
        if(offProcessor == null)
            offProcessor = new OffProcessor();

        return offProcessor;
    }

    public static void setMenu(ListicOptionMenu listicOptionMenu, String offID) {
        listicOptionMenu.setOption(adminControl.getOffByID(offID));
    }

    public Menu addToOff(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Product product = (Product) objects[1];
        VendorProcessor.getOff().addProductToOff(product.getID());
        return menu.getParentMenu();
    }

    public Menu removeFromOff(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Product product = (Product) objects[1];
        VendorProcessor.getOff().removeProductFromOff(product.getID());
        return menu.getParentMenu();
    }

    public Menu modifyRequest(boolean modification, Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Off off = (Off) objects[1];
        System.out.println(adminControl.modifyOffApprove(off.getOffID(), modification).getMessage());
        return menu.getParentMenu();
    }

}
