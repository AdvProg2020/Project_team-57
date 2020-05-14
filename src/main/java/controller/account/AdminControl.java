package controller.account;


import model.db.*;
import model.existence.Discount;
import model.existence.Product;
import notification.Notification;
import model.existence.Category;

import java.sql.Date;
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
            throwable.printStackTrace();
        } catch (ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }

        return Notification.UNKNOWN_ERROR;
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

    public ArrayList<String> getAllDiscountCodes() {
        ArrayList<String> discountCodes = new ArrayList<>();
        try {
            DiscountTable.updateDiscountCodes();
            for (Discount discountCode : DiscountTable.getAllDiscountCodes()) {
                discountCodes.add(discountCode.getCode());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return discountCodes;
    }


    public ArrayList<String> getAllDiscountIDs() {
        ArrayList<String> discountCodes = new ArrayList<>();
        try {
            for (Discount discountCode : DiscountTable.getAllDiscountCodes()) {
                discountCodes.add(discountCode.getID());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return discountCodes;
    }

    public Discount getDiscountByID(String ID) {
        try {
            return DiscountTable.getDiscountByID(ID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Notification removeDiscountByID(String ID)
    {
        try {
            DiscountTable.removeDiscountCode(ID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.DELETED_DISCOUNT;
    }

    public Notification addDiscount(Discount discount) {
        if(isDiscountComplete(discount) != null)
            return isDiscountComplete(discount);

        String ID = "";

        try {
            do {
                ID = generateDiscountID();
            } while (DiscountTable.isThereDiscountWithID(ID));

            discount.setID(ID);
            DiscountTable.addDiscount(discount);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Notification.ADD_DISCOUNT;
    }

    public Notification isDiscountComplete(Discount discount) {
        Notification notification = null;

        if(discount.getCode() == null)
            notification = Notification.EMPTY_DISCOUNT_CODE;
        else if(discount.getStartDate() == null)
            notification = Notification.EMPTY_DISCOUNT_START_DATE;
        else if(discount.getFinishDate() == null)
            notification = Notification.EMPTY_DISCOUNT_FINISH_DATE;
        else if(discount.getDiscountPercent() == 0)
            notification = Notification.EMPTY_DISCOUNT_PERCENT;
        else if(discount.getMaxDiscount() == 0)
            notification = Notification.EMPTY_MAX_DISCOUNT;
        else if(discount.getMaxRepetition() == 0)
            notification = Notification.EMPTY_DISCOUNT_MAX_REPETITION;
        else if(discount.getCustomersWithRepetition().keySet().size() == 0 ||
                discount.getCustomersWithRepetition() == null)
            notification = Notification.EMPTY_DISCOUNT_CUSTOMERS_LIST;

        return notification;
    }

    private String generateDiscountID() {
        char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};
        StringBuilder ID = new StringBuilder("d");

        for(int i = 0; i < 7; ++i)
            ID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);

        return ID.toString();
    }

    public Notification editCode(String ID, String code) {
        try {
            if (code.length() > 16 || code.length() < 6)
                return Notification.INVALID_DISCOUNT_CODE;
            else if (DiscountTable.getDiscountByID(ID).getCode().equals(code))
                return Notification.SAME_FIELD_ERROR;
            else
                DiscountTable.editCode(ID, code);

                return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Notification editFinishDate(String ID, Date finishDate) {
        try {
            Date currentDate = new Date(new java.util.Date().getTime());

            if (finishDate.compareTo(DiscountTable.getDiscountByID(ID).getStartDate()) != +1)
                return Notification.INVALID_FINISH_DATE_EARLIER_THAN_START_DATE;
            else if (finishDate.compareTo(currentDate) != +1)
                return Notification.INVALID_FINISH_DATE_EARLIER_THAN_CURRENT_DATE;
            else if (DiscountTable.getDiscountByID(ID).getFinishDate().equals(finishDate))
                return Notification.SAME_FIELD_ERROR;
            else
                DiscountTable.editFinishDate(ID, finishDate);

                return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Notification editDiscountPercent(String ID, double discountPercent) {
        try {
            if (discountPercent > 100 || discountPercent <= 0)
                return Notification.INVALID_DISCOUNT_PERCENT;
            else if (DiscountTable.getDiscountByID(ID).getDiscountPercent() == discountPercent)
                return Notification.SAME_FIELD_ERROR;
            else
                DiscountTable.editDiscountPercent(ID, discountPercent);

                return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Notification editMaxDiscount(String ID, double maxDiscount) {
        try {
            if (maxDiscount <= 0)
                return Notification.INVALID_MAX_DISCOUNT;
            else if (DiscountTable.getDiscountByID(ID).getMaxDiscount() == maxDiscount)
                return Notification.SAME_FIELD_ERROR;
            else
                DiscountTable.editMaxDiscount(ID, maxDiscount);

                return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Notification editMaxRepetition(String ID, int maxRepetition) {
        try {
            if (maxRepetition <= 0)
                return Notification.INVALID_MAX_REPETITION;
            else if (DiscountTable.getDiscountByID(ID).getMaxRepetition() == maxRepetition)
                return Notification.SAME_FIELD_ERROR;
            else
                DiscountTable.editMaxRepetition(ID, maxRepetition);

                return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getAllUnApprovedOffNames(){
        try {
            return OffTable.getAllUnApprovedOffNames();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getAllUnApprovedOffIDs(){
        try {
            return OffTable.getAllUnApprovedOffIDs();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Notification modifyOffApprove(String offID, boolean flag){
        try {
            if (flag){
                OffTable.acceptOffRequest(offID);
                return Notification.ACCEPT_OFF_REQUEST;
            } else{
                OffTable.declineOffRequest(offID);
                return Notification.DECLINE_REQUEST;
            }
        } catch (SQLException e) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }
}
