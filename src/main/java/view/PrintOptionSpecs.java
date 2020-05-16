package view;

import controller.Control;
import controller.product.ProductControl;
import model.existence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public interface PrintOptionSpecs {
    default void printOptionSpecs(Object option) {
        if(option instanceof Account) {
            printAccountSpecs((Account) option);
        } else if(option instanceof Product) {
            printProductSpecs((Product) option);
        } else if(option instanceof Category) {
            printCategorySpecs((Category) option);
        } else if(option instanceof String) {
            printFilteringOptionSpecs((String) option);
        } else if(option instanceof Discount) {
            printDiscountSpecs((Discount) option);
        } else if(option instanceof Off) {
            printOffSpecs((Off) option);
        }
    }

    default void printAccountSpecs(Account account){
        this.printCustomLineForAccount();

        printWithNullCheckingForAccount("UserName", account.getUsername());
        this.printCustomLineForAccount();

        printWithNullCheckingForAccount("PassWord", "************");
        this.printCustomLineForAccount();

        printWithNullCheckingForAccount("Account Type", account.getType());
        this.printCustomLineForAccount();

        printWithNullCheckingForAccount("FirstName", account.getFirstName());
        this.printCustomLineForAccount();

        printWithNullCheckingForAccount("LastName", account.getLastName());
        this.printCustomLineForAccount();

        printWithNullCheckingForAccount("Email", account.getEmail());
        this.printCustomLineForAccount();

        if(account.getType().equals("Vendor")){
            printWithNullCheckingForAccount("Brand", account.getBrand());
            this.printCustomLineForAccount();
        }
        if(!account.getType().equals("Admin"))
        {
            System.out.format("| %-15s | %-35f | %n", "Credit", account.getCredit());
            this.printCustomLineForAccount();
        }
    }

    default void printWithNullCheckingForAccount(String fieldName, String fieldValue){
        if(fieldValue == null)
            System.out.format("| %-15s | %-35s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-15s | %-35s | %n", fieldName, fieldValue);
    }

    default void printCustomLineForAccount(){
        System.out.println("+-----------------+-------------------------------------+");
    }

    default void printProductSpecs(Product product){
        this.printCustomLineForProduct();

        printCustomStatusForProduct(product.getStatus());
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Name", product.getName());
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Brand Name", product.getBrand());
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Seller Name", product.getSellerUserName());
        this.printCustomLineForProduct();

        printCustomCountForProduct(product);
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Category", product.getCategory());
        this.printCustomLineForProduct();

        printCustomDescriptionForProduct(product.getDescription());
        this.printCustomLineForProduct();

        printCustomPriceForProduct(product);
        this.printCustomLineForProduct();

        System.out.format("| %-20s | %-35d | %n", "View", product.getSeen());
        this.printCustomLineForProduct();

        System.out.format("| %-20s | %-35f | %n", "Average Score", product.getAverageScore());
        this.printCustomLineForProduct();
    }

    default void printCustomStatusForProduct(int status){
        String state = null;

        if(status == 1){
            state = "Approved";
        } else if(status == 2){
            state = "Waiting For Creating Approval";
        } else if(status == 3){
            state = "Waiting For Editing Approval";
        }

        System.out.format("| %-20s | %-35s | %n", "Status", state);
    }

    default void printCustomLineForProduct(){
        System.out.println("+----------------------+-------------------------------------+");
    }

    default void printWithNullCheckingForProduct(String fieldName, String fieldValue){
        if(fieldValue == null)
            System.out.format("| %-20s | %-35s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-20s | %-35s | %n", fieldName, fieldValue);
    }

    default void printCustomCountForProduct(Product product){
        if(product.isCountable()){
            System.out.format("| %-20s | %-35d | %n", "Count", product.getCount());
        } else {
            System.out.format("| %-20s | %-35f | %n", "Amount", product.getAmount());
        }
    }

    default void printCustomPriceForProduct(Product product) {
        ProductControl productControl = ProductControl.getController();

        if(productControl.isThereProductInOff(product.getID())) {
            Off productOff = productControl.getOffByProductID(product.getID());
            double oldPrice = product.getPrice();
            double newPrice = oldPrice - (oldPrice * productOff.getOffPercent() / 100);
            System.out.format("| %-20s | %-16f => %-15f | %n", "Price", oldPrice, newPrice);
            System.out.format("| %-20s | %%%-34f | %n", "Off Percent", productOff.getOffPercent());
        } else {
            System.out.format("| %-20s | %-35f | %n", "Price", product.getPrice());
        }

    }

    default void printCustomDescriptionForProduct(String description){
        if(description != null) {
            ArrayList<String> splitDescription = splitDescriptionForProduct(description);

            for(int i = 0; i < splitDescription.size(); i++){
                if(i == 0)
                    System.out.format("| %-20s | %-35s | %n", "Description", splitDescription.get(i));
                else
                    System.out.format("| %-20s | %-35s | %n", "", splitDescription.get(i));
            }
        } else {
            System.out.format("| %-20s | %-35s | %n", "Description", "Not Assigned");
        }
    }

    default ArrayList<String> splitDescriptionForProduct(String description) {
        ArrayList<String> splitDescription = new ArrayList<>();

        if(description.length() > 35) {
            Character[] splitCharacters = new Character[] {' ', '\t', '.', ',', '!', '\n'};
            ArrayList<Character> characters = new ArrayList<>(Arrays.asList(splitCharacters));

            int splitIndex = 0;

            for(int i = 34; i > 0; i--) {
                if(characters.contains(description.charAt(i))) {
                    splitIndex = i;
                    break;
                }
            }

            if(splitIndex == 0)
                splitIndex = 34;

            splitDescription.add(description.substring(0, splitIndex + 1));
            splitDescription.addAll(splitDescriptionForProduct(description.substring(splitIndex + 1)));

        } else {
            splitDescription.add(description);
        }

        return splitDescription;
    }

    default void printCategorySpecs(Category category) {
        printCustomLineForCategory();

        printWithNullCheckingForCategory("Name", category.getName());
        printCustomLineForCategory();

        printWithNullCheckingForCategory("ParentName", category.getParentCategory());
        printCustomLineForCategory();

        printCustomFeaturesForCategory(category.getFeatures());
        printCustomLineForCategory();
    }

    default void printCustomLineForCategory() {
        System.out.println("+----------------------+-------------------------------------+");
    }

    default void printWithNullCheckingForCategory(String fieldName, String fieldValue) {
        if(fieldValue == null)
            System.out.format("| %-20s | %-35s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-20s | %-35s | %n", fieldName, fieldValue);
    }

    default void printCustomFeaturesForCategory(String features) {
        if(features != null) {
            ArrayList<String> splitFeatures = splitFeaturesForCategory(features);

            for(int i = 0; i < splitFeatures.size(); i++){
                if(i == 0)
                    System.out.format("| %-20s | %-35s | %n", "Features", splitFeatures.get(i));
                else
                    System.out.format("| %-20s | %-35s | %n", "", splitFeatures.get(i));
            }
        } else {
            System.out.format("| %-20s | %-35s | %n", "Features", "Not Assigned");
        }
    }

    default ArrayList<String> splitFeaturesForCategory(String features) {
        ArrayList<String> splitFeatures = new ArrayList<>();

        if(features.length() > 35) {
            Character[] splitCharacters = new Character[] {' ', '\t', '.', ',', '!', '\n'};
            ArrayList<Character> characters = new ArrayList<>(Arrays.asList(splitCharacters));

            int splitIndex = 0;

            for(int i = 34; i > 0; i--) {
                if(characters.contains(features.charAt(i))) {
                    splitIndex = i;
                    break;
                }
            }

            if(splitIndex == 0)
                splitIndex = 34;

            splitFeatures.add(features.substring(0, splitIndex + 1));
            splitFeatures.addAll(splitFeaturesForCategory(features.substring(splitIndex + 1)));
        } else {
            splitFeatures.add(features);
        }

        return splitFeatures;
    }

    default void printFilteringOptionSpecs(String filteringOption) {
        //TODO
        System.out.println("********    " + filteringOption + "    ********");
    }

    default void printDiscountSpecs(Discount discount) {
        printCustomLineForDiscount();

        printWithNullCheckingForDiscount("Discount Code", discount.getCode());
        printCustomLineForDiscount();

        printCustomDateForDiscount("Start Date", discount.getStartDate());
        printCustomLineForDiscount();

        printCustomDateForDiscount("Finish Date", discount.getFinishDate());
        printCustomLineForDiscount();

        System.out.format("| %-22s | %-35f |%n", "Discount Percent", discount.getDiscountPercent());
        printCustomLineForDiscount();

        System.out.format("| %-22s | %-35f |%n", "Maximum Discount", discount.getMaxDiscount());
        printCustomLineForDiscount();

        System.out.format("| %-22s | %-35d |%n", "Maximum Repetition", discount.getMaxRepetition());
        printCustomLineForDiscount();

        if(Control.getType().equals("Admin"))
            printCustomersListForDiscount(discount.getCustomersWithRepetition());
        else
            printCustomersRepetitionForDiscount(discount);

        printCustomLineForDiscount();

    }

    default void printCustomLineForDiscount() {
        System.out.println("+------------------------+-------------------------------------+");
    }

    default void printWithNullCheckingForDiscount(String fieldName, String fieldValue) {
        if(fieldValue == null)
            System.out.format("| %-22s | %-35s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-22s | %-35s | %n", fieldName, fieldValue);
    }

    default void printCustomDateForDiscount(String fieldName, Date date) {
        if(date == null) {
            System.out.format("| %-22s | %-35s | %n", fieldName, "Not Assigned");
        } else {
            java.util.Date date1 = new java.util.Date(date.getTime());
            System.out.format("| %-22s | %-35s | %n", fieldName, date1.toString());
        }
    }

    default void printCustomersListForDiscount(HashMap<String, Integer> customersHashMap) {
        if(customersHashMap == null || customersHashMap.size() == 0) {
            System.out.format("| %-22s | %-35s |%n", "Customers", "Not Assigned");
        } else {
            HashMap<String, Integer> clonedCustomersHashMap = (HashMap<String, Integer>) customersHashMap.clone();
            ArrayList<String> splitCustomers = splitCustomers(clonedCustomersHashMap);

            for (String customerLine : splitCustomers) {
                if (customerLine.equals(splitCustomers.get(0))) {
                    System.out.format("| %-22s | %-35s |%n", "Customers", customerLine);
                } else
                    System.out.format("| %-22s | %-35s |%n", "", customerLine);
            }
        }
    }

    default ArrayList<String> splitCustomers(HashMap<String, Integer> customersHashMap) {

        if(customersHashMap.keySet().size() == 0)
            return new ArrayList<>();

        ArrayList<String> splitCustomers = new ArrayList<>();
        StringBuilder customersLine = new StringBuilder("");
        boolean flag = true;
        ArrayList<String> printedCustomers = new ArrayList<>();

        for(String customer : customersHashMap.keySet()) {
            if(customersLine.length() + customer.length() > 33) {
                break;
            }
            customersLine.insert(customersLine.length(), customer + ", ");
            printedCustomers.add(customer);
        }

        for (String printedCustomer : printedCustomers) {
            customersHashMap.remove(printedCustomer);
        }
        if(customersHashMap.keySet().size() == 0)
            customersLine = new StringBuilder (customersLine.substring(0, customersLine.length() - 2));
        splitCustomers.add(customersLine.toString());
        splitCustomers.addAll(splitCustomers(customersHashMap));
        return splitCustomers;
    }

    default void printCustomersRepetitionForDiscount(Discount discount) {
        int repetition = discount.getCustomersWithRepetition().get(Control.getUsername());
        System.out.format("| %-22s | %-35d | %n", "Repetition", repetition);
    }

    default void printOffSpecs(Off off) {
        printCustomLineForOff();

        printWithNullCheckingForOff("Off ID", off.getOffID());
        printCustomLineForOff();

        printWithNullCheckingForOff("Off Name", off.getOffName());
        printCustomLineForOff();

        printCustomStatusForOff(off.getStatus());
        printCustomLineForOff();

        printWithNullCheckingForOff("Vendor UserName", off.getVendorUsername());
        printCustomLineForOff();

        printCustomDateForOff("Start Date", off.getStartDate());
        printCustomLineForOff();

        printCustomDateForOff("Finish Date", off.getFinishDate());
        printCustomLineForOff();

        printCustomDoubleForOff("Off Percentage", off.getOffPercent());
        printCustomLineForOff();

        printCustomProductsForOff(off.getProductIDs());
        printCustomLineForOff();
    }

    default void printCustomLineForOff() {
        System.out.println("+----------------------+-------------------------------+");
    }

    default void printWithNullCheckingForOff(String fieldName, String fieldValue) {
        if(fieldValue == null)
            System.out.format("| %-20s | %-29s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-20s | %-29s | %n", fieldName, fieldValue);
    }

    default void printCustomStatusForOff(int status) {
        switch (status) {
            case 0:
                System.out.format("| %-20s | %-29s | %n", "Status", "Not Assigned");
                break;
            case 1:
                System.out.format("| %-20s | %-29s | %n", "Status", "Approved");
                break;
            case 2:
                System.out.format("| %-20s | %-29s | %n", "Status", "Waiting For Adding Approval");
                break;
            case 3:
                System.out.format("| %-20s | %-29s | %n", "Status", "Waiting For Editing Approval");
                break;
            default:
                System.out.println("What The Fuck ? Error In Off Printing");
        }
    }

    default void printCustomDateForOff(String fieldName, Date date) {
        if(date == null)
            System.out.format("| %-20s | %-29s | %n", fieldName, "Not Assigned");
        else {
            java.util.Date printingDate = new java.util.Date(date.getTime());
            System.out.format("| %-20s | %-29s | %n", fieldName, printingDate);
        }
    }

    default void printCustomDoubleForOff(String fieldName, double doubleValue) {
        if(doubleValue == 0)
            System.out.format("| %-20s | %-29s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-20s | %-29f | %n", fieldName, doubleValue);
    }

    default void printCustomProductsForOff(ArrayList<String> productIDs) {
        if(productIDs == null)
            System.out.format("| %-20s | %-29s | %n", "Product Names", "Not Assigned");
        else {
            ArrayList<String> splitProducts = splitProductsForOff(getProductNamesForOff(productIDs), 0);

            for (String splitProduct : splitProducts) {
                if(splitProducts.indexOf(splitProduct) == 0)
                    System.out.format("| %-20s | %-29s | %n", "Products", splitProduct);
                else
                    System.out.format("| %-20s | %-29s | %n", "", splitProduct);
            }
        }
    }

    default ArrayList<String> getProductNamesForOff(ArrayList<String> productIDs) {
        ProductControl productControl = ProductControl.getController();
        ArrayList<String> productNames = new ArrayList<>();

        for (String productID : productIDs) {
            //System.out.println(productID);
            productNames.add(productControl.getProductById(productID).getName());
        }
        return productNames;
    }

    default ArrayList<String> splitProductsForOff(ArrayList<String> productNames, int index) {
        ArrayList<String> splitProducts = new ArrayList<>();
        StringBuilder nextLine = new StringBuilder("");

        //System.out.println(productNames);
        while(index != productNames.size() && nextLine.length() + productNames.get(index).length() < 28) {
            nextLine.insert(nextLine.length(), productNames.get(index) + ", ");
            //System.out.println(nextLine);
            index++;
        }
        //System.out.println(productNames + " Size :" + productNames.size());

        if(index == productNames.size())
            splitProducts.add(nextLine.substring(0, nextLine.length() - 2));
        else {
            splitProducts.add(nextLine.toString());
            splitProducts.addAll(splitProductsForOff(productNames, index));
        }

        return splitProducts;
    }

}
