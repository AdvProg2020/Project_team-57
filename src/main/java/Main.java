import model.db.DiscountTable;
import model.db.ProductTable;
import model.existence.Discount;
import model.existence.Product;
import view.menu.Menu;
import view.process.Processor;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        /*try {
            ProductTable.tempAddProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        /*try {
            addDiscountCodes();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

/*        for (Product showingProduct : ProductTable.getAllShowingProducts()) {
            System.out.println(showingProduct.getName() + ": " + showingProduct.getApprovalDate());
        }*/

        Processor.initProcessHashMaps();
        Menu menu = Menu.makeMenu("Main Menu");

        while(menu != null) {
            menu.show();
            menu = menu.execute();
        }

    }

    private static void addDiscountCodes() throws SQLException, ClassNotFoundException {
        Discount discount = new Discount();
        discount.setID("d1234567");
        discount.setCode("HelloWorld");
        HashMap<String, Integer> customers = new HashMap<>();
        customers.put("AliKhd", 4);
        customers.put("Naghii", 2);
        discount.setCustomersWithRepetition(customers);
        DiscountTable.addDiscount(discount);
        discount.setID("a2345678");
        discount.setCode("HelloWorld");
        customers = new HashMap<>();
        customers.put("Karimi", 3);
        customers.put("Mostafa", 0);
        discount.setCustomersWithRepetition(customers);
        DiscountTable.addDiscount(discount);
        discount.setID("a2345679");
        discount.setCode("FuckAlexis");
        customers = new HashMap<>();
        customers.put("Alexis", 3);
        customers.put("Molly", 0);
        customers.put("Arya Fae", 20);
        customers.put("Sasha Grey", 15);
        customers.put("Karla Kush", 20);
        customers.put("Riley Reid", 2);
        customers.put("Danni Daniels", 12);
        customers.put("Aleta Ocean", 8);
        discount.setCustomersWithRepetition(customers);
        DiscountTable.addDiscount(discount);
    }
}
