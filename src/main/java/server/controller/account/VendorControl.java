package server.controller.account;

import server.controller.product.ProductControl;
import server.model.db.*;
import server.model.existence.Account;
import server.model.existence.Log;
import server.model.existence.Off;
import server.model.existence.Product;
import notification.Notification;
import server.server.RandomGenerator;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public class VendorControl extends AccountControl{
    private static VendorControl vendorControl = null;

    public static VendorControl getController() {
        if (vendorControl == null)
            vendorControl = new VendorControl();

        return vendorControl;
    }

    public String addProduct(Product product, ArrayList<Notification> addingProductNotifications, String username) {
        try {
            addingProductNotifications.addAll(checkProductFields(product));
            if (addingProductNotifications.isEmpty()) {
                String productID = "p" + generateRandomNumber(7, s -> {
                    try {
                        return !ProductTable.isIDFree(s);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                });
                if (product.isCountable())
                    VendorTable.addCountableProduct(product, username);
                else
                    VendorTable.addUnCountableProduct(product, username);

                addingProductNotifications.add(Notification.ADD_PRODUCT);
                return productID;
            }
        } catch (SQLException | ClassNotFoundException e) {
            return null;
        }

        return null;
    }

    private ArrayList<Notification> checkProductFields(Product product) throws SQLException, ClassNotFoundException {
        ArrayList<Notification> checkNotifications = new ArrayList<>();

        if(product.getPrice() == 0) {
            checkNotifications.add(Notification.EMPTY_PRODUCT_PRICE);
        } if(product.getName() == null || product.getName().isEmpty()) {
            checkNotifications.add(Notification.EMPTY_PRODUCT_NAME);
        } if(product.getCategory() == null || product.getCategory().isEmpty()) {
            product.setCategory("All Products");
        } if(product.getCategory() != null && !CategoryTable.isThereCategoryWithName(product.getCategory())) {
            checkNotifications.add(Notification.INVALID_PRODUCT_CATEGORY);
        } if(product.isCountable() && product.getCount() == 0) {
            checkNotifications.add(Notification.EMPTY_PRODUCT_COUNT);
        } if(!product.isCountable() && product.getAmount() == 0) {
            checkNotifications.add(Notification.EMPTY_PRODUCT_AMOUNT);
        } if(product.getBrand() == null || product.getBrand().isEmpty()) {
            checkNotifications.add(Notification.EMPTY_PRODUCT_BRAND);
        } if(product.getDescription() == null || product.getDescription().isEmpty()) {
            checkNotifications.add(Notification.EMPTY_PRODUCT_DESCRIPTION);
        }

        return checkNotifications;
    }

    public Notification editProduct(Product currentProduct, Product editingProduct, String username) {
        Notification editProductNotification = null;

        try {
            editProductNotification = checkEditingProduct(currentProduct, editingProduct);

            if (editProductNotification == null) {
                editingProduct.setStatus(3);
                editingProduct.setSellerUserName(username);
                ProductTable.setProductStatus(editingProduct.getID(), 3);
                if (EditingProductTable.isIDFree(editingProduct.getID())) {
                    EditingProductTable.addProduct(editingProduct);
                } else {
                    if (editingProduct.isCountable())
                        EditingProductTable.updateCountableProduct(editingProduct);
                    else
                        EditingProductTable.updateUnCountableProduct(editingProduct);
                }
                editProductNotification = Notification.EDIT_PRODUCT;
            }
        } catch (SQLException | ClassNotFoundException e) {
            editProductNotification = Notification.UNKNOWN_ERROR;
        }

        return editProductNotification;
    }

    private Notification checkEditingProduct(Product currentProduct, Product editingProduct) throws SQLException, ClassNotFoundException {

        if(editingProduct.getPrice() == 0)
            editingProduct.setPrice(currentProduct.getPrice());

        if(editingProduct.getName() == null || editingProduct.getName().isEmpty())
            editingProduct.setName(currentProduct.getName());

        if(editingProduct.getCategory() == null || editingProduct.getCategory().isEmpty())
            editingProduct.setCategory(currentProduct.getCategory());
        else if(editingProduct.getCategory() != null && !CategoryTable.isThereCategoryWithName(editingProduct.getCategory()))
            return Notification.INVALID_PRODUCT_CATEGORY;

        if(editingProduct.isCountable() && editingProduct.getCount() == 0)
            editingProduct.setCount(currentProduct.getCount());
        if(!editingProduct.isCountable() && editingProduct.getAmount() == 0)
            editingProduct.setAmount(currentProduct.getAmount());

        if(editingProduct.getBrand() == null || editingProduct.getBrand().isEmpty())
            editingProduct.setBrand(currentProduct.getBrand());

        if(editingProduct.getDescription() == null || editingProduct.getDescription().isEmpty())
            editingProduct.setDescription(currentProduct.getDescription());

        return null;
    }

/*    private String generateProductID()
    {
        char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
        StringBuilder ID = new StringBuilder("p");
        for(int i = 0; i < 7; ++i)
        {
            ID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);
        }
        return ID.toString();
    }*/

    public boolean isThereCategoryWithName(String categoryName) {
        try {
            return CategoryTable.isThereCategoryWithName(categoryName);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return false;
    }

    public ArrayList<Off> getAllOffs(String usernameByAuth) {
        try {
            OffTable.removeOutDatedOffs();
            return OffTable.getVendorOffs(usernameByAuth);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification addOff(Off off, String username){
        if (off.getOffName() == null)
            return Notification.UNCOMPLETED_OFF_NAME;
        if (off.getFinishDate() == null)
            return Notification.NOT_SET_FINISH_DATE;
        if (off.getOffPercent() <= 0 || off.getOffPercent() >= 100)
            return Notification.OUT_BOUND_OF_PERCENT;
        if(off.getProductIDs() == null || off.getProductIDs().size() == 0)
            return Notification.EMPTY_OFF_PRODUCTS;
        if(off.getStartDate().compareTo(off.getFinishDate()) > -1)
            return Notification.START_DATE_AFTER_FINISH_DATE;
        off.setVendorUsername(username);
        try {

            off.setOffID("o" + generateRandomNumber(7, s -> {
                try {
                    return OffTable.isThereOffWithID(s);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            }));
            off.setStatus(2);

            OffTable.addOff(off);
            return Notification.ADD_OFF;
        } catch (SQLException e) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public ArrayList<Product> getNonOffProducts(String username, String... exceptions) {
        ArrayList<Product> nonOffProducts = new ArrayList<>();
        try {
            OffTable.removeOutDatedOffs();
            if(exceptions != null && exceptions.length > 0) {
                for (String exception : exceptions) {
                    for (String productID : OffTable.getSpecificOff(exception).getProductIDs()) {
                        nonOffProducts.add(ProductTable.getProductByID(productID));
                    }
                }
            }
            for (Product product : VendorTable.getProductsWithUsername(username)) {
                if(!OffTable.isThereProductInOffIgnoreStatus(product.getID()) && product.getStatus() != 2)
                    nonOffProducts.add(product);
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return nonOffProducts;
    }

    public ArrayList<Log> getAllVendorLogs(String username) {
        try {
            return LogTable.getAllVendorLogs(username);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public ArrayList<Account> getProductBuyers(String productID) {
        ArrayList<Account> customers = new ArrayList<>();
        try {
            for (String account : LogTable.getAllCustomerUsernamesForProduct(productID)) {
                customers.add(AccountTable.getAccountByUsername(account));
            }
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return customers;
    }

    public double getMaxSale(String productID) {
        try {
            return LogTable.getMaxSaleByID(productID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    public int getMaxCountOfSale(String productID) {
        try {
            return LogTable.getMaxCountOfSaleByProductID(productID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    public double getMaxAmountOfSale(String productID) {
        try {
            return LogTable.getMaxAmountOfSaleByProductID(productID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    public ArrayList<Product> getAllProducts(String username) {
        try {
            OffTable.removeOutDatedOffs();
            return VendorTable.getProductsWithUsername(username);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    @Deprecated
    public Notification editOff(Off off, File offImageFile) {
        if (off.getOffName() == null)
            return Notification.UNCOMPLETED_OFF_NAME;
        if (off.getFinishDate() == null)
            return Notification.NOT_SET_FINISH_DATE;
        if (off.getOffPercent() <= 0 || off.getOffPercent() >= 100)
            return Notification.OUT_BOUND_OF_PERCENT;
        if(off.getProductIDs() == null || off.getProductIDs().size() == 0)
            return Notification.EMPTY_OFF_PRODUCTS;
        if(off.getStartDate().compareTo(off.getFinishDate()) > -1)
            return Notification.START_DATE_AFTER_FINISH_DATE;
        try {
            off.setStatus(3);
            if(offImageFile != null) {
                ProductControl.getController().setEditingOffPicture(off.getOffID(), offImageFile);
            } else {
                ProductControl.getController().deleteEditingOffPicture(off.getOffID());
            }
            OffTable.changeOffStatus(off.getOffID(), 3);
            if(ProductControl.getController().isOffEditing(off.getOffID())) {
                OffTable.removeEditingOff(off.getOffID());
            }
            OffTable.addEditingOff(off);
            return Notification.EDIT_OFF;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification editOff(Off off) {
        if (off.getOffName() == null)
            return Notification.UNCOMPLETED_OFF_NAME;
        if (off.getFinishDate() == null)
            return Notification.NOT_SET_FINISH_DATE;
        if (off.getOffPercent() <= 0 || off.getOffPercent() >= 100)
            return Notification.OUT_BOUND_OF_PERCENT;
        if(off.getProductIDs() == null || off.getProductIDs().size() == 0)
            return Notification.EMPTY_OFF_PRODUCTS;
        if(off.getStartDate().compareTo(off.getFinishDate()) > -1)
            return Notification.START_DATE_AFTER_FINISH_DATE;
        try {
            off.setStatus(3);
            OffTable.changeOffStatus(off.getOffID(), 3);
            if(ProductControl.getController().isOffEditing(off.getOffID())) {
                OffTable.removeEditingOff(off.getOffID());
            }
            OffTable.addEditingOff(off);
            return Notification.EDIT_OFF;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

}
