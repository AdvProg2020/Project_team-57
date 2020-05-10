package view.menu;

import view.process.ProductProcessor;
import view.process.UserProcessor;

import java.io.FileNotFoundException;

public class ListicOptionMenu extends Menu {
    protected Menu parentMenu;

    public static ListicOptionMenu makeMenu(String menuName, Menu parentMenu, String optionID) {
        String json = "";

        try {
            json = ProductMenu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("ProductMenu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }

        ListicOptionMenu menu = null;

        if(menuName.contains("Product")) {
            menu = ProductProcessor.setMenu(json, optionID);
        } else {
            menu = UserProcessor.setMenu(json, optionID);
        }
        //TODO(OTHERS)

        menu.setParentMenu(parentMenu);
        return menu;
    }

    public void show(){
        printOptionSpecs();

        System.out.println("0. back");

        for(int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));

    }

    protected void printOptionSpecs(){}

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }
}
