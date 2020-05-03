package view.process;

import controller.product.ProductControl;
import view.menu.Menu;

import java.util.HashMap;

public class ProductProcessor extends Processor{
    private static ProductControl productControl = ProductControl.getController();
    private static ProductProcessor productProcessor = null;

    private ProductProcessor(){
        functionsHashMap = new HashMap<String, FunctioningOption>();
        functionsHashMap.put("Edit Product", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                editProduct();
                return null;
            }
        });
        functionsHashMap.put("View Buyers", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                viewBuyers();
                return null;
            }
        });
        functionsHashMap.put("Remove Product", new FunctioningOption() {
            @Override
            public Menu doTheThing() {
                removeProduct();
                return null;
            }
        });

    }

    public static ProductProcessor getInstance(){
        if(productProcessor == null)
            productProcessor = new ProductProcessor();

        return productProcessor;
    }

    public void editProduct(){

    }

    public void viewBuyers(){

    }

    public void removeProduct(){

    }
}
