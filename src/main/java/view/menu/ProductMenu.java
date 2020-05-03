package view.menu;

import com.google.gson.GsonBuilder;
import model.existence.Product;
import view.process.Processor;
import view.process.ProductProcessor;

import java.io.FileNotFoundException;

public class ProductMenu extends Menu {
    private ProductListicMenu parentMenu;
    private Product product;

    public static ProductMenu makeMenu(String menuName, ProductListicMenu parentMenu) {
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
        printProductSpecs(product);

        System.out.println("0. back");

        for(int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i));

    }

    public Menu execute(){
        processor = ProductProcessor.getInstance();
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

        if(input == 0) {
            nextMenu = parentMenu;
        } else if(options.get(input - 1).equals("Edit Product")){

        } else if(options.get())

        return nextMenu;
    }

    public void printProductSpecs(Product product){

    }

    public void setParentMenu(ProductListicMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
