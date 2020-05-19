package view.process;

import controller.Control;
import controller.account.CustomerControl;
import controller.product.ProductControl;
import model.existence.Product;
import view.menu.ListicMenu;
import view.menu.Menu;

import java.util.HashMap;

public class FilterProcessor extends Processor {
    private static FilterProcessor processor = null;
    private CustomerControl customerControl = null;
    private ProductControl productControl = null;

    private FilterProcessor() {
        customerControl = CustomerControl.getController();
        productControl = ProductControl.getController();
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
        this.functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                if(productControl.isOffListic())
                    return ListicMenu.makeListicMenu("Off Products Listic Menu");
                return ListicMenu.makeListicMenu("Products Listic Menu");
            }
        });
        this.functionsHashMap.put("Filter By Price Period", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return filterByPrice();
            }
        });
    }

    private Menu filterByPrice() {
        Menu nextMenu = Menu.makeMenu("Filter Menu");
        System.out.println("-1. Back");
        System.out.println("Please Enter The Start Price: ");
        String startPrice = scanner.nextLine().trim();
        if(startPrice.equals("-1"))
            return nextMenu;
        System.out.println("Please Enter The Finish Price: ");
        String finishPrice = scanner.nextLine();
        if (finishPrice.equals("-1"))
            return nextMenu;
        try {
            System.out.println(customerControl.setPriceFilters(Double.parseDouble(startPrice), Double.parseDouble(finishPrice)).getMessage());
        } catch (NumberFormatException e) {
            System.out.println("You Should've Entered A Real Number");
        }
        return nextMenu;
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
            System.out.println("2. Current Price Filters");
            System.out.println("3. Current Name Filters");
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                switch (input)
                {
                    case 0 :
                        return Menu.makeMenu("Filter Menu");
                    case 1 :
                        return ListicMenu.makeListicMenu("Current Category Filters Listic Menu");
                    case 2 :
                        return showCurrentFilter();
                    case 3 :
                        return ListicMenu.makeListicMenu("Current Name Filters Listic Menu");
                    default :
                        System.out.println("Invalid Number!!! \nWhat are you doing, man?!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            }
        }
    }

    private Menu showCurrentFilter() {
        if(customerControl.getFinishPeriod() == Double.MAX_VALUE && customerControl.getStartPeriod() == 0)
            System.out.println("..!No Price Period Set!..");
        else
        {
            System.out.println("Start Price: " + customerControl.getStartPeriod());
            System.out.println("Finish Price: " + customerControl.getFinishPeriod());
        }
        return Menu.makeMenu("Filter Menu");
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
        return Menu.makeMenu("Sorting Menu");
    }

    public Menu showCurrentSort()
    {
        System.out.println("Current Sort: " + customerControl.getCurrentSort());
        return Menu.makeMenu("Sorting Menu");
    }

    public Menu disableCurrenSort() {
        System.out.println(customerControl.disableSore().getMessage());
        return Menu.makeMenu("Sorting Menu");
    }
}
