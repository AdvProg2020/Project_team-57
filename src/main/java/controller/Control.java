package controller;

import model.db.OffTable;
import notification.Notification;

import java.sql.SQLException;
import java.util.ArrayList;

public class Control {
    private static Control control = null;
    private static boolean isLoggedIn = false;
    private static String username;
    private static String type;

    private static Filter filter;
    private static Sort sort;

    public ArrayList<String> getCurrentCategories() {
        return filter.getFilterCategories();
    }

    public boolean isThereFilteringCategoryWithName(String optionID) {
        return filter.getFilterCategories().contains(optionID);
    }

    public boolean isThereFilteringNameWithName(String optionID) {
        return filter.getFilterNames().contains(optionID);
    }

    public void initFilter() {
        filter = new Control.Filter(new ArrayList<>(), new ArrayList<>());
    }

    public Notification addToFilterCategoryList(String categoryName) {
        filter.addToCategories(categoryName);
        return Notification.CATEGORY_FILTERED;
    }

    public Notification addToFilterNameList(String name) {
        if(!isThereFilteringNameWithName(name)) {
        filter.addToNames(name);
        return Notification.NAME_FILTERED;
        }
        return Notification.NAME_FILTERED_DUPLICATE;
    }

    public Notification removeFromFilterCategoryList(String categoryName) {
        filter.removeFromCategories(categoryName);
        return Notification.CATEGORY_FILTER_DELETED;
    }

    public Notification removeFromFilterNameList(String name) {
        filter.removeFromNames(name);
        return Notification.NAME_FILTER_DELETED;
    }

    public ArrayList<String> getCurrentNameFilters() {
        return filter.getFilterNames();
    }

    public double getStartPeriod() {
        return filter.getMinPrice();
    }

    public double getFinishPeriod() {
        return filter.getMaxPrice();
    }

    //START INNER CLASS
    protected static class Filter{
        ArrayList<String> filterCategories;
        ArrayList<String> filterNames;
        double minPrice, maxPrice;

        public Filter(ArrayList<String> filterCategories, ArrayList<String> filterNames) {
            this.filterCategories = filterCategories;
            this.filterNames = filterNames;
            this.minPrice = 0;
            this.maxPrice = Double.MAX_VALUE;
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

        public void addToCategories(String categoryName)
        {
            filterCategories.add(categoryName);
        }

        public void addToNames(String name)
        {
            filterNames.add(name);
        }

        public void removeFromCategories(String categoryName)
        {
            filterCategories.remove(categoryName);
        }

        public void removeFromNames(String name)
        {
            filterNames.remove(name);
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            this.minPrice = minPrice;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }
    }
    //END INNER CLASS

    public void initSort()
    {
        sort = new Sort();
    }

    public Notification setSort(String sort, boolean isAscending) {
        if(sort.equalsIgnoreCase("View")) {
            Control.sort.setAscending(isAscending);
            Control.sort.setSortType(Sort.SortType.VIEW);
        }
        else if(sort.equalsIgnoreCase("Name")){
            Control.sort.setAscending(isAscending);
            Control.sort.setSortType(Sort.SortType.NAME);
        }
        else if(sort.equalsIgnoreCase("Time")) {
            Control.sort.setAscending(isAscending);
            Control.sort.setSortType(Sort.SortType.TIME);
        }
        else if(sort.equalsIgnoreCase("Score")) {
            Control.sort.setAscending(isAscending);
            Control.sort.setSortType(Sort.SortType.SCORE);
        }
        return Notification.SORTED;
    }

    public String getCurrentSort() {
        String currentSort = sort.getSortType().getMessage();
        if(sort.isAscending())
            currentSort += ", Ascending";
        else
            currentSort += ", Descending";
        return currentSort;
    }

    public Notification setPriceFilters(double minPrice, double maxPrice) {
        if(minPrice < 0)
            return Notification.INVALID_MIN_PRICE;
        else if(minPrice > maxPrice)
            return Notification.MIN_PRICE_BIGGER_THAN_MAX_PRICE;
        else {
            filter.minPrice = minPrice;
            filter.maxPrice = maxPrice;
            return Notification.SET_PRICE_FILTERS;
        }
    }

    public Notification disableSort() {
        sort.setSortType(Sort.SortType.VIEW);
        sort.setAscending(false);
        return Notification.SORT_DISABLED;
    }

    //START INNER CLASS
    protected static class Sort
    {
        public enum SortType {
            VIEW("View"), NAME("Name"), TIME("Time"), SCORE("Score");
            private String message;
            SortType(String message) {
                this.message = message;
            }
            public String getMessage() {
                return message;
            }
        }
        private SortType sortType;
        private boolean isAscending;

        public Sort() {
            this.sortType = SortType.VIEW;
            this.isAscending = false;
        }

        public boolean isAscending() {
            return isAscending;
        }

        public void setAscending(boolean ascending) {
            isAscending = ascending;
        }

        public SortType getSortType() {
            return sortType;
        }

        public void setSortType(SortType sortType) {
            this.sortType = sortType;
        }
    }
    //END INNER CLASS

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

    public static Filter getFilter() {
        return filter;
    }

    public static Sort getSort() {
        return sort;
    }

    public boolean isThereProductInOff(String productID) {
        try {
            return OffTable.isThereProductInOff(productID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logOut() {
        isLoggedIn = false;
        username = null;
        type = null;
    }
}
