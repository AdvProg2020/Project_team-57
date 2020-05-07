package view.menu;

import com.google.gson.GsonBuilder;
import model.existence.Product;
import view.process.Processor;
import view.process.ProductListicProcessor;
import view.process.ProductProcessor;

import java.io.FileNotFoundException;

public class ProductMenu extends ListicOptionMenu {
    private ProductProcessor productProcessor = ProductProcessor.getInstance();
    private ProductListicMenu parentMenu;
    private Product product;

    /*public static ProductMenu makeMenu(String menuName, ProductListicMenu parentMenu) {
        String json = "";

        try {
            json = ProductMenu.getJsonFromDB(menuName);
        } catch (FileNotFoundException e) {
            System.out.println("ProductMenu File Couldn't Get Initialized! Please Contact Us As Soon As Possible :.(");
        }

        ProductMenu menu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ProductMenu.class);
        menu.setParentMenu(parentMenu);
        return menu;
    }

    public void show(){
        printProductSpecs();

        System.out.println("0. back");

        for(int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));

    }*/

    public void setMenu(String ID){
        listicOptionProcessor = ProductProcessor.getInstance();

    }

    public Menu execute(){
        boolean flag = true;
        int input = 0;

        while(flag){
            try {
                input = Integer.parseInt(scanner.nextLine().trim());

                if (input > options.size() || input < 0)
                    throw new InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                else
                    flag = false;

            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
        }

        if(input != 0 && options.get(input - 1).equals("Remove Product"))
        {
            productProcessor.removeProduct(product.getID());
            parentMenu.deleteProductFromListWithId(product.getID());
        }

        return parentMenu;
    }

    public void printProductSpecs(){
        printCustomLine();

        printCustomStatus(product.getStatus());
        printCustomLine();

        System.out.format("| %-20s | %-35s | %n", "Name", product.getName());
        printCustomLine();

        System.out.format("| %-20s | %-35s | %n", "Brand Name", product.getBrand());
        printCustomLine();

        System.out.format("| %-20s | %-35s | %n", "Seller Name", product.getSellerUserName());
        printCustomLine();

        printCustomCount(product);
        printCustomLine();

        System.out.format("| %-20s | %-35s | %n", "Category", product.getCategory());
        printCustomLine();

        printCustomDescription(product.getDescription());
        printCustomLine();

        System.out.format("| %-20s | %-35f | %n", "Price", product.getPrice());
        printCustomLine();

        System.out.format("| %-20s | %-35f | %n", "Average Score", product.getAverageScore());
        printCustomLine();
    }

    public void printCustomLine(){
        System.out.println("+----------------------+-------------------------------------+");
    }

    public void printCustomStatus(int status){
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

    public void printCustomCount(Product product){
        if(product.isCountable()){
            System.out.format("| %-20s | %-35d | %n", "Count", product.getCount());
        } else {
            System.out.format("| %-20s | %-35f | %n", "Average Score", product.getAmount());
        }
    }

    public void printCustomDescription(String description){
        String[] splitDescription = splitDescription(description);

        for(int i = 0; i < splitDescription.length; i++){
            if(i == 0)
                System.out.format("| %-20s | %-35s | %n", "Description", splitDescription[i]);
            else
                System.out.format("| %-20s | %-35s | %n", "", splitDescription[i]);
        }
    }

    public String[] splitDescription(String description){
        if(description.length() <= 35){
            return new String[]{description};
        } else if(description.length() > 35 && description.length() <= 70){
            return new String[]{description.substring(0, 35), description.substring(35)};
        } else {
            return new String[]{description.substring(0, 35), description.substring(35, 70), description.substring(70)};
        }
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
