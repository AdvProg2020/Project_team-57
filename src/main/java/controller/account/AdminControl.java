package controller.account;


import model.db.EditingProductTable;
import model.db.ProductTable;
import model.db.VendorTable;
import model.existence.Product;
import notification.Notification;
import model.db.CategoryTable;
import model.existence.Category;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminControl extends AccountControl{
    private static AdminControl adminControl = null;

    public static AdminControl getController() {
        if (adminControl == null)
            adminControl = new AdminControl();

        return adminControl;
    }

    public Notification approveProductByID(String id){
        try {
            ProductTable.setProductStatus(id, 1);
            ProductTable.setProductApprovalDate(id);
            return Notification.ACCEPT_ADDING_PRODUCT;
        } catch (SQLException throwable) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException throwable) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification acceptEditingProductByID(String editingProductID) {
        try {
            Product editingProduct = EditingProductTable.getEditingProductWithID(editingProductID);
            EditingProductTable.removeProductById(editingProductID);
            ProductTable.removeProductByID(editingProduct.getID());
            if(editingProduct.isCountable())
                VendorTable.addCountableProduct(editingProduct, editingProduct.getSellerUserName());
            else
                VendorTable.addUnCountableProduct(editingProduct, editingProduct.getSellerUserName());
            ProductTable.setProductStatus(editingProduct.getID(), 1);
            return Notification.ACCEPT_EDITING_PRODUCT;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<String> getAllCategoryNames() {
        ArrayList<String> categories = new ArrayList<>();
        for(Category category : CategoryTable.getAllCategories())
        {
            categories.add(categories.getName());
        }
        return categories;
    }

    public Notification addCategory(Category category)
    {
        if(!CategoryTable.isThereCategoryWithName(category.getName()))
        {
            if(!category.getParentName() != null && !CategoryTable.isThereCategoryWithName(category.getParentName()))
                return Notification.PARENT_CATEGORY_NOT_FOUND;
            if(category.getParentCategory() == null)
                category.setParentCategory("All Products");
            CategoryTable.addCategory(category);
            return Notification.CATEGORY_ADDED;
        }
        return Notification.DUPLICATE_CATEGORY_NAME;
    }

    public Notification removeCategory(Category category)
    {
        if(CategoryTable.isThereCategoryWithName(category.getName()))
        {
            ArrayList<Category> subCategories = CategoryTable.getAllSubCategories(category.getName());
            for (Category subCategory : subCategories) {
                CategoryTable.setCategoryParentName(subCategory.getName(), category.getParentName());
            }
            CategoryTable.removeCategoryWithName(category.getName());
            ArrayList<Product> products = ProductTable.getProductsWithCategory(category.getName());
            for (Product product : products) {
                ProductTable.removeProductByID(product.getID());
                if(!EditingProductTable.isIDFree(product.getID()))
                    EditingProductTable.removeProductById(product.getID());
            }
            return Notification.CATEGORY_DELETED;
        }
        return Notification.CATEGORY_NOT_FOUND;
    }
}
