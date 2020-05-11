package view;

import model.existence.Account;
import model.existence.Category;
import model.existence.Product;

public interface PrintOptionSpecs {
    default void printOptionSpecs(Object option) {
        if(option instanceof Account) {
            printAccountSpecs((Account) option);
        } else if(option instanceof Product) {
            printProductSpecs((Product) option);
        } else if(option instanceof Category) {
            printCategorySpecs((Category) option);
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

        printCustomStatus(product.getStatus());
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Name", product.getName());
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Brand Name", product.getBrand());
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Seller Name", product.getSellerUserName());
        this.printCustomLineForProduct();

        printCustomCount(product);
        this.printCustomLineForProduct();

        printWithNullCheckingForProduct("Category", product.getCategory());
        this.printCustomLineForProduct();

        printCustomDescriptionForProduct(product.getDescription());
        this.printCustomLineForProduct();

        System.out.format("| %-20s | %-35f | %n", "Price", product.getPrice());
        this.printCustomLineForProduct();

        System.out.format("| %-20s | %-35f | %n", "Average Score", product.getAverageScore());
        this.printCustomLineForProduct();
    }

    default void printCustomStatus(int status){
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

    default void printCustomCount(Product product){
        if(product.isCountable()){
            System.out.format("| %-20s | %-35d | %n", "Count", product.getCount());
        } else {
            System.out.format("| %-20s | %-35f | %n", "Amount", product.getAmount());
        }
    }

    default void printCustomDescriptionForProduct(String description){
        if(description != null) {
            String[] splitDescription = splitDescriptionForProduct(description);

            for(int i = 0; i < splitDescription.length; i++){
                if(i == 0)
                    System.out.format("| %-20s | %-35s | %n", "Description", splitDescription[i]);
                else
                    System.out.format("| %-20s | %-35s | %n", "", splitDescription[i]);
            }
        } else {
            System.out.format("| %-20s | %-35s | %n", "Description", "Not Assigned");
        }
    }

    default String[] splitDescriptionForProduct(String description){
        if(description.length() <= 35){
            return new String[]{description};
        } else if(description.length() > 35 && description.length() <= 70){
            return new String[]{description.substring(0, 35), description.substring(35)};
        } else {
            return new String[]{description.substring(0, 35), description.substring(35, 70), description.substring(70)};
        }
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

    default void printCategorySpecs(Category category) {
        printCustomLineForCategory();

        printWithNullCheckingForCategory("Name", category.getName());
        printCustomLineForCategory();

        printWithNullCheckingForCategory("ParentName", category.getParentName());
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
            String[] splitedFeatures = splitDescriptionForProduct(features);

            for(int i = 0; i < splitedFeatures.length; i++){
                if(i == 0)
                    System.out.format("| %-20s | %-35s | %n", "Features", splitedFeatures[i]);
                else
                    System.out.format("| %-20s | %-35s | %n", "", splitedFeatures[i]);
            }
        } else {
            System.out.format("| %-20s | %-35s | %n", "Features", "Not Assigned");
        }
    }

    default String[] splitFeaturesForCategory(String features) {
        //TODO
        return null;
    }

}
