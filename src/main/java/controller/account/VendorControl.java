package controller.account;

import controller.Control;
import model.db.CategoryTable;
import model.db.OffTable;
import model.db.ProductTable;
import model.db.VendorTable;
import model.existence.Off;
import model.existence.Product;
import notification.Notification;

import java.sql.Date;
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
                productsIDs.add(product.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return productsIDs;
    }

    public Notification addProduct(Product product)
    {
        try {
            while (true) {
                String productId = generateProductID();
                if (ProductTable.isIDFree(productId)) {
                    product.setID(productId);
                    break;
                }
            }
            if(product.isCountable())
                VendorTable.addCountableProduct(product, Control.getUsername());
            else
                VendorTable.addUnCountableProduct(product, Control.getUsername());
            return Notification.ADD_PRODUCT;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    private String generateProductID()
    {
        char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
        StringBuilder ID = new StringBuilder("p");
        for(int i = 0; i < 7; ++i)
        {
            ID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);
        }
        return ID.toString();
    }

    public boolean isThereCategoryWithName(String categoryName) {
        try {
            return CategoryTable.isThereCategoryWithName(categoryName);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getAllOffNames(){
        ArrayList<String> offs = new ArrayList<>();
        try {
            for (Off vendorOff : OffTable.getVendorOffs(Control.getUsername())) {
                offs.add(vendorOff.getOffName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return offs;
    }

    public ArrayList<String> getAllOffIDs(){
        ArrayList<String> offs = new ArrayList<>();
        try {
            for (Off vendorOff : OffTable.getVendorOffs(Control.getUsername())) {
                offs.add(vendorOff.getOffID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return offs;
    }

    public Notification addOff(Off off){
       if (off.getOffName() == null)
           return Notification.UNCOMPLETED_OFF_NAME;
       if (off.getFinishDate() == null)
           return Notification.NOT_SET_FINISH_DATE;
       if (off.getOffPercent() <= 0 || off.getOffPercent() >= 100)
           return Notification.OUT_BOUND_OF_PERCENT;
       if(off.getProductIDs() == null || off.getProductIDs().size() == 0)
           return Notification.EMPTY_OFF_PRODUCTS;
       off.setVendorUsername(Control.getUsername());
       try {
           do {
               off.setOffID(setOffID());
           } while (OffTable.isThereOffWithID(off.getOffID()));
           off.setStatus(2);
           OffTable.addOff(off);
           return Notification.ADD_OFF;
       } catch (SQLException e) {
           return Notification.UNKNOWN_ERROR;
       } catch (ClassNotFoundException e) {
           return Notification.UNKNOWN_ERROR;
       }
    }

    private String setOffID(){
        char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
        StringBuilder offID = new StringBuilder("o");

        for(int i = 0; i < 7; ++i)
            offID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);

        return offID.toString();
    }

    public ArrayList<String> getNonOffProductsNames() {
        ArrayList<String> nonOffProducts = new ArrayList<>();
        try {
            for (Product product : VendorTable.getProductsWithUsername(Control.getUsername())) {
                if(!OffTable.isThereProductInOff(product.getID()))
                    nonOffProducts.add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return nonOffProducts;
    }

    public ArrayList<String> getNonOffProductsIDs() {
        ArrayList<String> nonOffProducts = new ArrayList<>();
        try {
            for (Product product : VendorTable.getProductsWithUsername(Control.getUsername())) {
                if(!OffTable.isThereProductInOff(product.getID()))
                    nonOffProducts.add(product.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return nonOffProducts;
    }

    public Notification editOffName(String offID, String offName)
    {
        try {
            if(OffTable.isThereEditingOffWithID(offID)) {
                Off off = OffTable.getSpecificEditingOffByID(offID);
                if(off.getOffName().equals(offName))
                    return Notification.DUPLICATE_OFF_VALUE;
                OffTable.editEditingOffName(off.getOffID() ,offName);
            } else {
                Off off = OffTable.getSpecificOff(offID);
                if(off.getOffName().equals(offName))
                    return Notification.DUPLICATE_OFF_VALUE;
                OffTable.changeOffStatus(offID, 3);
                off.setOffName(offName);
                off.setStatus(3);
                OffTable.addEditingOff(off);
            }
            return Notification.OFF_EDITED;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification editOffFinishDate(String offID, Date date)
    {
        try {
            if(OffTable.isThereEditingOffWithID(offID)) {
                Off off = OffTable.getSpecificEditingOffByID(offID);
                if(off.getFinishDate().compareTo(date) == 0)
                    return Notification.DUPLICATE_OFF_VALUE;
                if(date.compareTo(new Date(System.currentTimeMillis())) != 1 || date.compareTo(off.getStartDate()) != 1)
                    return Notification.WRONG_OFF_FINISH_DATE;
                OffTable.editEditingOffFinishDate(off.getOffID() ,date);
            } else {
                Off off = OffTable.getSpecificOff(offID);
                if(off.getFinishDate().compareTo(date) == 0)
                    return Notification.DUPLICATE_OFF_VALUE;
                if(date.compareTo(new Date(System.currentTimeMillis())) != 1 || date.compareTo(off.getStartDate()) != 1)
                    return Notification.WRONG_OFF_FINISH_DATE;
                OffTable.changeOffStatus(offID, 3);
                off.setFinishDate(date);
                off.setStatus(3);
                OffTable.addEditingOff(off);
            }
            return Notification.OFF_EDITED;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification editOffPercent(String offID, Double percent)
    {
        try {
            if(OffTable.isThereEditingOffWithID(offID)) {
                Off off = OffTable.getSpecificEditingOffByID(offID);
                if(off.getOffPercent() == percent)
                    return Notification.DUPLICATE_OFF_VALUE;
                if(!(percent > 0 && percent <= 100))
                    return Notification.INVALID_OFF_PERCENT;
                OffTable.editEditingOffPercent(off.getOffID(), percent);
            } else {
                Off off = OffTable.getSpecificOff(offID);
                if(off.getOffPercent() == percent)
                    return Notification.DUPLICATE_OFF_VALUE;
                if(!(percent > 0 && percent <= 100))
                    return Notification.INVALID_OFF_PERCENT;
                OffTable.changeOffStatus(offID, 3);
                off.setOffPercent(percent);
                off.setStatus(3);
                OffTable.addEditingOff(off);
            }
            return Notification.OFF_EDITED;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }


    public Notification removeOffWithID(String offID) {
        try {
            if(OffTable.isThereEditingOffWithID(offID))
                OffTable.removeEditingOff(offID);
            OffTable.removeOffByID(offID);
            return Notification.OFF_REMOVED;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }
}
