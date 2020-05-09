package view.menu;

import com.google.gson.GsonBuilder;
import view.process.ListicProcessor;
import view.process.Processor;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ListicMenu extends Menu {
    private String listicProcessorName;
    private ArrayList<String> listicOptionNames = new ArrayList<>();
    private ArrayList<String> listicOptionPrimaryKeys = new ArrayList<>();
    private int pageSize = 5;
    private int pageNumber = 0;
    private int pageLim;
    private int maxOption;
    private String input;


    public static ListicMenu makeListicMenu(String menuName)
    {
        String json = "";
        try {
            json = Menu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("Menu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }
        ListicMenu listicMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ListicMenu.class);
        return listicMenu;
    }

    @Override
    public void show() {
        ListicProcessor.initListicMenu(this);
        pageLim = (listicOptionNames.size() / pageSize);
        if(listicOptionNames.size() % 5 != 0)
            ++pageLim;
        setMaxOption();
        System.out.println("0. Cancel");
        System.out.println("Page " + (pageNumber + 1));
        for(int j = 0; j < maxOption; ++j)
        {
            System.out.println((j + 1) + ". " + listicOptionNames.get((pageNumber * pageSize) + j));
        }

        if(pageLim != 1 && pageLim != 0)
        {
            if(pageNumber == pageLim - 1)
                System.out.println("-: Previous Page");
            else if(pageNumber == 0)
                System.out.println("+: Next Page");
            else
                System.out.println("-: Previous Page, +: Next Page");
        }

        if(options != null && options.size() > 0)
        {

            for(int j = maxOption; j < options.size() + (maxOption); ++j)
            {
                System.out.println((j + 1) + ". " + options.get(j - maxOption));
            }
            maxOption += options.size();
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
                setMaxOption();
                int command = Integer.parseInt(input) - 1;
                if(command < maxOption)
                {
                    //System.out.println("MaxOption = " + maxOption + " Command = " + command);
                    if (command == -1)
                    {
                        return Menu.makeMenu(parentName);
                    }
                    //return listicProcessor.getProductMenu(listicProcessor.chooseProductMenuType(), listicOptionPrimaryKeys.get(command + (pageNumber * pageSize)), this);
                    return Processor.findProcessorWithName(this.processorName).
                            executeTheFunctionWithName("Open Functioning Option",
                            listicOptionPrimaryKeys.get(command + (pageNumber * pageSize)), this);
                }
                else
                {
                    command -= maxOption;
                    return Processor.findProcessorWithName(this.processorName).
                            executeTheFunctionWithName(options.get(command), this);
                }
            }
        }
        return this;
    }

    private void setMaxOption() {
        if(listicOptionNames.size() - (pageNumber * pageSize) < 5 ) {
            //System.out.println("ListicOptinNames.size() = " + listicOptionNames.size());
            maxOption = listicOptionNames.size() - (pageNumber * pageSize);
        }
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
        } catch (Menu.InputIsBiggerThanExistingNumbers e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private boolean canChangePage() {
        if(pageNumber == 0 && input.equals("-"))
        {
            System.out.println("It's Only The beginning, BRO!!!");
            return false;
        }
        if(pageNumber == pageLim - 1 && input.equals("+"))
        {
            System.out.println("It's The End Of The Line, DUDE! You Can't Go Any Further :/");
            return false;
        }
        return true;
    }

    public ArrayList<String> getListicOptionNames() {
        return listicOptionNames;
    }

    public ArrayList<String> getListicOptionPrimaryKeys() {
        return listicOptionPrimaryKeys;
    }

    public void setListicOptionNames(ArrayList<String> listicOptionNames) {
        this.listicOptionNames = listicOptionNames;
    }

    public void setListicOptionPrimaryKeys(ArrayList<String> listicOptionPrimaryKeys) {
        this.listicOptionPrimaryKeys = listicOptionPrimaryKeys;
    }

    public void deleteProductFromListWithId(String productID)
    {
        int index = listicOptionPrimaryKeys.indexOf(productID);
        listicOptionPrimaryKeys.remove(index); listicOptionNames.remove(index);
    }
}
