package view.menu;

import model.existence.Product;

public interface PrintProductSpecs {
    default void printSpecificProductSpecs(Product product){
        printCustomLine();

        printCustomStatus(product.getStatus());
        printCustomLine();

        printWithNullChecking("Name", product.getName());
        printCustomLine();

        printWithNullChecking("Brand Name", product.getBrand());
        printCustomLine();

        printWithNullChecking("Seller Name", product.getSellerUserName());
        printCustomLine();

        printCustomCount(product);
        printCustomLine();

        printWithNullChecking("Category", product.getCategory());
        printCustomLine();

        printCustomDescription(product.getDescription());
        printCustomLine();

        System.out.format("| %-20s | %-35f | %n", "Price", product.getPrice());
        printCustomLine();

        System.out.format("| %-20s | %-35f | %n", "Average Score", product.getAverageScore());
        printCustomLine();
    }

    default void printCustomLine(){
        System.out.println("+----------------------+-------------------------------------+");
    }

    default void printWithNullChecking(String fieldName, String fieldValue){
        if(fieldValue == null)
            System.out.format("| %-20s | %-35s | %n", fieldName, "Not Assigned");
        else
            System.out.format("| %-20s | %-35s | %n", fieldName, fieldValue);
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

    default void printCustomDescription(String description){
        if(description != null)
        {
            String[] splitDescription = splitDescription(description);

            for(int i = 0; i < splitDescription.length; i++){
                if(i == 0)
                    System.out.format("| %-20s | %-35s | %n", "Description", splitDescription[i]);
                else
                    System.out.format("| %-20s | %-35s | %n", "", splitDescription[i]);
            }
        }
        else
        {
            System.out.format("| %-20s | %-35s | %n", "Description", "Not Assigned");
        }
    }

    default String[] splitDescription(String description){
        if(description.length() <= 35){
            return new String[]{description};
        } else if(description.length() > 35 && description.length() <= 70){
            return new String[]{description.substring(0, 35), description.substring(35)};
        } else {
            return new String[]{description.substring(0, 35), description.substring(35, 70), description.substring(70)};
        }
    }
}
