package view.process;

import com.google.gson.GsonBuilder;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.menu.ProductMenu;
import view.menu.RegisterMenu;

public class ListicOptionProcessor extends Processor{
    public void setMenu(Menu menu, String menuName, String ID) {
        if(menuName.contains("Product")) {
            menu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ProductMenu.class);
            menu.listicOptionProcessor = ProductProcessor.getInstance();
        } else if(menuName.contains("Register")) {
            menu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, RegisterMenu.class);
            menu.listicOptionProcessor = RegisterProcessor.getInstance();
        }
    }

    public void setTheOption(ListicOptionMenu listicOptionMenu, String ID){ }
}
