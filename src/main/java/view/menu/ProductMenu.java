package view.menu;

import com.google.gson.GsonBuilder;
import model.existence.Product;
import view.process.Processor;
import view.process.ProductProcessor;

import java.io.FileNotFoundException;

public class ProductMenu extends ListicOptionMenu {
    private Product product;

    public Menu execute(){
        processor = Processor.findProcessorWithName(processorName);
        Menu nextMenu = this;
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

        if(input != 0 && processor.isThereFunctionWithName(options.get(input - 1)))
        {
            nextMenu = processor.executeTheFunctionWithName(options.get(input - 1), this.getParentMenu(), product.getID());
        }

        return nextMenu;
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
