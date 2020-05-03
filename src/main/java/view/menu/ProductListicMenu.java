package view.menu;

import com.google.gson.GsonBuilder;
import view.menu.Menu;
import view.process.Processor;
import view.process.ProductListicProcessor;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ProductListicMenu extends Menu {
    private ProductListicProcessor processor = ProductListicProcessor.getInstance();
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productIDs = new ArrayList<>();
    private int pageSize = 5;
    private int pageNumber = 0;
    private int pageLim;
    private int maxOption;
    private String input;


    public static ProductListicMenu makeProductListicMenu(String menuName)
    {
        String json = "";
        try {
            json = Menu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("Menu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }
        return new GsonBuilder().setPrettyPrinting().create().fromJson(json, ProductListicMenu.class);
    }

    @Override
    public void show() {
        pageLim = (productNames.size() / pageSize);
        if(productNames.size() % 5 != 0)
            ++pageLim;
        while (true)
        {
            setMaxOption();
            System.out.println("0. Cancel");
            System.out.println("Page " + (pageNumber + 1));
            for(int j = 0; j < maxOption; ++j)
            {
                System.out.println((j + 1) + ". " + productNames.get((pageNumber * pageLim) + j));
            }
            if(pageNumber == pageLim - 1)
                System.out.println("-: Previous Page");
            else if(pageNumber == 0)
                System.out.println("+: Next Page");
            else
                System.out.println("-: Previous Page, +: Next Page");

            if(options != null && options.size() > 0)
            {

                for(int j = maxOption; j < options.size() + (maxOption); ++j)
                {
                    System.out.println((j + 1) + ". " + options.get(j - maxOption));
                }
                maxOption += options.size();
            }
        }
    }

    @Override
    public Menu execute() {
        if(getInput())
        {
            if(input.equals("+"))
                ++pageNumber;
            else if(input.equals("-"))
                --pageNumber;
            else {
                int command = Integer.parseInt(input) - 1;
                if(command < maxOption)
                {
                    return processor.getProductMenu(processor.chooseProductMenuType(), productIDs.get(command), this);
                }
                else
                {
                    //TODO
                }
            }
        }
        return this;
    }

    private void setMaxOption() {
        if(productNames.size() - (pageNumber * pageLim) < 5 )
            maxOption = productNames.size() - (pageNumber * pageLim);
        else
            maxOption = 5;
    }


    private boolean getInput() {
        try {
            input = scanner.nextLine().trim();
            if(input.equals("+") || input.equals("-"))
            {
                return canChangePage();
            }
            else
            {
                int command = Integer.parseInt(input);
                if(command >= 0 && command <= maxOption)
                {
                    return true;
                }
                else
                    throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please Enter An Integer");
        } catch (NullPointerException e) {
            System.out.println("Please Enter An Integer");
        } catch (InputIsBiggerThanExistingNumbers inputIsBiggerThanExistingNumbers) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private boolean canChangePage() {
        if(pageNumber == 0 && input.equals("-"))
            return false;
        if(pageNumber == pageLim - 1 && input.equals("+"))
            return false;
        return true;
    }
}
