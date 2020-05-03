package view.process;

import controller.Control;
import controller.account.AdminControl;
import controller.product.ProductControl;

public class ProductProcessor extends Processor{
    private static ProductProcessor productProcessor = null;

    private ProductControl productControl = ProductControl.getController();
    private ProductProcessor(){

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

    public void removeProduct(String productID){
        System.out.println(productControl.removeProductById(productID).getMessage());
    }
}
