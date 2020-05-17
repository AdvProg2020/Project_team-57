package view.process;

import controller.account.AdminControl;
import model.existence.Discount;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.person.AdminProcessor;

import java.util.HashMap;

public class DiscountProcessor extends Processor {
    private static DiscountProcessor discountProcessor = null;
    private static AdminControl adminControl = AdminControl.getController();

    private DiscountProcessor() {
        functionsHashMap = new HashMap<>();
        functionsHashMap.put("Edit Discount", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editDiscount(objects);
            }
        });
        functionsHashMap.put("Remove Discount", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeDiscount(objects);
            }
        });

    }

    public static DiscountProcessor getInstance() {
        if(discountProcessor == null)
            discountProcessor = new DiscountProcessor();

        return discountProcessor;
    }

    public static void setMenu(ListicOptionMenu discountMenu, String discountID) {
        discountMenu.setOption(adminControl.getDiscountByID(discountID));
    }

    public static Menu editDiscount(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Discount discount = (Discount) objects[1];

        AdminProcessor.setEditingDiscount(discount.getID());
        AdminProcessor.setEditDiscountParentMenu(menu);
        Menu nextMenu = Menu.makeMenu("Edit Discount Menu");
        return nextMenu;
    }

    public static Menu removeDiscount(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Discount discount = (Discount) objects[1];

        System.out.println(adminControl.removeDiscountByID(discount.getID()).getMessage());
        return  menu.getParentMenu();
    }

}
