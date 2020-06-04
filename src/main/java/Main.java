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

        Processor.initProcessHashMaps();
        Menu menu = Menu.makeMenu("Main Menu");

        while(menu != null) {
            menu.show();
            menu = menu.execute();
        }

    }
}
