package view.process;

import controller.Control;
import controller.account.AdminControl;
import controller.account.CustomerControl;
import controller.account.VendorControl;
import controller.product.ProductControl;
import model.existence.Product;
import sun.net.www.protocol.file.FileURLConnection;
import view.menu.ListicMenu;
import view.menu.ListicOptionMenu;
import view.menu.Menu;
import view.PrintOptionSpecs;

import java.util.HashMap;

public class ProductProcessor extends ListicOptionProcessor implements PrintOptionSpecs {
    private static ProductProcessor productProcessor = null;
    private static ProductControl productControl = ProductControl.getController();
    private static AdminControl adminControl = AdminControl.getController();
    private static CustomerControl customerControl = CustomerControl.getController();
    private static VendorControl vendorControl = VendorControl.getController();
    private static ListicOptionMenu comparisonParentMenu = null;

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
        functionsHashMap.put("Increase", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return increaseQuantityCart(objects);
            }
        });
        functionsHashMap.put("Decrease", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return decreaseQuantityCart(objects);
            }
        });
        functionsHashMap.put("Remove From Cart", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeCartProduct(objects);
            }
        });
        functionsHashMap.put("Compare To", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                comparisonParentMenu = (ListicOptionMenu) objects[0];
                productControl.setComparingProducts(new Product[2]);
                productControl.setFirstComparingProduct(((Product) objects[1]).getID());
                return Menu.makeMenu("Comparison Menu");
            }
        });
        functionsHashMap.put("Choose The Comparing Product", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return ListicMenu.makeListicMenu("Comparison Listic Menu");
            }
        });
        functionsHashMap.put("Do The Compare", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                if(productControl.getComparingProducts()[0] != null && productControl.getComparingProducts()[1] != null)
                    return ListicOptionMenu.makeMenu("Comparing Products Menu", Menu.makeMenu("Comparison Menu"),
                            productControl.getComparingProducts()[1].getID());
                else
                {
                    System.out.println("Please First Choose The Comparing Product");
                    return Menu.makeMenu("Comparison Menu");
                }
            }
        });
        functionsHashMap.put("Give A Score", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return giveScoreToProduct(objects);
            }
        });
        functionsHashMap.put("Back", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return comparisonParentMenu;
            }
        });

    }

    public static ProductProcessor getInstance(){
        if(productProcessor == null)
            productProcessor = new ProductProcessor();

        return productProcessor;
    }

    public static void setMenu(ListicOptionMenu productMenu, String ID){
        //TODO(OTHERS)
        if(productMenu.getParentName().contains("Manage All Products Listic Menu") ||
                productMenu.getParentName().contains("Manage Add Product Requests Listic Menu")) {

            productMenu.setOption(productControl.getProductById(ID));
        } else if(productMenu.getName().contains("Cart Product Menu")) {
            productMenu.setOption(customerControl.getCartProductByID(ID));
        } else if(productMenu.getName().equals("Comparing Menu")) {
            productMenu.setOption(productControl.getComparingProducts());
        } else if(productMenu.getName().equals("Product Of Log Menu")) {
            //System.out.println("Step 1 " + productMenu.getName());
            Object[] objects = new Object[4];
            objects[0] = productControl.getProductById(ID);
            objects[1] = customerControl.getProductOfLog(ID);
            objects[2] = vendorControl.getCustomerName();
            objects[3] = customerControl.getScore(ID);
            productMenu.setOption(objects);
        } else {
            //System.out.println("Step 2 " + productMenu.getName());
            productMenu.setOption(productControl.getEditedProductByID(ID));
        }

    }

    public Menu editProduct(Object... objects){
        //Todo
        Object[] parameters = objects.clone();
        ListicOptionMenu productMenu = (ListicOptionMenu) objects[0];
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
        return ((ListicOptionMenu)parameters[0]).getParentMenu();
    }

    public Menu acceptAddingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        System.out.println(adminControl.approveProductByID(((Product)parameters[1]).getID()).getMessage());
        return ((ListicOptionMenu)parameters[0]).getParentMenu();
    }

    public Menu declineAddingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        productControl.removeProductById(((Product)parameters[1]).getID());
        System.out.println("Adding Product Declined Successfully. \uD83D\uDE2C");
        return ((ListicOptionMenu)parameters[0]).getParentMenu();
    }

    public Menu acceptEditingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        System.out.println(adminControl.acceptEditingProductByID(((Product)parameters[1]).getID()).getMessage());
        return ((ListicOptionMenu)parameters[0]).getParentMenu();
    }

    public Menu declineEditingProduct(Object... objects) {
        Object[] parameters = objects.clone();
        productControl.removeEditingProductById(((Product)parameters[1]).getID());
        System.out.println("Product Editing Declined Successfully. \uD83D\uDE2C");
        return ((ListicOptionMenu)parameters[0]).getParentMenu();
    }

    public Menu viewProductLastApproved(Object... objects)
    {
        Object[] parameters = objects.clone();
        ListicOptionMenu productMenu = ((ListicOptionMenu) parameters[0]);
        Product product = ((Product) parameters[1]);
        printProductSpecs(productControl.getProductById(product.getID()));
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
        ListicOptionMenu productMenu = (ListicOptionMenu) objects[0];
        if(product.isCountable()) {
            int count = getCount(product);
            System.out.println(customerControl.addToCartCountable(getUserNameForCart(), product.getID(), count).getMessage());
        } else {
            double amount = getAmount(product);
            System.out.println(customerControl.addToCartUnCountable(getUserNameForCart(), product.getID(), amount).getMessage());
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
            return "temp";
        }
    }

    public Menu increaseQuantityCart(Object... objects)
    {
        Product product = (Product) objects[1];
        ListicOptionMenu productMenu = (ListicOptionMenu) objects[0];
        System.out.println("0. Back");

        if(product.isCountable())
        {
            System.out.println("Enter Additional Number, You Want From This Product: ");
            System.out.println("* " + productControl.getProductById(product.getID()).getCount() + " At Stock *");
            String command = scanner.nextLine().trim();
            if(command.equals("0"))
                return productMenu;
            System.out.println(customerControl.increaseCount(product.getID(), command).getMessage());
        }
        else
        {
            System.out.println("Enter Additional Amount You Tend To Buy From This Product, In Kilogram: ");
            System.out.println("* " + productControl.getProductById(product.getID()).getAmount() + " Kg At Stock *");
            String command = scanner.nextLine().trim();
            if(command.equals("0"))
                return productMenu;
            System.out.println(customerControl.increaseAmount(product.getID(), command).getMessage());
        }
        productMenu.setOption(customerControl.getCartProductByID(product.getID()));
        return productMenu.getParentMenu();

    }

    public Menu decreaseQuantityCart(Object... objects)
    {
        Product product = (Product) objects[1];
        ListicOptionMenu productMenu = (ListicOptionMenu) objects[0];
        System.out.println("0. Back");

        if(product.isCountable())
        {
            System.out.println("Enter subtractive Number, You Want From This Product: ");
            System.out.println("* " + productControl.getProductById(product.getID()).getCount() + " At Stock *");
            String command = scanner.nextLine().trim();
            if(command.equals("0"))
                return productMenu;
            System.out.println(customerControl.decreaseCount(product.getID(), command).getMessage());
        }
        else
        {
            System.out.println("Enter subtractive Amount You Tend To Buy From This Product, In Kilogram: ");
            System.out.println("* " + productControl.getProductById(product.getID()).getAmount() + " Kg At Stock *");
            String command = scanner.nextLine().trim();
            if(command.equals("0"))
                return productMenu;
            System.out.println(customerControl.decreaseAmount(product.getID(), command).getMessage());
        }
        productMenu.setOption(customerControl.getCartProductByID(product.getID()));
        return productMenu.getParentMenu();

    }

    public Menu removeCartProduct(Object... objects)
    {
        Product product = (Product) objects[1];
        ListicOptionMenu productMenu = (ListicOptionMenu) objects[0];
        System.out.println(customerControl.removeFromCartByID(product.getID()).getMessage());
        return productMenu.getParentMenu();
    }

    public static ListicOptionMenu getComparisonParentMenu() {
        return comparisonParentMenu;
    }

    public static void setComparisonParentMenu(ListicOptionMenu comparisonParentMenu) {
        ProductProcessor.comparisonParentMenu = comparisonParentMenu;
    }

    public Menu giveScoreToProduct(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Object[] products = (Object[]) objects[1];
        Product product = (Product) products[0];
        int score = 0;
        boolean flag = true;

        while (flag) {
            try {
                System.out.println("Please Enter The Score :");
                System.out.println("Score Must Be Between 0 & 5");
                score = Integer.parseInt(scanner.nextLine().trim());

                if (score < 0 || score > 5)
                    System.out.println("!Wrong Score!");
                else
                    flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            }
        }

        System.out.println(customerControl.setScore(product.getID(), score).getMessage());
        products[2] = customerControl.getScore(product.getID());
        return menu;
    }
}
