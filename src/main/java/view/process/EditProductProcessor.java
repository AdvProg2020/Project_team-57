package view.process;

import controller.product.ProductControl;
import model.existence.Product;
import view.menu.Menu;
import view.menu.ProductMenu;

import java.util.HashMap;

public class EditProductProcessor extends Processor {
    private static ProductControl productControl = ProductControl.getController();
    private static EditProductProcessor editProductProcessor = null;
    private static ProductMenu parentMenu;
    private static Product product;

    private EditProductProcessor(){
        this.functionsHashMap = new HashMap<>();
        functionsHashMap.put("Name", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Name", objects);
            }
        });
        functionsHashMap.put("Brand", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Brand", objects);
            }
        });
        functionsHashMap.put("Count", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Count", objects);
            }
        });
        functionsHashMap.put("Amount", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Amount", objects);
            }
        });
        functionsHashMap.put("Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Category", objects);
            }
        });
        //TODO for editing Category function
        functionsHashMap.put("Description", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Description", objects);
            }
        });
        functionsHashMap.put("Price", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editField("Price", objects);
            }
        });
        functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return back();
            }
        });

    }

    public static EditProductProcessor getInstance(){
        if(editProductProcessor == null)
            editProductProcessor = new EditProductProcessor();

        return editProductProcessor;
    }

    public static EditProductProcessor getInstance(ProductMenu parentMenu, Product product) {
        if(editProductProcessor == null)
            editProductProcessor = new EditProductProcessor();

        EditProductProcessor.parentMenu = parentMenu;
        EditProductProcessor.product = product;
        return editProductProcessor;
    }

    public Menu editField(String fieldName, Object... objects) {
        System.out.println("Please Enter The new Field Value :");
        String newFieldValue = scanner.nextLine().trim();
        System.out.println(productControl.editField(fieldName, newFieldValue, product.getID()).getMessage());
        product = productControl.getEditedProductByID(product.getID());
        return editProductMenuManage(product);
    }

    public Menu editProductMenuManage(Product product){
        if(product.isCountable())
            return Menu.makeMenu("Edit Product Menu For Countable");

        return Menu.makeMenu("Edit Product Menu For UnCountable");
    }

    public Menu back(){
        parentMenu.setProduct(product);
        return parentMenu;
    }

}
