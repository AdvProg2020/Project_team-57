package server.server;

import notification.Notification;
import server.model.existence.Discount;

import java.util.ArrayList;
import java.util.HashMap;

public class Property {

    //admin add discount
    private HashMap<Discount, ArrayList<String>> discountsAddedUsers;

    public void createDiscountAddedUsers() {
        this.discountsAddedUsers = new HashMap<>();
    }

    public void addDiscountToHashMap(Discount discount) {
        if(discount.getCustomersWithRepetition().isEmpty())
            discountsAddedUsers.put(discount, new ArrayList<>());
        else {
            ArrayList<String> users = new ArrayList<>();
            users.addAll(discount.getCustomersWithRepetition().keySet());
            discountsAddedUsers.put(discount, users);
        }
    }

    public void removeDiscountFromHashMap(Discount discount) {
        if(discount != null && discountsAddedUsers != null)
            discountsAddedUsers.remove(discount);
    }

    public HashMap<Discount, ArrayList<String>> getDiscountsAddedUsers() {
        return discountsAddedUsers;
    }

    public void addUserToDiscountAddedUsers(Discount discount, String userName) {
        if(discountsAddedUsers.containsKey(discount) && !discountsAddedUsers.get(discount).contains(userName)) {
            discountsAddedUsers.get(discount).add(userName);
        }
    }

    public void removeUserFromDiscountAddedUsers(Discount discount, String userName) {
        if(discountsAddedUsers.containsKey(discount) && discountsAddedUsers.get(discount).contains(userName)) {
            discountsAddedUsers.get(discount).remove(userName);
        }
    }

    public boolean isUserAddedInDiscount(Discount discount, String userName) {
        if(discountsAddedUsers.containsKey(discount))
            return discountsAddedUsers.get(discount).contains(userName);
        return false;
    }

    //filter and sorting
    private Filter filter;
    private Sort sort;

    public Filter getFilter() {
        return filter;
    }

    public Sort getSort() {
        return sort;
    }

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
        filter = new Filter(new ArrayList<>(), new ArrayList<>());
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

    //START INNER CLASS
    public static class Filter{
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
        this.sort = new Sort();
    }

    public Notification setSort(String sort, boolean isAscending) {
        if(sort.equalsIgnoreCase("View")) {
            this.sort.setAscending(isAscending);
            this.sort.setSortType(Sort.SortType.VIEW);
        }
        else if(sort.equalsIgnoreCase("Name")){
            this.sort.setAscending(isAscending);
            this.sort.setSortType(Sort.SortType.NAME);
        }
        else if(sort.equalsIgnoreCase("Time")) {
            this.sort.setAscending(isAscending);
            this.sort.setSortType(Sort.SortType.TIME);
        }
        else if(sort.equalsIgnoreCase("Score")) {
            this.sort.setAscending(isAscending);
            this.sort.setSortType(Sort.SortType.SCORE);
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
    public static class Sort
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
        private Sort.SortType sortType;
        private boolean isAscending;

        public Sort() {
            this.sortType = Sort.SortType.VIEW;
            this.isAscending = false;
        }

        public boolean isAscending() {
            return isAscending;
        }

        public void setAscending(boolean ascending) {
            isAscending = ascending;
        }

        public Sort.SortType getSortType() {
            return sortType;
        }

        public void setSortType(Sort.SortType sortType) {
            this.sortType = sortType;
        }
    }
    //END INNER CLASS
}
