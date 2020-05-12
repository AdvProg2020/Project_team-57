package view.menu;

import com.google.gson.GsonBuilder;
import controller.Control;
import view.PrintOptionSpecs;
import view.process.*;

import java.io.FileNotFoundException;

public class ListicOptionMenu extends Menu implements PrintOptionSpecs {
    private static ListicOptionProcessor listicOptionProcessor = ListicOptionProcessor.getInstance();
    protected Menu parentMenu;
    protected Object option;

    public static ListicOptionMenu makeMenu(String menuName, Menu parentMenu, String optionID) {
        menuName = listicOptionProcessor.changeMenuNameForDifferentPurposes(menuName, optionID);

        String json = "";

        try {
            json = ListicOptionMenu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("ProductMenu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }

        ListicOptionMenu listicOptionMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ListicOptionMenu.class);
        System.out.println(listicOptionMenu.getName());
        listicOptionProcessor.setMenuForDifferentPurposes(menuName, listicOptionMenu, optionID);

        listicOptionMenu.setParentMenu(parentMenu);
        return listicOptionMenu;
    }

    public void show(){
        printOptionSpecs(option);

        System.out.println("0. back");

        for(int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));

    }

    public Menu execute(){
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

        if(input == 0) {
            nextMenu = parentMenu;
        } else if(processor.isThereFunctionWithName(options.get(input - 1))) {
            nextMenu = processor.executeTheFunctionWithName(options.get(input - 1), this, option);
        }

        return nextMenu;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setOption(Object option) {
        this.option = option;
    }
}
