package view.process.person;


import controller.account.AdminControl;
import model.existence.Category;
import model.existence.Discount;
import notification.Notification;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.process.FunctioningOption;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdminProcessor extends AccountProcessor {
    private static AdminControl adminControl = AdminControl.getController();
    private static AdminProcessor adminProcessor = null;
    private static Category category;
    private static Discount discount;
    private static Date currentDateForDiscount;
    private static String editingDiscount;
    private static ListicOptionMenu editDiscountParentMenu;

    private AdminProcessor(){
        super();
        functionsHashMap.put("Manage All Products", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage All Products Listic Menu");
            }
        });
        functionsHashMap.put("Manage All Users", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage All Users Listic Menu");
            }
        });
        functionsHashMap.put("View All Admins", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("View All Admins Listic Menu");
            }
        });
        functionsHashMap.put("Manage Categories", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Categories Listic Menu");
            }
        });
        functionsHashMap.put("Add Category Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addCategoryName();
            }
        });
        functionsHashMap.put("Add Category Features", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addCategoryFeatures();
            }
        });
        functionsHashMap.put("Add Category Parent Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addCategoryParent();
            }
        });
        functionsHashMap.put("Confirm Adding Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return confirmCategory();
            }
        });
        this.functionsHashMap.put("Manage Discount Codes", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Manage Discount Codes Listic Menu");
            }
        });
        this.functionsHashMap.put("Add Customers To Discount Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Add Customers To Discount Code Listic Menu");
            }
        });
        this.functionsHashMap.put("Add Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addCodeToDiscount();
            }
        });
        this.functionsHashMap.put("Add Start Date", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addDateDiscount(true);
            }
        });
        this.functionsHashMap.put("Add Finish Date", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addDateDiscount(false);
            }
        });
        functionsHashMap.put("Add Discount Percent", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addPercentToDiscount();
            }
        });
        this.functionsHashMap.put("Add Maximum Value", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addMaxValue();
            }
        });
        this.functionsHashMap.put("Add Maximum Repetition", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addMaxRepetition();
            }
        });
        this.functionsHashMap.put("Confirm Creating Discount", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return confirmDiscount();
            }
        });
        this.functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                editDiscountParentMenu.setOption(adminControl.getDiscountByID(editingDiscount));
                return editDiscountParentMenu;
            }
        });
        this.functionsHashMap.put("Edit Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editCodeDiscount();
            }
        });
        this.functionsHashMap.put("Edit Finish Date", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editFinishDateDiscount();
            }
        });
        this.functionsHashMap.put("Edit Discount Percentage", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editPercentDiscount();
            }
        });
        this.functionsHashMap.put("Edit Maximum Discount Value", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editMaxValDiscount();
            }
        });
        this.functionsHashMap.put("Edit Maximum Repetition", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editMaxRepetitionDiscount();
            }
        });
    }

    public Menu editMaxRepetitionDiscount() {
        Menu nextMenu = Menu.makeMenu("Edit Discount Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Discount Maximum Repetition More Than 0: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            System.out.println(adminControl.editMaxRepetition(editingDiscount, Integer.parseInt(command)).getMessage());
        } catch (NumberFormatException e)
        {
            System.out.println("!Wrong Repetition Format!");
        }
        return nextMenu;
    }

    public Menu editMaxValDiscount() {
        Menu nextMenu = Menu.makeMenu("Edit Discount Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Discount Maximum Value: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            System.out.println(adminControl.editMaxDiscount(editingDiscount, Double.parseDouble(command)).getMessage());
        } catch (NumberFormatException e)
        {
            System.out.println("!Wrong Value Format!");
        }
        return nextMenu;
    }

    public Menu editPercentDiscount() {
        Menu nextMenu = Menu.makeMenu("Edit Discount Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Discount Percentage More Than 0 And Less Than Or Equal To 100: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            System.out.println(adminControl.editDiscountPercent(editingDiscount, Double.parseDouble(command)).getMessage());
        } catch (NumberFormatException e)
        {
            System.out.println("!Wrong Percentage Format!");
        }
        return nextMenu;
    }

    public Menu editFinishDateDiscount() {
        Menu nextMenu = Menu.makeMenu("Edit Discount Menu");
        String regex = "yyyy/MM/dd HH:mm:ss";
        System.out.println("0. Back");
        System.out.println("Please Enter New Finish Code In This Format: ");
        System.out.println(regex);
        try {
            String command = scanner.nextLine().trim();
            if(command.equals("0"))
                return nextMenu;
            Date date = new Date(new SimpleDateFormat(regex).parse(command).getTime());
            System.out.println(adminControl.editFinishDate(editingDiscount, date).getMessage());
        } catch (ParseException e) {
            System.out.println("!Wrong Date Format!");
        }
        return nextMenu;
    }

    public Menu editCodeDiscount() {
        Menu nextMenu = Menu.makeMenu("Edit Discount Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter New Discount Code: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        System.out.println(adminControl.editCode(editingDiscount, command).getMessage());
        return nextMenu;
    }

    public static AdminProcessor getInstance(){
        if(adminProcessor == null)
            adminProcessor = new AdminProcessor();

        return adminProcessor;
    }

    public Menu addCategoryName()
    {
        System.out.println("Please Enter Category Name: ");
        category.setName(scanner.nextLine());
        return Menu.makeMenu("Add Category Menu");
    }

    public Menu addCategoryFeatures()
    {
        System.out.println("Please Enter Features Of This Category: ");
        category.setFeatures(scanner.nextLine());
        return Menu.makeMenu("Add Category Menu");
    }

    public Menu addCategoryParent()
    {
        System.out.println("Please Enter Parent Category Of This Category: ");
        category.setParentCategory(scanner.nextLine());
        return Menu.makeMenu("Add Category Menu");
    }

    public Menu confirmCategory()
    {
        if(category.getName() != null && category.getFeatures() != null) {
            System.out.println(adminControl.addCategory(category).getMessage());
            return Menu.makeMenu("Manage Categories Listic Menu");
        } else {
            System.out.println("You Should First Fill Category Name And Features");
            return Menu.makeMenu("Add Category Menu");
        }
    }

    public static void newCategory()
    {
        category = new Category();
    }

    public static void newDiscount(){
        discount = new Discount();
        currentDateForDiscount = new Date(System.currentTimeMillis());
        discount.setStartDate(new Date(System.currentTimeMillis()));
    }

    public static boolean isThereCustomerInDiscount(String username)
    {
        return discount.getCustomersWithRepetition().keySet().contains(username);
    }

    public static void addCustomerToDiscount(String username)
    {
        discount.addCustomerWithRepetition(username, 0);
    }

    public static void removeCustomerFromDiscount(String username)
    {
        discount.removeCustomer(username);
    }

    public Menu addCodeToDiscount()
    {
        Menu nextMenu = Menu.makeMenu("Create Discount Code Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter The Discount Code: ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        discount.setCode(command);
        return nextMenu;
    }

    public Menu addDateDiscount(boolean isStart)
    {
        Menu nextMenu = Menu.makeMenu("Create Discount Code Menu");
        System.out.println("0.Back");
        if(isStart)
            System.out.println("* If You Leave The Start Date Free, It Will Automatically Set To Current Date *");
        System.out.println("Please Enter The Date In This Format:");
        System.out.println("yyyy/MM/dd HH:mm:ss");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        System.out.println(setDate(command, isStart));
        return nextMenu;
    }

    private String setDate(String command, boolean isStart) {
        String regex = "yyyy/MM/dd HH:mm:ss";
        try {
            Date date = new Date(new SimpleDateFormat(regex).parse(command).getTime());
            if(isStart) {
                if((discount.getFinishDate() != null && discount.getFinishDate().compareTo(date) != 1) || (date.compareTo(currentDateForDiscount) != 1))
                    return "Starting Date Must Be Before Finishing Date and After Current Date";
                discount.setStartDate(date);
                return "Date Set";
            } else {
                if(discount.getStartDate().compareTo(date) != -1)
                    return "Finish Date Must Be After Start Date";
                discount.setFinishDate(date);
                return "Date Set";
            }
        } catch (ParseException e) {
            return "!Wrong Date Format!";
        }
    }

    public Menu addPercentToDiscount()
    {
        Menu nextMenu = Menu.makeMenu("Create Discount Code Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter The Discount Percentage More Than 0, Less Than Or Equal To 100: ");
        try {
            String command = scanner.nextLine().trim();
            if(command.equals("0"))
                return nextMenu;
            double percent = Double.parseDouble(command);
            if(percent > 0 && percent <= 100)
            {
                discount.setDiscountPercent(percent);
                System.out.println("Discount Percentage Set");
                return nextMenu;
            }
        } catch (NumberFormatException e) {}
        System.out.println("!Wrong Percentage Format!");
        return nextMenu;
    }

    public Menu addMaxValue()
    {
        Menu nextMenu = Menu.makeMenu("Create Discount Code Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter Discount Code Maximum Value (Must Be More Than 0): ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            double maxVal = Double.parseDouble(command);
            if(maxVal > 0) {
                discount.setMaxDiscount(maxVal);
                System.out.println("Maximum Value Set");
                return nextMenu;
            }
        } catch (NumberFormatException e) {}
        System.out.println("!Wrong Value Format!");
        return nextMenu;
    }

    public Menu addMaxRepetition()
    {
        Menu nextMenu = Menu.makeMenu("Create Discount Code Menu");
        System.out.println("0. Back");
        System.out.println("Please Enter Discount Code Maximum Repetition (Must Be More Than 0): ");
        String command = scanner.nextLine().trim();
        if(command.equals("0"))
            return nextMenu;
        try {
            int maxRep = Integer.parseInt(command);
            if(maxRep > 0) {
                discount.setMaxRepetition(maxRep);
                System.out.println("Maximum Repetition Set");
                return nextMenu;
            }
        } catch (NumberFormatException e) {}
        System.out.println("!Wrong Repetition Format!");
        return nextMenu;
    }

    public Menu confirmDiscount()
    {
        Menu nextMenu = Menu.makeMenu("Create Discount Code Menu");
        Notification notification = adminControl.addDiscount(discount);
        System.out.println(notification.getMessage());

        if(notification.equals(Notification.ADD_DISCOUNT))
            nextMenu = ListicMenu.makeListicMenu("Manage Discount Codes Listic Menu");

        return nextMenu;
    }

    public static void setEditingDiscount(String editingDiscount) {
        AdminProcessor.editingDiscount = editingDiscount;
    }

    public static void setEditDiscountParentMenu(ListicOptionMenu editDiscountParentMenu) {
        AdminProcessor.editDiscountParentMenu = editDiscountParentMenu;
    }
}
