package controller;

import notification.Notification;

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
        System.out.println("Categories: ");
        for (String category : filter.getFilterCategories()) {
            System.out.println(category);
        }
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

    //START INNER CLASS
    protected static class Filter{
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
    }
    //END INNER CLASS

    public void initSort()
    {
        sort = new Sort();
    }

    public Notification setSort(String sort) {
        if(sort.equalsIgnoreCase("View"))
            Control.sort.setSortType(Sort.SortType.VIEW);
        else if(sort.equalsIgnoreCase("Name"))
            Control.sort.setSortType(Sort.SortType.NAME);
        else if(sort.equalsIgnoreCase("Time"))
            Control.sort.setSortType(Sort.SortType.TIME);
        else if(sort.equalsIgnoreCase("Score"))
            Control.sort.setSortType(Sort.SortType.SCORE);
        return Notification.SORTED;
    }

    public String getCurrentSort() {
        return sort.getSortType().getMessage();
    }

    public Notification disableSore() {
        sort.setSortType(Sort.SortType.VIEW);
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

}
