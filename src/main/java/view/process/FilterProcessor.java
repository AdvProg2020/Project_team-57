package view.process;

import controller.account.CustomerControl;
import view.menu.ListicMenu;
import view.menu.Menu;

import java.util.HashMap;

public class FilterProcessor extends Processor {
    private static FilterProcessor processor = null;
    private CustomerControl customerControl = null;

    private FilterProcessor() {
        customerControl = CustomerControl.getController();
        this.functionsHashMap = new HashMap<>();
        this.functionsHashMap.put("Filter By Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return filterByCategory();
            }
        });
        this.functionsHashMap.put("Current Filters", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return currentFilters();
            }
        });
        this.functionsHashMap.put("Filter By Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return filterByName();
            }
        });
    }

    public static FilterProcessor getInstance() {
        if(processor == null)
            processor = new FilterProcessor();

        return processor;
    }

    public Menu filterByCategory() {
        return ListicMenu.makeListicMenu("Categories Listic Menu");
    }

    public Menu filterByName()
    {
        System.out.println("0. Back");
        System.out.println("Enter The Name You Want To Filter The Products By: ");
        System.out.println(customerControl.addToFilterNameList(scanner.nextLine()).getMessage());
        return Menu.makeMenu("Filter Menu");
    }

    public Menu currentFilters() {
        while (true)
        {
            System.out.println("0. Back");
            System.out.println("1. Current Category Filters");
            System.out.println("2. Current Name Filters");
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if(input == 0)
                    return Menu.makeMenu("Filter Menu");
                else if(input == 1)
                    return ListicMenu.makeListicMenu("Current Category Filters Listic Menu");
                else if(input == 2) {
                    return ListicMenu.makeListicMenu("Current Name Filters Listic Menu");
                }
                System.out.println("Invalid Number!!! \nWhat are you doing, man?!");
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            }
        }
    }
}
