package view.process;

import controller.account.VendorControl;
import controller.product.ProductControl;
import model.existence.Product;
import view.menu.ListicMenu;
import view.menu.Menu;

import java.util.HashMap;

public class AddProductProcessor extends Processor {
    private static VendorControl vendorControl = VendorControl.getController();
    private static AddProductProcessor addProductProcessor = null;
    private static ListicMenu parentMenu;
    private static Menu thisMenu = null;
    private static Product product;

    private AddProductProcessor(){
        functionsHashMap = new HashMap<>();
        functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return back();
            }
        });
        functionsHashMap.put("Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getName();
            }
        });
        functionsHashMap.put("Brand", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getBrand();
            }
        });
        functionsHashMap.put("Quantity", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getQuantity();
            }
        });
        functionsHashMap.put("Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getCategory();
            }
        });
        functionsHashMap.put("Description", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getDescription();
            }
        });
        functionsHashMap.put("Price", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return getPrice();
            }
        });
        functionsHashMap.put("Confirm", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return confirm();
            }
        });

    }

    public static AddProductProcessor getInstance(){
        if(addProductProcessor == null)
            addProductProcessor = new AddProductProcessor();

        return addProductProcessor;
    }

    public static AddProductProcessor getInstance(ListicMenu parentMenu){
        if(addProductProcessor == null)
            addProductProcessor = new AddProductProcessor();

        AddProductProcessor.parentMenu = parentMenu;
        AddProductProcessor.product = new Product();
        return addProductProcessor;
    }

    public static Menu getThisMenu(){
        if(thisMenu == null)
            thisMenu = Menu.makeMenu("Add Product Menu");

        return thisMenu;
    }

    public Menu back(){
        return parentMenu;
    }

    public Menu getName() {
        System.out.println("Please Enter The Name Of Your Product :");
        product.setName(scanner.nextLine().trim());
        return getThisMenu();
    }

    public Menu getBrand(){
        System.out.println("Please Enter The Name Of Your Brand :");
        product.setBrand(scanner.nextLine().trim());
        return getThisMenu();
    }

    public Menu getQuantity(){
        boolean flag = true;
        int input = 0, count;
        double amount;

        while(flag){
            try {
                System.out.println("Are Your Product Countable Or UnCountable ?" +
                        "\n1. Countable" + "\n2. UnCountable");
                input = Integer.parseInt(scanner.nextLine().trim());

                if (input > 2 || input < 0)
                    throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                else
                    flag = false;

            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (Menu.InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
        }

        if(input == 1) {
            product.setCountable(true);
            product.setCount(getCount());
        } else if(input == 2) {
            product.setCountable(false);
            product.setAmount(getAmount());
        }

        return getThisMenu();
    }

    public int getCount(){
        boolean flag = true;
        int count = 0;

        while(flag) {
            System.out.println("Please Enter Your Product's Count : ");

            try {
                count = Integer.parseInt(scanner.nextLine().trim());
                flag = false;
            } catch (Exception e) {
                System.out.println("Please Enter An Integer : ");
            }
        }

        return count;
    }

    public double getAmount(){
        boolean flag = true;
        double amount = 0;

        while(flag) {
            System.out.println("Please Enter Your Product's Amount : ");

            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                flag = false;
            } catch (Exception e) {
                System.out.println("Please Enter A Double Number : ");
            }
        }

        return amount;
    }

    public Menu getCategory(){
        //TODO Getting Category
        return null;
        return getThisMenu();
    }

    public Menu getDescription(){
        System.out.println("Please Enter The Description Of Your Product :");
        product.setDescription(scanner.nextLine().trim());
        return getThisMenu();
    }

    public Menu getPrice(){
        boolean flag = true;

        while(flag) {
            System.out.println("Please Enter Your Product's Price : ");

            try {
                product.setPrice(Double.parseDouble(scanner.nextLine().trim()));
                flag = false;
            } catch (Exception e) {
                System.out.println("Please Write A Double Number.");
            }
        }

        return getThisMenu();
    }

    public Menu confirm(){
        //TODO Seting Position for Parent Menu
        return back();
    }
}
