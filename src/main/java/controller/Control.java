package controller;

import java.util.ArrayList;

public class Control {
    private static Control control = null;
    private static boolean isLoggedIn = false;
    private static String username;
    private static String type;

    private static boolean isFiltered;
    private static Filter filter;

    //INNER CLASS
    public static class Filter{
        ArrayList<String> filterCategories;
        ArrayList<String> filterNames;

        public Filter(ArrayList<String> filterCategories, ArrayList<String> filterNames) {
            this.filterCategories = filterCategories;
            this.filterNames = filterNames;
        }

        public ArrayList<String> getFilterCategories() {
            return filterCategories;
        }

        public void setFilterCategories(ArrayList<String> filterCategories) {
            this.filterCategories = filterCategories;
        }

        public ArrayList<String> getFilterNames() {
            return filterNames;
        }

        public void setFilterNames(ArrayList<String> filterNames) {
            this.filterNames = filterNames;
        }
    }

    public static Control getController(){
        if (control == null){
            control = new Control();
        }
        return control;
    }

    public static boolean isLoggedIn() {
        return Control.isLoggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Control.isLoggedIn = loggedIn;
    }

    public static String getUsername() {
        return Control.username;
    }

    public static void setUsername(String username) {
        Control.username = username;
    }

    public static String getType() {
        return Control.type;
    }

    public static void setType(String type) {
        Control.type = type;
    }

    public static boolean isIsFiltered() {
        return isFiltered;
    }

    public static Filter getFilter() {
        return filter;
    }
}
