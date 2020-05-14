package view.process;

import controller.account.VendorControl;
import model.existence.Product;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.HashMap;

public class OffProcessor extends Processor{
    private static OffProcessor offProcessor = null;
    private static VendorControl vendorControl = VendorControl.getController();

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

    }

    public static OffProcessor getInstance() {
        if(offProcessor == null)
            offProcessor = new OffProcessor();

        return offProcessor;
    }

    public Menu addToOff(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Product product = (Product) objects[1];
        vendorControl.addProductToOff(product.getID());
        return menu.getParentMenu();
    }

    public Menu removeFromOff(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Product product = (Product) objects[1];
        vendorControl.removeProductFromOff(product.getID());
        return menu.getParentMenu();
    }

}
