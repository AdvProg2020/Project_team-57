package view.process;


import controller.Control;
import controller.product.ProductControl;
import view.menu.ProductListicMenu;
import view.menu.ProductMenu;

public class ProductListicProcessor extends Processor {
    private static ProductListicProcessor processor = null;
    private ProductControl controller = null;
    private ProductListicProcessor() {
        controller = ProductControl.getController();
    }

    public static ProductListicProcessor getInstance() {
        if(processor == null)
            processor = new ProductListicProcessor();
        return processor;
    }

    public ProductMenu getProductMenu(String menuName, String productID, ProductListicMenu parentMenu)
    {
        ProductMenu productMenu = ProductMenu.makeMenu(menuName, parentMenu);
        productMenu.setProduct(controller.getProductById(productID));
        return productMenu;
    }

    public String chooseProductMenuType() {
        return controller.getProductMenuType();
    }
}
