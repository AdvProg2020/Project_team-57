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
        this.functionsHashMap.put("Sort By View", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return sortBy("View");
            }
        });
        this.functionsHashMap.put("Sort By Time", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return sortBy("Time");
            }
        });
        this.functionsHashMap.put("Sort By Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return sortBy("Name");
            }
        });
        this.functionsHashMap.put("Sort By Score", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return sortBy("Score");
            }
        });
        this.functionsHashMap.put("Current Sort", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return showCurrentSort();
            }
        });
        this.functionsHashMap.put("Disable Current Sort", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return disableCurrenSort();
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

    public Menu sortBy(String sort) {
        int input = 0;
        while(true)
        {

            System.out.println("0. Back\n1. Ascending\n2. Descending");
            try{
                input = Integer.parseInt(scanner.nextLine().trim());
                if(input == 0)
                    return Menu.makeMenu("Sorting Menu");
                if(input == 1 || input == 2)
                    break;
                System.out.println("Invalid Number");
            }catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            }catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            }
        }
        System.out.println(customerControl.setSort(sort, input == 1).getMessage());
        return ListicMenu.makeListicMenu("Products Listic Menu");
    }

    public Menu showCurrentSort()
    {
        System.out.println("Current Sort: " + customerControl.getCurrentSort());
        return Menu.makeMenu("Sorting Menu");
    }

    public Menu disableCurrenSort() {
        System.out.println(customerControl.disableSore().getMessage());
        return ListicMenu.makeListicMenu("Products Listic Menu");
    }
}
