package controller.account;

import controller.Control;
import model.db.VendorTable;
import model.existence.Product;

import java.sql.SQLException;
import java.util.ArrayList;

public class VendorControl extends AccountControl{
    private static VendorControl vendorControl = null;

    public static VendorControl getController() {
        if (vendorControl == null)
            vendorControl = new VendorControl();

        return vendorControl;
    }

    public ArrayList<String> getVendorProductNames() {
        ArrayList<String> productNames = new ArrayList<>();
        try {
            for (Product product : VendorTable.getProductsWithUsername(Control.getUsername())) {
                productNames.add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productNames;
    }

    public ArrayList<String> getVendorProductIDs() {
        ArrayList<String> productsIDs = new ArrayList<>();
        try {
            for (Product product : VendorTable.getProductsWithUsername(Control.getUsername())) {
                productsIDs.add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productsIDs;
    }

    private String generateProductID()
    {
        char[] validchars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
        String ID = "p";
        for(int i = 0; i < 7; ++i)
        {
            ID += validchars[((int)(Math.random() * 1000000)) % validchars.length];
        }
        return ID;
    }
}
