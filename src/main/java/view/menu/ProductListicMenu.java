package view.menu;

import com.google.gson.GsonBuilder;
import view.menu.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProductListicMenu extends Menu {
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<String> productIDs = new ArrayList<>();
    private int pageSize = 5;
    private int pageLim;
    private int input;

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
        pageLim = (productNames.size() / pageSize) + 1;
        for(int i = 0; i < pageLim; ++i)
        {
            System.out.println("0. Cancel");
            System.out.println("Page " + (i + 1));
            for(int j = 0; j < productNames.size() - (i * pageLim) && j < 5; ++j)
            {
                System.out.println((j + 1) + ". " + productNames.get((i * pageLim) + j));
            }
            if(i == pageLim - 1)
                System.out.println("-: Previous Page");
            else if(i == 0)
                System.out.println("+: Next Page");
            else
                System.out.println("-: Previous Page, +: Next Page");
            if(options != null && options.size() > 0)
            {
                for(int j = nextPageNumber; j < options.size() + (nextPageNumber); ++j)
                {
                    System.out.println((j + 1) + ". " + options.get(j - nextPageNumber));
                }
            }
            if(getInput(i))
            {

            }
        }
    }


    private boolean getInput(int pageNumber) {
        try {
            input = Integer.parseInt(scanner.nextLine().trim());

        } catch (NumberFormatException e) {
            System.out.println("Please Enter An Integer");
        } catch (NullPointerException e) {
            System.out.println("Please Enter An Integer");
        }
        return false;
    }
}
