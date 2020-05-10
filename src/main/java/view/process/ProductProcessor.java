package view.process;

import com.google.gson.GsonBuilder;
import controller.Control;
import controller.account.AdminControl;
import controller.product.ProductControl;
import model.existence.Product;
import view.menu.Menu;
import view.menu.PrintProductSpecs;
import view.menu.ProductMenu;

import java.util.HashMap;

public class ProductProcessor extends ListicOptionProcessor implements PrintProductSpecs {
    private static ProductProcessor productProcessor = null;
    private static ProductControl productControl = ProductControl.getController();
    private static AdminControl adminControl = AdminControl.getController();

    private ProductProcessor(){
        this.functionsHashMap = new HashMap<>();
        functionsHashMap.put("Remove Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeProduct(objects);
            }
        });
        functionsHashMap.put("Edit Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editProduct(objects);
            }
        });
        functionsHashMap.put("Accept Adding Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return acceptAddingProduct(objects);
            }
        });
        functionsHashMap.put("Decline Adding Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return declineAddingProduct(objects);
            }
        });
        functionsHashMap.put("Accept Editing Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return acceptEditingProduct(objects);
            }
        });
        functionsHashMap.put("Decline Editing Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return declineEditingProduct(objects);
            }
        });
        functionsHashMap.put("View Product Last Approved Version", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return viewProductLastApproved(objects);
            }
        });
        functionsHashMap.put("Add To Cart", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return addToCart(objects);
            }
        });

    }

    public static ProductProcessor getInstance(){
        if(productProcessor == null)
            productProcessor = new ProductProcessor();

        return productProcessor;
    }

    public static ProductMenu setMenu(String json, String ID){
        ProductMenu productMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ProductMenu.class);
        //TODO(OTHERS)
        if(json.contains("Manage All Products Listic Menu") || json.contains("Manage Add Product Requests Listic Menu")) {
            productMenu.setProduct(productControl.getProductById(ID));
        } else {
            productMenu.setProduct(productControl.getEditedProductByID(ID));
        }
        return productMenu;
    }

    public Menu editProduct(Object... objects){
        //Todo
        Object[] parameters = objects.clone();
        ProductMenu productMenu = (ProductMenu)objects[0];
        Product product = (Product)objects[1];
        EditProductProcessor.getInstance(productMenu, product);
        return EditProductProcessor.getInstance().editProductMenuManage(product);
    }

    public void viewBuyers(Object... objects){
        //Todo
    }

    public Menu removeProduct(Object... objects){
        Object[] parameters = objects.clone();
        System.out.println(productControl.removeProductById(((Product)parameters[1]).getID()).getMessage());
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu acceptAddingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        System.out.println(adminControl.approveProductByID(((Product)parameters[1]).getID()).getMessage());
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu declineAddingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        productControl.removeProductById(((Product)parameters[1]).getID());
        System.out.println("Adding Product Declined Successfully. \uD83D\uDE2C");
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu acceptEditingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        System.out.println(adminControl.acceptEditingProductByID(((Product)parameters[1]).getID()).getMessage());
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu declineEditingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        productControl.removeEditingProductById(((Product)parameters[1]).getID());
        System.out.println("Product Editing Declined Successfully. \uD83D\uDE2C");
        return ((ProductMenu)parameters[0]).getParentMenu();
    }

    public Menu viewProductLastApproved(Object... objects)
    {
        Object[] parameters = objects.clone();
        ProductMenu productMenu = ((ProductMenu) parameters[0]);
        Product product = ((Product) parameters[1]);
        printSpecificProductSpecs(productControl.getProductById(product.getID()));
        while(true)
        {
            System.out.println("0. Back");
            try {
                String input = scanner.nextLine().trim();
                int command = Integer.parseInt(input);
                if(command == 0)
                {
                    return productMenu;
                }
                else
                    throw new Menu.InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (Menu.InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public Menu addToCart(Object... objects) {
        Object[] parameters = objects.clone();
        Product product = (Product) objects[1];
        ProductMenu productMenu = (ProductMenu) objects[0];

        if(product.isCountable()) {
            int count = getCount(product);
            System.out.println(adminControl.addToCartCountable(getUserNameForCart(), product.getID(), count).getMessage());
        } else {
            double amount = getAmount(product);
            System.out.println(adminControl.addToCartUnCountable(getUserNameForCart(), product.getID(), amount).getMessage());
        }

        return productMenu;
    }

    public int getCount(Product product) {
        int count = 0;
        boolean flag = true;

        while(flag) {
            System.out.println("How Many Of These Do You Want ? :)");

            try {
                count = Integer.parseInt(scanner.nextLine().trim());
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            }
        }

        return count;
    }

    public double getAmount(Product product) {
        double amount = 0;
        boolean flag = true;

        while(flag) {
            System.out.println("How Much Of These Do You Want ? :)");

            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Please Enter A Double Number");
            } catch (NullPointerException e) {
                System.out.println("Please Enter A Double Number");
            }
        }

        return amount;
    }

    public String getUserNameForCart() {
        if(Control.isLoggedIn()) {
            return Control.getUsername();
        } else {
            return "Temp";
        }
    }
}
