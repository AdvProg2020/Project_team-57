package view.menu;

import com.google.gson.GsonBuilder;
import view.process.Processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    protected static Scanner scanner = new Scanner(System.in);
    protected String processorName;
    protected Processor processor;
    protected ArrayList<String> options;
    protected String name;
    protected String parentName;
    private boolean isThereParentMenu;

    protected Menu() {
    }

    public static Menu makeMenu(String menuName) {
        String json = "";

        try {
            json = Menu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("Menu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }

        return new GsonBuilder().setPrettyPrinting().create().fromJson(json, Menu.class);
    }

    protected static String getJsonFromDB(String menuName) throws FileNotFoundException {
        File file = new File("menujsons\\" + menuName + ".json");
        Scanner myScanner = new Scanner(file);
        StringBuilder json = new StringBuilder();

        while (myScanner.hasNextLine()){
            json.append(myScanner.nextLine());
            json.append("\n");
        }

        json = new StringBuilder(json.substring(0, json.length() - 1));
        return json.toString();
    }

    public void show() {
        System.out.println(this.name + ":");

        if (this.isThereParentMenu)
            System.out.println("0. Back");
          else
            System.out.println("0. Exit");

        for (int i = 0; i < this.options.size(); i++) {
            System.out.println("" + (i + 1) + ". " + options.get(i));
        }

    }

    public Menu execute() {
        processor = Processor.findProcessorWithName(processorName);
        Menu nextMenu = this;
        boolean flag = true;
        int input = 0;

        while(flag){
            try {
                input = Integer.parseInt(scanner.nextLine().trim());

                if (input > options.size() || input < 0)
                    throw new InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                else
                    flag = false;

            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
        }

        if (input == 0) {
            if(isThereParentMenu){
                nextMenu = Menu.makeMenu(this.parentName);
            } else {
                nextMenu = null;
            }
        } else {
            if (processor.isThereFunctionWithName(this.options.get(input - 1)))
                nextMenu = processor.executeTheFunctionWithName(this.options.get(input - 1));
            else
                nextMenu = Menu.makeMenu(this.options.get(input - 1));
        }

        return nextMenu;
    }

    public static Scanner getScanner() {
        return scanner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class InputIsBiggerThanExistingNumbers extends Exception {
        public InputIsBiggerThanExistingNumbers(String message) {
            super(message);
        }
    }

    public String getName() {
        return name;
    }
}