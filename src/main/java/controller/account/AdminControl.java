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
        try {
            ArrayList<String> categories = new ArrayList<>();
            for (Category category : CategoryTable.getAllCategories()) {
                categories.add(category.getName());
            }

            categories.remove("All Products");
            return categories;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public Notification addCategory(Category category)
    {
        try {
            if (!CategoryTable.isThereCategoryWithName(category.getName())) {
                if(category.getName().length() > 5 && category.getName().length() < 17) {
                    if(category.getFeatures().length() < 101) {
                        if (category.getParentCategory() != null && !CategoryTable.isThereCategoryWithName(category.getParentCategory()))
                            return Notification.PARENT_CATEGORY_NOT_FOUND;
                        if (category.getParentCategory() == null)
                            category.setParentCategory("All Products");
                        CategoryTable.addCategory(category);
                        return Notification.CATEGORY_ADDED;
                    }
                    return Notification.INVALID_FEATURES;
                }
                return Notification.INVALID_CATEGORY_NAME;
            }
            return Notification.DUPLICATE_CATEGORY_NAME;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Notification.UNKNOWN_ERROR;
    }

    public Notification removeCategory(Category category)
    {
        try {
            if (CategoryTable.isThereCategoryWithName(category.getName())) {
                ArrayList<Category> subCategories = CategoryTable.getSubCategories(category.getName());
                for (Category subCategory : subCategories) {
                    CategoryTable.setCategoryParentName(subCategory.getName(), category.getParentCategory());
                }
                CategoryTable.removeCategoryWithName(category.getName());
                ArrayList<Product> products = ProductTable.getProductsWithCategory(category.getName());
                for (Product product : products) {
                    ProductTable.removeProductByID(product.getID());
                    if (!EditingProductTable.isIDFree(product.getID()))
                        EditingProductTable.removeProductById(product.getID());
                }
                return Notification.CATEGORY_DELETED;
            }
            return Notification.CATEGORY_NOT_FOUND;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Notification.UNKNOWN_ERROR;
    }

    public Notification editCategoryName(Category oldCategory, Category newCategory)
    {
        try {
            if(!oldCategory.getName().equals(newCategory))
            {
                if(!CategoryTable.isThereCategoryWithName(newCategory.getName()))
                {
                    if(newCategory.getName().length() < 17 && newCategory.getName().length() > 5) {
                        for (Product product : ProductTable.getProductsWithCategory(oldCategory.getName())) {
                            ProductTable.changeProductCategoryByID(product.getID(), newCategory.getName());
                        }
                        for (Category subcategory : CategoryTable.getSubCategories(oldCategory.getName())) {
                            CategoryTable.setCategoryParentName(subcategory.getName(), newCategory.getName());
                        }
                        CategoryTable.changeCategoryName(oldCategory.getName(), newCategory.getName());
                        return Notification.CATEGORY_MODIFIED;
                    }
                    return Notification.INVALID_CATEGORY_NAME;
                }
                return Notification.DUPLICATE_CATEGORY_NAME;
            }
            return Notification.SAME_FIELD_ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification editCategoryFeatures(Category oldCategory, Category newCategory)
    {
        try {
            if (!oldCategory.getFeatures().equals(newCategory.getFeatures())) {
                if(newCategory.getFeatures().length() < 101) {
                    CategoryTable.changeCategoryFeatures(oldCategory.getName(), newCategory.getFeatures());
                    return Notification.CATEGORY_MODIFIED;
                }
                return Notification.INVALID_FEATURES;
            }
            return Notification.SAME_FIELD_ERROR;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Notification.UNKNOWN_ERROR;
    }

    public Notification editCategoryParentName(Category oldCategory, Category newCategory)
    {
        if(!oldCategory.getParentCategory().equals(newCategory.getParentCategory()))
        {
            try {
                if(CategoryTable.isThereCategoryWithName(newCategory.getParentCategory())) {
                    CategoryTable.setCategoryParentName(oldCategory.getName(), newCategory.getParentCategory());
                    return Notification.CATEGORY_MODIFIED;
                }
                    return Notification.PARENT_CATEGORY_NOT_FOUND;

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return Notification.SAME_FIELD_ERROR;
    }

    public Category getCategoryByName(String categoryName) {
        try {
            return CategoryTable.getCategoryWithName(categoryName);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
