package view.menu;

import controller.Control;
import view.PrintOptionSpecs;
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

    protected void printOptionSpecs(){}

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
