package view.process.person;


import controller.account.VendorControl;
import notification.Notification;
import view.menu.ListicMenu;
import view.menu.Menu;
import view.process.FunctioningOption;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class VendorProcessor extends AccountProcessor {
    private static VendorControl vendorControl = VendorControl.getController();
    private static VendorProcessor vendorProcessor = null;
    private static Off off = null;

    private VendorProcessor(){
        super();
        this.functionsHashMap.put("Manage Products", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return manageProducts();
            }
        });
        this.functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return back();
            }
        };
        this.functionsHashMap.put("Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addOffName();
            }
        });
        this.functionsHashMap.put("Start Date", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addOffDate(true);
            }
        });
        this.functionsHashMap.put("Finish Date", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addOffDate(false);
            }
        });
        this.functionsHashMap.put("Off Percent", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addOffPercent();
            }
        });
        this.functionsHashMap.put("Add Off Products", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Add Products To Off Listic Menu");
            }
        });
        this.functionsHashMap.put("Confirm", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return confirmOff();
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

    public Menu back() {
        off = null;
        return ListicMenu.makeListicMenu("Manage Offs Listic Menu");
    }

    public Menu addOffName() {
        System.out.println("Please Enter The Name Of Your Off :");
        off.setName(scanner.nextLine().trim());
        return Menu.makeMenu("Add Off Menu");
    }

    public Menu addOffDate(boolean isStart) {
        if(isStart)
            System.out.println(System.out.println("* If You Leave The Start Date Free, It Will Automatically Set To Current Date *"););

        System.out.println("Please Enter The Date In This Format :");
        System.out.println("yyyy/MM/dd HH:mm:ss");
        String dateString = scanner.nextLine().trim();

        System.out.println(setOffDate(dateString, isStart));
        return Menu.makeMenu("Add Off Menu");
    }

    public String setOffDate(String dateString, boolean isStart) {
        String dateRegex = "yyyy/MM/dd HH:mm:ss";

        try {
            Date date = new Date(new SimpleDateFormat(dateRegex).parse(dateString).getTime());

            if(isStart) {
                if(off.getFinishDate() != null && date.compareTo(off.getFinishDate()) != -1)
                    return "The Start Date Must Be Before Finish Date";
                else if(date.compareTo(new Date(new java.util.Date().getTime())) == -1)
                    return "The Start Date Must Be after Current Time";
                else
                    off.setStartDate(date);
                return "Start Date Set";
            } else {
                if(off.getStartDate() != null && date.compareTo(off.getStartDate()) != 1)
                    return "The Finish Date Must Be After Start Date.";
                else if(date.compareTo(new Date(new java.util.Date().getTime())) != 1)
                    return "The Finish Date Must Be After Current Date.";
                else
                    off.setFinishDate(date);
                return "Finish Date Set.";
            }

        } catch (ParseException e) {
            System.out.println("Wrong Date Format");
        }

        return "Wrong Date Format";
    }

    public Menu addOffPercent() {
        double offPercent = 0;

        System.out.println("Please Enter The Off Percent : ");

        try {
           offPercent = Double.parseDouble(scanner.nextLine().trim());

           if(offPercent > 100 || offPercent <= 0)
               System.out.println("Off Percent Must Be Between 0 And 100, Obviously 😐");
           else
               off.setOffPercent(offPercent);
        } catch (NumberFormatException e) {
            System.out.println("Wrong Percent Format");
        }

        return Menu.makeMenu("Add Off Menu");
    }

    public Menu confirmOff() {
        Notification notification = vendorControl.addOff(off);
        System.out.println(notification.getMessage());

        if(notification.equals(Notification.ADD_OFF)) {
            off = null;
            return ListicMenu.makeListicMenu("Manage Offs Listic Menu");
        }

        return Menu.makeMenu("Add Off Menu");
    }

    public static Off getOff() {
        return off;
    }
}
