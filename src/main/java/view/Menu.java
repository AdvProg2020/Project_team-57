package view;

import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    protected static Scanner scanner;
    private ArrayList<String> options;
    private String name;
    private String parentName;
    private int input;
    private boolean isThereParentMenu;

    private Menu() {
    }

    public static Menu makeMenu(String menuName) {
        String json;
        try {
            json = Menu.getJsonFromDB(String menuName);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }
        return new GsonBuilder().setPrettyPrinting().create().fromJson(json, Menu.class);
    }

    public void show() {
        System.out.println(this.name + ":");
        if (this.isThereParentMenu) {
            System.out.println("0. Back");
            this.isThereParentMenu = true;
        }
        for (int i = 0; i < this.options.size(); i++) {
            System.out.println("" + (i + 1) + ". " + options.get(i));
        }
        this.execute();
    }

    public Menu execute() {
        Menu nextMenu = this;
        try {
            input = Integer.parseInt(scanner.nextLine().trim());
            if (input > options.size() || input < 0 || (!isThereParentMenu && input == 0))
                throw new InputIsBiggerThanExistingNumbers("input integer is invalid");
        } catch (NumberFormatException e) {
            System.out.println("please enter an integer");
        } catch (NullPointerException e) {
            System.out.println("please enter an integer");
        } catch (InputIsBiggerThanExistingNumbers e) {
            e.printStackTrace();
        }
        if (input == 0)
            nextMenu = Menu.makeMenu(this.parentName);
        else {
            nextMenu = Menu.makeMenu(this.options.get(input));
        }
        return nextMenu;
    }

    private class InputIsBiggerThanExistingNumbers extends Exception {
        public InputIsBiggerThanExistingNumbers(String message) {
            super(message);
        }
    }
}