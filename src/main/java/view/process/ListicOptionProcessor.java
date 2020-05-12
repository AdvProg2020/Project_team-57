package view.process;

import controller.Control;
import view.menu.ListicOptionMenu;

public class ListicOptionProcessor extends Processor{
    private static ListicOptionProcessor listicOptionProcessor = null;

    public static ListicOptionProcessor getInstance() {
        if(listicOptionProcessor == null)
            listicOptionProcessor = new ListicOptionProcessor();

        return listicOptionProcessor;
    }

    public static void changeMenuNameForDifferentPurposes(String menuName, String optionID) {

        //TODO
        if(menuName.equals("Common Product Menu")) {
            menuName = changeMenuNameForCommonProductMenu();
        } else if(menuName.equals("Filtering Category Menu")) {
            menuName = changeMenuNameForFilteringCategoryMenu(optionID);
        } else if(menuName.equals("Filtering Name Menu")) {
            menuName = changeMenuNameForFilteringNameMenu(optionID);
        }

    }

    public static String changeMenuNameForCommonProductMenu() {
        String menuName = null;

        if(Control.isLoggedIn() && (Control.getType().equals("Vendor") || Control.getType().equals("Admin"))) {
            menuName = "Common Product Menu2";
        } else {
            menuName = "Common Product Menu1";
        }

        return menuName;
    }

    public static String changeMenuNameForFilteringCategoryMenu(String optionID) {
        String menuName = null;

        if(Control.getFilter().isThereFilteringCategoryWithName(optionID)) {
            menuName = "Filtering Category Menu2";
        } else {
            menuName = "Filtering Category Menu1";
        }

        return menuName;
    }

    public static String changeMenuNameForFilteringNameMenu(String optionID) {
        String menuName = null;

        if(Control.getFilter().isThereFilteringNameWithName(optionID)) {
            menuName = "Filtering Category Menu2";
        } else {
            menuName = "Filtering Category Menu1";
        }

        return menuName;
    }

    public static void setMenuForDifferentPurposes(String menuName, ListicOptionMenu listicOptionMenu, String optionID) {

        if(menuName.contains("Product")) {
            ProductProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Filtering")) {
            FilteringProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Category")){
            CategoryProcessor.setMenu(listicOptionMenu, optionID);
        } else {
            UserProcessor.setMenu(listicOptionMenu, optionID);
        }

    }

}
