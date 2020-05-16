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
        functionsHashMap.put("Address", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("Address");
            }
        });
        functionsHashMap.put("Email", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("Email");
            }
        });
        functionsHashMap.put("Postal Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("Postal Code");
            }
        });
        functionsHashMap.put("PhoneNumber (If You Are Hot 😂)", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ReceiveBullShitDontSave("PhoneNumber");
            }
        });
        functionsHashMap.put("Confirm", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeMenu("Select Discount Listic Menu");
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

    public static Menu ReceiveBullShitDontSave(String information) {
        System.out.println("0. Back");
        System.out.println("Please Enter Your " + information + " :");
        String command = scanner.nextLine().trim();
        return Menu.makeMenu("Receiving Information Menu");
    }
}
