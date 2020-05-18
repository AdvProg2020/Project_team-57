package view.process;

import controller.Control;
import controller.account.CustomerControl;
import controller.account.VendorControl;
import view.menu.ListicOptionMenu;
import view.process.person.AdminProcessor;
import view.process.person.VendorProcessor;

import java.util.ArrayList;

public class ListicOptionProcessor extends Processor{
    private static ListicOptionProcessor listicOptionProcessor = null;
    private static CustomerControl customerControl = CustomerControl.getController();
    private static VendorControl vendorControl = VendorControl.getController();

    public static ListicOptionProcessor getInstance() {
        if(listicOptionProcessor == null)
            listicOptionProcessor = new ListicOptionProcessor();

        return listicOptionProcessor;
    }

    public static String setMenuName(String menuName, String optionID) {

        //TODO
        if(menuName.equals("Common Product Menu"))
            menuName = setMenuNameForCommonProductMenu();
        else if(menuName.equals("Filtering Category Menu"))
            menuName = setMenuNameForFilteringCategoryMenu(optionID);
        else if(menuName.equals("Discount User Menu"))
            menuName = setMenuNameForDiscountUserMenu(optionID);
        else if(menuName.equals("Off Product Menu"))
            menuName = setMenuNameForOffProductMenu(optionID);
        else if(menuName.equals("View Log Menu"))
            menuName = setMenuNameForLogMenu();
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

    public static String setMenuNameForDiscountUserMenu(String userName) {
        String menuName = null;

        if(AdminProcessor.isThereCustomerInDiscount(userName))
            menuName = "Discount User Menu2";
        else
            menuName = "Discount User Menu1";

        return menuName;
    }

    public static String setMenuNameForOffProductMenu(String productID) {
        String menuName = "";

        if(VendorProcessor.getOff().isThereProductInOff(productID))
            menuName = "Off Product Menu2";
        else
            menuName = "Off Product Menu1";

        return menuName;
    }

    public static String setMenuNameForLogMenu() {
        String menuName = null;

        if(Control.getType().equals("Customer"))
            menuName = "View Buying Log Menu";
        else if(Control.getType().equals("Vendor"))
            menuName = "View Selling Log Menu";

        return menuName;
    }


    public static void setMenu(String menuName, ListicOptionMenu listicOptionMenu, String optionID) {
        ArrayList<String> productMenuNames = getAllProductMenuNames();
        ArrayList<String> filteringMenuNames = getAllFilteringMenuNames();
        ArrayList<String> categoryMenuNames = getAllCategoryMenuNames();
        ArrayList<String> discountMenuNames = getAllDiscountMenuNames();
        ArrayList<String> offMenuNames = getAllOffMenuNames();
        //ArrayList<String> userMenuNames = getAllUserMenuNames();
        //ArrayList<String> productOfLogMenuNames = getAllProductOfLogMenuNames();

        /*if(menuName.contains("Product")) {
            ProductProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Filtering")) {
            FilteringProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Category")) {
            CategoryProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Discount Menu")) {
            DiscountProcessor.setMenu(listicOptionMenu, optionID);
        } else if(menuName.contains("Off Request Menu") || menuName.equals("Vendor Off Menu")) {
            OffProcessor.setMenu(listicOptionMenu, optionID);
        } else {
            UserProcessor.setMenu(listicOptionMenu, optionID);
        }*/

        if(productMenuNames.contains(menuName)) {
            ProductProcessor.setMenu(listicOptionMenu, optionID);
        } else if(filteringMenuNames.contains(menuName)) {
            FilteringProcessor.setMenu(listicOptionMenu, optionID);
        } else if(categoryMenuNames.contains(menuName)) {
            CategoryProcessor.setMenu(listicOptionMenu, optionID);
        } else if(discountMenuNames.contains(menuName)) {
            DiscountProcessor.setMenu(listicOptionMenu, optionID);
        } else if(offMenuNames.contains(menuName)) {
            OffProcessor.setMenu(listicOptionMenu, optionID);
        } else {
            UserProcessor.setMenu(listicOptionMenu, optionID);
        }

    }

    /*private static ArrayList<String> getAllProductOfLogMenuNames() {
        ArrayList<String> productOfLogMenuNames = new ArrayList<>();
        productOfLogMenuNames.add("View Log Menu");
        return productOfLogMenuNames;
    }*/

    private static ArrayList<String> getAllOffMenuNames() {
        ArrayList<String> offMenuNames = new ArrayList<>();
        offMenuNames.add("Edit Off Request Menu");
        offMenuNames.add("Add Off Request Menu");
        offMenuNames.add("Vendor Off Menu");
        return offMenuNames;
    }

    private static ArrayList<String> getAllDiscountMenuNames() {
        ArrayList<String> discountMenuNames = new ArrayList<>();
        discountMenuNames.add("Customer View Discount Menu");
        discountMenuNames.add("View Discount Menu");
        return discountMenuNames;
    }

    private static ArrayList<String> getAllCategoryMenuNames() {
        ArrayList<String> allCategoryMenuNames = new ArrayList<>();
        allCategoryMenuNames.add("Admin Category Menu");
        return allCategoryMenuNames;
    }

    private static ArrayList<String> getAllFilteringMenuNames() {
        ArrayList<String> allFilteringMenuNames = new ArrayList<>();
        allFilteringMenuNames.add("Filtering Category Menu1");
        allFilteringMenuNames.add("Filtering Category Menu1");
        allFilteringMenuNames.add("Filtering Name Menu");
        return allFilteringMenuNames;
    }

    private static ArrayList<String> getAllProductMenuNames() {
        ArrayList<String> allProductMenuNames = new ArrayList<>();
        allProductMenuNames.add("Add Product Request Menu");
        allProductMenuNames.add("Admin Product Menu");
        allProductMenuNames.add("Cart Product Menu");
        allProductMenuNames.add("Common Product Menu1");
        allProductMenuNames.add("Common Product Menu2");
        allProductMenuNames.add("Comparing Products Menu");
        allProductMenuNames.add("Customer Product Menu");
        allProductMenuNames.add("Edit Product Request Menu");
        allProductMenuNames.add("Off Product Menu1");
        allProductMenuNames.add("Off Product Menu2");
        allProductMenuNames.add("Vendor Product Menu");
        allProductMenuNames.add("Non Logged In Product Menu");
        allProductMenuNames.add("View Log Menu");
        return allProductMenuNames;
    }

}
