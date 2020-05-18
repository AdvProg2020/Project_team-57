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

    private ArrayList<String> productMenuNames = getAllProductMenuNames();
    private ArrayList<String> filteringMenuNames = getAllFilteringMenuNames();
    private ArrayList<String> categoryMenuNames = getAllCategoryMenuNames();
    private ArrayList<String> discountMenuNames = getAllDiscountMenuNames();
    private ArrayList<String> offMenuNames = getAllOffMenuNames();
    //private static ArrayList<String> userMenuNames = getAllUserMenuNames();
    //private static ArrayList<String> productOfLogMenuNames = getAllProductOfLogMenuNames();
    private ArrayList<String> commentMenuNames = getAllCommentMenuNames();

    private ListicOptionProcessor() {
        productMenuNames = getAllProductMenuNames();
        filteringMenuNames = getAllFilteringMenuNames();
        categoryMenuNames = getAllCategoryMenuNames();
        discountMenuNames = getAllDiscountMenuNames();
        offMenuNames = getAllOffMenuNames();
        //userMenuNames = getAllUserMenuNames();
        //productOfLogMenuNames = getAllProductOfLogMenuNames();
        commentMenuNames = getAllCommentMenuNames();
    }

    public static ListicOptionProcessor getInstance() {
        if(listicOptionProcessor == null)
            listicOptionProcessor = new ListicOptionProcessor();

        return listicOptionProcessor;
    }

    public String setMenuName(String menuName, String optionID) {

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

    public String setMenuNameForCommonProductMenu() {
        String menuName = null;

        if(Control.isLoggedIn() && (Control.getType().equals("Vendor") || Control.getType().equals("Admin"))) {
            menuName = "Common Product Menu2";
        } else {
            menuName = "Common Product Menu1";
        }

        return menuName;
    }

    public String setMenuNameForFilteringCategoryMenu(String optionID) {
        String menuName = null;

        if(customerControl.isThereFilteringCategoryWithName(optionID))
        {
            menuName = "Filtering Category Menu2";
        } else {
            menuName = "Filtering Category Menu1";
        }

        return menuName;
    }

    public String setMenuNameForDiscountUserMenu(String userName) {
        String menuName = null;

        if(AdminProcessor.isThereCustomerInDiscount(userName))
            menuName = "Discount User Menu2";
        else
            menuName = "Discount User Menu1";

        return menuName;
    }

    public String setMenuNameForOffProductMenu(String productID) {
        String menuName = "";

        if(VendorProcessor.getOff().isThereProductInOff(productID))
            menuName = "Off Product Menu2";
        else
            menuName = "Off Product Menu1";

        return menuName;
    }

    public String setMenuNameForLogMenu() {
        String menuName = null;

        if(Control.getType().equals("Customer"))
            menuName = "View Buying Log Menu";
        else if(Control.getType().equals("Vendor"))
            menuName = "View Selling Log Menu";

        return menuName;
    }


    public void setMenu(String menuName, ListicOptionMenu listicOptionMenu, String optionID) {
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
        } else if(commentMenuNames.contains(menuName)) {
            ProductProcessor.setCommentMenu(listicOptionMenu, optionID);
        } else {
            UserProcessor.setMenu(listicOptionMenu, optionID);
        }

    }

    /*private static ArrayList<String> getAllProductOfLogMenuNames() {
        ArrayList<String> productOfLogMenuNames = new ArrayList<>();
        productOfLogMenuNames.add("View Log Menu");
        return productOfLogMenuNames;
    }*/

    private ArrayList<String> getAllOffMenuNames() {
        ArrayList<String> offMenuNames = new ArrayList<>();
        offMenuNames.add("Edit Off Request Menu");
        offMenuNames.add("Add Off Request Menu");
        offMenuNames.add("Vendor Off Menu");
        return offMenuNames;
    }

    private ArrayList<String> getAllDiscountMenuNames() {
        ArrayList<String> discountMenuNames = new ArrayList<>();
        discountMenuNames.add("Customer View Discount Menu");
        discountMenuNames.add("View Discount Menu");
        return discountMenuNames;
    }

    private ArrayList<String> getAllCategoryMenuNames() {
        ArrayList<String> allCategoryMenuNames = new ArrayList<>();
        allCategoryMenuNames.add("Admin Category Menu");
        return allCategoryMenuNames;
    }

    private ArrayList<String> getAllFilteringMenuNames() {
        ArrayList<String> allFilteringMenuNames = new ArrayList<>();
        allFilteringMenuNames.add("Filtering Category Menu1");
        allFilteringMenuNames.add("Filtering Category Menu1");
        allFilteringMenuNames.add("Filtering Name Menu");
        return allFilteringMenuNames;
    }

    private ArrayList<String> getAllProductMenuNames() {
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
        allProductMenuNames.add("View Buying Log Menu");
        allProductMenuNames.add("View Selling Log Menu");

        return allProductMenuNames;
    }

    private ArrayList<String> getAllCommentMenuNames() {
        ArrayList<String> allCommentMenuNames = new ArrayList<>();
        allCommentMenuNames.add("Comment Menu");
        allCommentMenuNames.add("Admin Adding Comment Request Menu");
        allCommentMenuNames.add("Admin Editing Comment Request Menu");
        return allCommentMenuNames;
    }
}
