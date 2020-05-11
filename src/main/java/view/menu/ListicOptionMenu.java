package view.menu;

import controller.Control;
import view.PrintOptionSpecs;
import view.process.Processor;
import view.process.ProductProcessor;
import view.process.UserProcessor;

import java.io.FileNotFoundException;

public class ListicOptionMenu extends Menu implements PrintOptionSpecs {
    protected Menu parentMenu;
    protected Object option;

    public static ListicOptionMenu makeMenu(String menuName, Menu parentMenu, String optionID) {
        String json = "";

        try {
            if(menuName.equals("Common Product Menu")) {
                if(Control.isLoggedIn() && (Control.getType().equals("Vendor") || Control.getType().equals("Admin"))) {
                    menuName = "Common Product Menu2";
                } else {
                    menuName = "Common Product Menu1";
                }
            }

            json = ProductMenu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("ProductMenu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }

        ListicOptionMenu menu = null;

        if(menuName.contains("Product")) {
            menu = ProductProcessor.setMenu(json, optionID);
        } else if(menuName.contains("Category")){

        } else {
            menu = UserProcessor.setMenu(json, optionID);
        }
        //TODO(OTHERS)

        menu.setParentMenu(parentMenu);
        return menu;
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
