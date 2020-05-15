package view.process;

import controller.account.AdminControl;
import controller.account.VendorControl;
import model.existence.Off;
import model.existence.Product;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.person.VendorProcessor;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class OffProcessor extends Processor{
    private static OffProcessor offProcessor = null;
    private static VendorControl vendorControl = VendorControl.getController();
    private static AdminControl adminControl = AdminControl.getController();
    private static String editingOff;
    private static ListicOptionMenu editingOffParentMenu;

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

        this.functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                editingOffParentMenu.setOption(vendorControl.getVendorOff(editingOff));
                return editingOffParentMenu;
            }
        });
        this.functionsHashMap.put("Edit Off Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editOffName();
            }
        });
        this.functionsHashMap.put("Edit Off Finish Date", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editOffFinishDate();
            }
        });
        this.functionsHashMap.put("Edit Off Percentage", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editOffPercent();
            }
        });

        this.functionsHashMap.put("Edit Off", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editOff(objects);
            }
        });
        this.functionsHashMap.put("Remove Off", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeOff(objects);
            }
        });

    }

    public static OffProcessor getInstance() {
        if(offProcessor == null)
            offProcessor = new OffProcessor();

        return offProcessor;
    }

    public static void setMenu(ListicOptionMenu listicOptionMenu, String offID) {
        if(vendorControl.isThereOffInEditingTable(offID))
            listicOptionMenu.setOption(vendorControl.getOffFromEditingTable(offID));
        else
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


    public Menu editOff(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Off off = (Off) objects[1];

        if (off.getStatus() == 2) {
            System.out.println("You Can't Edit This Off, Because It's Not Approved Yet.");
            return menu;
        }else {
            editingOff = off.getOffID();
            editingOffParentMenu = menu;
            return Menu.makeMenu("Edit Off Menu");
        }
    }

    public Menu removeOff(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Off off = (Off) objects[1];
        System.out.println(vendorControl.removeOffWithID(off.getOffID()).getMessage());
        return menu.getParentMenu();
    }

    public Menu editOffPercent() {
        Menu nextMenu = Menu.makeMenu("Edit Off Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Off Percentage: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            System.out.println(vendorControl.editOffPercent(editingOff, Double.parseDouble(command)).getMessage());
        } catch (NumberFormatException e) {
            System.out.println("!Wrong Percent Format!");
        }
        return nextMenu;
    }

    public Menu editOffFinishDate() {
        Menu nextMenu = Menu.makeMenu("Edit Off Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Off Finish Date In This Format: ");
        String regex = "yyyy/MM/dd HH:mm:ss";
        System.out.println(regex);
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            Date date = new Date(new SimpleDateFormat(regex).parse(command).getTime());
            System.out.println(vendorControl.editOffFinishDate(editingOff, date).getMessage());
            return nextMenu;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("!Wrong Date Format!");
        return nextMenu;
    }

    public Menu editOffName() {
        Menu nextMenu = Menu.makeMenu("Edit Off Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Off Name: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        System.out.println(vendorControl.editOffName(editingOff, command).getMessage());
        return nextMenu;
    }
}
