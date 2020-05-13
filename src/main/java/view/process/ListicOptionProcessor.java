package view.process;

import controller.Control;
import controller.account.CustomerControl;
import view.menu.ListicOptionMenu;

public class ListicOptionProcessor extends Processor{
    private static ListicOptionProcessor listicOptionProcessor = null;
    private static CustomerControl customerControl = CustomerControl.getController();

    public static ListicOptionProcessor getInstance() {
        if(listicOptionProcessor == null)
            listicOptionProcessor = new ListicOptionProcessor();

        return listicOptionProcessor;
    }

    public static String setMenuName(String menuName, String optionID) {

        //TODO
        if(menuName.equals("Common Product Menu")) {
            menuName = setMenuNameForCommonProductMenu();
        } else if(menuName.equals("Filtering Category Menu")) {
            menuName = setMenuNameForFilteringCategoryMenu(optionID);
        }

        return menuName;
    }

    public static String setMenuNameForCommonProductMenu() {
        String menuName = null;

        if(Control.isLoggedIn() && (Control.getType().equals("Vendor") || Control.getType().equals("Admin"))) {
            menuName = "Common Product Menu2";
        } else {
            menuName = "Common Product Menu1";
        }

        return menuName;
    }

    public static String setMenuNameForFilteringCategoryMenu(String optionID) {
        String menuName = null;

        if(customerControl.isThereFilteringCategoryWithName(optionID))
        {
            menuName = "Filtering Category Menu2";
        } else {
            menuName = "Filtering Category Menu1";
        }

        return menuName;
    }

    public static void setMenu(String menuName, ListicOptionMenu listicOptionMenu, String optionID) {

        if(menuName.contains("Product")) {
            ProductProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Filtering")) {
            FilteringProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Category")) {
            CategoryProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Discount")) {
            DiscountProcessor.setMenu(listicOptionMenu, optionID);
        } else {
            UserProcessor.setMenu(listicOptionMenu, optionID);
        }

    }

}
