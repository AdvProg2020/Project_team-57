package view.process.person;


import controller.account.AdminControl;
import model.existence.Category;
import model.existence.Discount;
import view.menu.ListicMenu;
import view.menu.Menu;
import view.process.FunctioningOption;


public class AdminProcessor extends AccountProcessor {
    private static AdminControl adminControl = AdminControl.getController();
    private static AdminProcessor adminProcessor = null;
    private static Category category;
    private static Discount discount;

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
        this.functionsHashMap.put("Add Customer To Discount Code", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Add Customers To Discount Code Listic Menu");
            }
        });
    }

    public static AccountProcessor getInstance(){
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
    }


}
