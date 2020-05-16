package controller.product;

import controller.Control;
import model.db.CategoryTable;
import model.db.EditingProductTable;
import model.db.OffTable;
import model.db.ProductTable;
import model.existence.Category;
import model.existence.Off;
import model.existence.Product;
import notification.Notification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class ProductControl extends Control {
    private static ProductControl productControl = null;
    private boolean isOffListic;
    private String listicOffID;
    private Product[] comparingProducts = null;

    public Product[] getComparingProducts() {
        return comparingProducts;
    }

    public void setComparingProducts(Product[] comparingProducts) {
        this.comparingProducts = comparingProducts;
    }

    public void setFirstComparingProduct(String productID)
    {
        try {
            this.comparingProducts[0] = ProductTable.getProductByID(productID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSecondComparingProduct(String productID)
    {
        try {
            this.comparingProducts[1] = ProductTable.getProductByID(productID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isOffListic() {
        return isOffListic;
    }

    public void setOffListic(boolean offListic) {
        isOffListic = offListic;
    }

    public String getListicOffID() {
        return listicOffID;
    }

    public void setListicOffID(String listicOffID) {
        this.listicOffID = listicOffID;
    }

    public Product getProductById(String productId) {
        try {
            return ProductTable.getProductByID(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Notification removeProductById(String productId) {
        try {
            ProductTable.removeProductByID(productId);
            if (!EditingProductTable.isIDFree(productId))
                EditingProductTable.removeProductById(productId);
            return Notification.REMOVE_PRODUCT_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public static ProductControl getController() {
        if (productControl == null)
            productControl = new ProductControl();
        return productControl;
    }

    public ArrayList<String> getAllProductNames() {
        ArrayList<String> allProductNames = new ArrayList<>();
        try {
            for (Product product : ProductTable.getAllProducts()) {
                allProductNames.add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return allProductNames;
    }

    public ArrayList<String> getAllProductIDs() {
        ArrayList<String> allProductIDs = new ArrayList<>();
        try {
            for (Product product : ProductTable.getAllProducts()) {
                allProductIDs.add(product.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return allProductIDs;
    }

    public Notification editField(String fieldName, String newField, String ID) {
        try {
            if(fieldName.equals("Category") && !CategoryTable.isThereCategoryWithName(newField))
                return Notification.INVALID_CATEGORY_NAME;

            if (ProductTable.getProductByID(ID).getStatus() == 2)
                return Notification.PRODUCT_NOT_AVAILABLE;

            if (checkFieldEquality(fieldName, newField, ID))
                return Notification.SAME_FIELD_ERROR;

            if (ProductTable.getProductByID(ID).getStatus() == 1)
                ProductTable.setProductStatus(ID, 3);

            if (EditingProductTable.isIDFree(ID))
                EditingProductTable.addProduct(ProductTable.getProductByID(ID));

            editSpecificField(fieldName, newField, ID);

            return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return Notification.UNKNOWN_ERROR;
        }
    }

    private void editSpecificField(String fieldName, String newField, String ID) {
        try {
            if (fieldName.equals("Name") || fieldName.equals("Brand") ||
                    fieldName.equals("Category") || fieldName.equals("Description")) {
                if (fieldName.equals("Name"))
                    fieldName = "ProductName";
                EditingProductTable.editFieldWithName(ID, fieldName, newField);
            } else if (fieldName.equals("Count"))
                EditingProductTable.changeProductCount(ID, Integer.parseInt(newField));

            else if (fieldName.equals("Amount"))
                EditingProductTable.changeProductAmount(ID, Double.parseDouble(newField));

            else if (fieldName.equals("Price"))
                EditingProductTable.changeProductPrice(ID, Double.parseDouble(newField));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkFieldEquality(String fieldName, String newField, String ID) {
        Product product = getEditedProductByID(ID);

        if (fieldName.equals("Name")) {
            if (product.getName() == null)
                return false;
            return product.getName().equals(newField);
        } else if (fieldName.equals("Brand")) {
            if (product.getBrand() == null)
                return false;
            return product.getBrand().equals(newField);
        } else if (fieldName.equals("Count")) {
            return Integer.parseInt(newField) == product.getCount();
        } else if (fieldName.equals("Amount")) {
            return Double.parseDouble(newField) == product.getAmount();
        } else if (fieldName.equals("Category")) {
            if (product.getCategory() == null)
                return false;
            return product.getCategory().equals(newField);
        } else if (fieldName.equals("Description")) {
            if (product.getDescription() == null)
                return false;
            return product.getDescription().equals(newField);
        } else if (fieldName.equals("Price")) {
            return Double.parseDouble(newField) == product.getPrice();
        }

        return false;
    }

    public Product getEditedProductByID(String ID) {
        try {
            if (EditingProductTable.isIDFree(ID)) {
                return ProductTable.getProductByID(ID);
            } else {
                return EditingProductTable.getEditingProductWithID(ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getAllUnApprovedProductNames() {
        ArrayList<String> unApprovedProducts = new ArrayList<>();
        try {
            for (Product product : ProductTable.getAllUnApprovedProducts()) {
                unApprovedProducts.add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return unApprovedProducts;
    }

    public ArrayList<String> getAllUnApprovedProductIDs() {
        ArrayList<String> unApprovedProducts = new ArrayList<>();
        try {
            for (Product product : ProductTable.getAllUnApprovedProducts()) {
                unApprovedProducts.add(product.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return unApprovedProducts;
    }

    public ArrayList<String> getAllEditingProductNames() {
        ArrayList<String> editingProducts = new ArrayList<>();
        try {
            for (Product editingProduct : EditingProductTable.getAllEditingProducts()) {
                editingProducts.add(editingProduct.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return editingProducts;
    }


    public ArrayList<String> getAllEditingProductIDs() {
        ArrayList<String> editingProducts = new ArrayList<>();
        try {
            for (Product editingProduct : EditingProductTable.getAllEditingProducts()) {
                editingProducts.add(editingProduct.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return editingProducts;
    }

    public void removeEditingProductById(String editingProductID) {
        try {
            EditingProductTable.removeProductById(editingProductID);
            ProductTable.setProductStatus(editingProductID, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getAllShowingProductNames() {
        ArrayList<String> showingProductNames = new ArrayList<>();
        try {
            ArrayList<Product> showingProducts = convertIDsToProducts(filterProducts());
            sortProducts(showingProducts);
            for (Product showingProduct : showingProducts) {
                showingProductNames.add(showingProduct.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return showingProductNames;
    }

    public ArrayList<String> getAllShowingProductIDs() {
        ArrayList<String> showingProductIDs = new ArrayList<>();
        try {
            ArrayList<Product> showingProducts = convertIDsToProducts(filterProducts());
            sortProducts(showingProducts);
            for (Product showingProduct : showingProducts) {
                showingProductIDs.add(showingProduct.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return showingProductIDs;
    }

    private ArrayList<String> filterProducts() throws SQLException, ClassNotFoundException {
        ArrayList<String> filteredProductIds = new ArrayList<>();
        Control.Filter filter = Control.getFilter();
        if (filter.getFilterCategories().size() != 0) {
            for (String category : filter.getFilterCategories()) {
                for (String productId : filterOnCategory(category)) {
                    if (!filteredProductIds.contains(productId))
                        filteredProductIds.add(productId);
                }
            }
        } else {
            if(!isOffListic) {
                //System.out.println("Hello fuck");
                for (Product product : ProductTable.getAllShowingProducts()) {
                    filteredProductIds.add(product.getID());
                }
            } else {
                for (Product product : ProductTable.getAllShowingProducts()) {
                    if (OffTable.isThereProductInSpecificOff(listicOffID, product.getID()))
                        filteredProductIds.add(product.getID());
                }
            }
        }
        if (filter.getFilterNames().size() != 0) {
            for (String filterName : filter.getFilterNames()) {
                filterOnName(filteredProductIds, filterName);
            }
        }
        return filteredProductIds;
    }

    private ArrayList<Product> convertIDsToProducts(ArrayList<String> productIDs) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        for (String productID : productIDs) {
            products.add(ProductTable.getProductByID(productID));
        }
        return products;
    }

    private void sortProducts(ArrayList<Product> products)
    {
        if(Control.getSort().getSortType() == Sort.SortType.VIEW && Control.getSort().isAscending())
            Collections.sort(products, new Sorting.ViewSortAscending());
        else if(Control.getSort().getSortType() == Sort.SortType.VIEW && !Control.getSort().isAscending())
            Collections.sort(products, new Sorting.ViewSortDescending());
        else if(Control.getSort().getSortType() == Sort.SortType.NAME && Control.getSort().isAscending())
            Collections.sort(products, new Sorting.NameSortAscending());
        else if(Control.getSort().getSortType() == Sort.SortType.NAME && !Control.getSort().isAscending())
            Collections.sort(products, new Sorting.NameSortDescending());
        else if(Control.getSort().getSortType() == Sort.SortType.TIME && Control.getSort().isAscending())
            Collections.sort(products, new Sorting.TimeSortAscending());
        else if(Control.getSort().getSortType() == Sort.SortType.TIME && !Control.getSort().isAscending())
            Collections.sort(products, new Sorting.TimeSortDescending());
        else if(Control.getSort().getSortType() == Sort.SortType.SCORE && Control.getSort().isAscending())
            Collections.sort(products, new Sorting.ScoreSortAscending());
        else if(Control.getSort().getSortType() == Sort.SortType.SCORE && !Control.getSort().isAscending())
            Collections.sort(products, new Sorting.ScoreSortDescending());
    }

    private ArrayList<String> filterOnCategory(String category) throws SQLException, ClassNotFoundException {
        if (!CategoryTable.isThereSubCategories(category) &&
                !ProductTable.isThereProductWithSpecificCategory(category)) {
            return new ArrayList<>();
        }
        ArrayList<String> productIds = new ArrayList<>();
        if(!isOffListic) {
            for (Product product : ProductTable.getProductsWithCategory(category)) {
                if (product.getStatus() != 2)
                    productIds.add(product.getID());
            }
        } else {
            for (Product product : ProductTable.getProductsWithCategory(category)) {
                if (product.getStatus() != 2 && OffTable.isThereProductInSpecificOff(listicOffID, product.getID()))
                    productIds.add(product.getID());
            }
        }

        for (Category subCategory : CategoryTable.getSubCategories(category)) {
            productIds.addAll(filterOnCategory(subCategory.getName()));
        }
        return productIds;
    }

    private void filterOnName(ArrayList<String> filteredProductIds, String filterName) throws SQLException, ClassNotFoundException {
        for (int i = 0; i < filteredProductIds.size(); i++) {
            if (!ProductTable.getProductByID(filteredProductIds.get(i)).getName().contains(filterName)) {
                filteredProductIds.remove(i);
                i--;
            }
        }
    }

    public void addSeenToProduct(String productID) {
        try {
            ProductTable.addSeenToProductWithID(productID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Off getOffByProductID(String productID){
        try {
            return OffTable.getOffByProductID(productID);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Off();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new Off();
        }
    }

    public boolean areComparable(String firstProduct, String secondProduct) {
        if(firstProduct.equals(secondProduct))
            return false;
        try {
            String firstProductCategory = ProductTable.getProductByID(firstProduct).getCategory();
            while (!CategoryTable.getParentCategory(firstProductCategory).equals("All Products"))
                firstProductCategory = CategoryTable.getParentCategory(firstProductCategory);
            String secondProductCategory = ProductTable.getProductByID(firstProduct).getCategory();
            while (!CategoryTable.getParentCategory(secondProductCategory).equals("All Products"))
                secondProductCategory = CategoryTable.getParentCategory(secondProductCategory);
            if(!firstProductCategory.equals(secondProductCategory))
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
}
