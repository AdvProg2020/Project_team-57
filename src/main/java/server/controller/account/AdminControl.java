package server.controller.account;


import server.controller.product.ProductControl;
import server.model.db.*;
import server.model.existence.*;
import notification.Notification;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminControl extends AccountControl{
    private static AdminControl adminControl = null;

    public static AdminControl getController() {
        if (adminControl == null)
            adminControl = new AdminControl();

        return adminControl;
    }

    private static final Object addDelCategoryLock = new Object();
    public static final Object offLock1 = new Object();

    private HashMap<Discount, ArrayList<String>> discountsAddedUsers;

    public void createDiscountAddedUsers() {
        this.discountsAddedUsers = new HashMap<>();
    }

    public void addDiscountToHashMap(Discount discount) {
        if(discount.getCustomersWithRepetition().isEmpty())
            discountsAddedUsers.put(discount, new ArrayList<>());
        else {
            ArrayList<String> users = new ArrayList<>();
            users.addAll(discount.getCustomersWithRepetition().keySet());
            discountsAddedUsers.put(discount, users);
        }
    }

    public void removeDiscountFromHashMap(Discount discount) {
        if(discount != null && discountsAddedUsers != null)
            discountsAddedUsers.remove(discount);
    }

    public HashMap<Discount, ArrayList<String>> getDiscountsAddedUsers() {
        return discountsAddedUsers;
    }

    public void addUserToDiscountAddedUsers(Discount discount, String userName) {
        if(discountsAddedUsers.containsKey(discount) && !discountsAddedUsers.get(discount).contains(userName)) {
            discountsAddedUsers.get(discount).add(userName);
        }
    }

    public void removeUserFromDiscountAddedUsers(Discount discount, String userName) {
        if(discountsAddedUsers.containsKey(discount) && discountsAddedUsers.get(discount).contains(userName)) {
            discountsAddedUsers.get(discount).remove(userName);
        }
    }

    public boolean isUserAddedInDiscount(Discount discount, String userName) {
        if(discountsAddedUsers.containsKey(discount))
            return discountsAddedUsers.get(discount).contains(userName);
        return false;
    }

    public Notification addCategory(Category category)
    {
        synchronized (addDelCategoryLock) {
            try {
                CategoryTable categoryTable = CategoryTable.getInstance();
                if (!categoryTable.isThereCategoryWithName(category.getName())) {
                    if(category.getName().length() > 5 && category.getName().length() < 17) {
                        if(category.getFeatures().length() < 101) {
                            if (category.getParentCategory() != null && !categoryTable.isThereCategoryWithName(category.getParentCategory()))
                                return Notification.PARENT_CATEGORY_NOT_FOUND;
                            if (category.getParentCategory() == null)
                                category.setParentCategory("All Products");
                            categoryTable.addCategory(category);
                            return Notification.CATEGORY_ADDED;
                        }
                        return Notification.INVALID_FEATURES;
                    }
                    return Notification.INVALID_CATEGORY_NAME;
                }
                return Notification.DUPLICATE_CATEGORY_NAME;
            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }

            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification removeCategory(Category category) {
        synchronized (addDelCategoryLock) {
            try {
                if (category == null)
                    return Notification.NOT_SELECTED_CATEGORY;
                if(category.getName().equals("All Products"))
                    return Notification.CANT_DELETE_CATEGORY;
                CategoryTable categoryTable = CategoryTable.getInstance();
                if (categoryTable.isThereCategoryWithName(category.getName())) {
                    ArrayList<Category> subCategories = categoryTable.getSubCategories(category.getName());
                    for (Category subCategory : subCategories) {
                        categoryTable.setCategoryParentName(subCategory.getName(), category.getParentCategory());
                    }
                    categoryTable.removeCategoryWithName(category.getName());
                    ProductTable productTable = ProductTable.getInstance();
                    ArrayList<Product> products = productTable.getProductsWithCategory(category.getName());
                    for (Product product : products) {
                        productTable.removeProductByID(product.getID());
                        EditingProductTable editingProductTable = EditingProductTable.getInstance();
                        if (!editingProductTable.isIDFree(product.getID()))
                            editingProductTable.removeProductById(product.getID());
                    }
                    return Notification.CATEGORY_DELETED;
                }
                return Notification.CATEGORY_NOT_FOUND;
            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }

            return Notification.UNKNOWN_ERROR;
        }

    }

    public Notification editCategory(Category oldCategory, Category newCategory, String fieldName) {
        Notification notification = null;

        switch (fieldName) {
            case "name" :
                notification = editCategoryName(oldCategory, newCategory);
                break;
            case "parent name" :
                notification = editCategoryParentName(oldCategory, newCategory);
                break;
            case "features" :
                notification = editCategoryFeatures(oldCategory, newCategory);
                break;
        }

        return notification;
    }

    public synchronized Notification editCategoryName(Category oldCategory, Category newCategory) {
        try {
            if(!oldCategory.getName().equals(newCategory.getName()))
            {
                if(!CategoryTable.getInstance().isThereCategoryWithName(newCategory.getName()))
                {
                    if(newCategory.getName().length() < 17 && newCategory.getName().length() > 5) {
                        ProductTable productTable = ProductTable.getInstance();
                        for (Product product : productTable.getProductsWithCategory(oldCategory.getName())) {
                            productTable.changeProductCategoryByID(product.getID(), newCategory.getName());
                        }
                        CategoryTable categoryTable = CategoryTable.getInstance();
                        for (Category subcategory : categoryTable.getSubCategories(oldCategory.getName())) {
                            categoryTable.setCategoryParentName(subcategory.getName(), newCategory.getName());
                        }
                        categoryTable.changeCategoryName(oldCategory.getName(), newCategory.getName());
                        return Notification.CATEGORY_MODIFIED;
                    }
                    return Notification.INVALID_CATEGORY_NAME;
                }
                return Notification.DUPLICATE_CATEGORY_NAME;
            }
            return Notification.CATEGORY_MODIFIED;
            //return Notification.SAME_CATEGORY_FIELD_ERROR;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification editCategoryFeatures(Category oldCategory, Category newCategory)
    {
        synchronized (addDelCategoryLock) {
            try {
                if (!oldCategory.getFeatures().equals(newCategory.getFeatures())) {
                    if(newCategory.getFeatures().length() < 101) {
                        CategoryTable.getInstance().changeCategoryFeatures(oldCategory.getName(), newCategory.getFeatures());
                        return Notification.CATEGORY_MODIFIED;
                    }
                    return Notification.INVALID_FEATURES;
                }
                return Notification.CATEGORY_MODIFIED;
                //return Notification.SAME_CATEGORY_FIELD_ERROR;
            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }

            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification editCategoryParentName(Category oldCategory, Category newCategory)
    {
        if(!oldCategory.getParentCategory().equals(newCategory.getParentCategory()))
        {
            try {
                CategoryTable categoryTable = CategoryTable.getInstance();
                if(categoryTable.isThereCategoryWithName(newCategory.getParentCategory())) {
                    categoryTable.setCategoryParentName(oldCategory.getName(), newCategory.getParentCategory());
                    return Notification.CATEGORY_MODIFIED;
                }
                    return Notification.PARENT_CATEGORY_NOT_FOUND;

            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }
        }
        return Notification.CATEGORY_MODIFIED;
        //return Notification.SAME_FIELD_ERROR;
    }

    public ArrayList<Discount> getAllDiscounts() {
        try {
            DiscountTable discountTable = DiscountTable.getInstance();
            discountTable.removeOutDatedDiscounts();
            return discountTable.getAllDiscountCodes();
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification removeDiscountByID(String ID)
    {
        try {
            DiscountTable.getInstance().removeDiscountCode(ID);
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.DELETED_DISCOUNT;
    }

    public Notification addDiscount(Discount discount) {
        Notification notification = null;

        synchronized (discountPurchaseLock) {
            try {
                if(discount.getID() != null && !discount.getID().isEmpty()) {
                    if((notification = setNewDiscountsEmptyFields(discount)) == null) {
                        DiscountTable.getInstance().removeDiscountCode(discount.getID());
                        notification = Notification.EDIT_DISCOUNT;
                    } else
                        return notification;
                } else {
                    if((notification = isDiscountComplete(discount)) == null) {
                        notification = Notification.ADD_DISCOUNT;
                    } else {
                        return notification;
                    }
                }

                String ID = "";

                DiscountTable discountTable = DiscountTable.getInstance();
                do {
                    ID = generateDiscountID();
                } while (discountTable.isThereDiscountWithID(ID));

                discount.setID(ID);
                discountTable.addDiscount(discount);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("Error In #addDiscount");
            }
            return notification;
        }

    }

    public Notification setNewDiscountsEmptyFields(Discount newDiscount) throws SQLException, ClassNotFoundException {
        Discount oldDiscount = DiscountTable.getInstance().getDiscountByID(newDiscount.getID());

        if(newDiscount.getCode() == null || newDiscount.getCode().isEmpty())
            newDiscount.setCode(oldDiscount.getCode());
        if(newDiscount.getDiscountPercent() == 0)
            newDiscount.setDiscountPercent(oldDiscount.getDiscountPercent());
        if(newDiscount.getMaxRepetition() == 0)
            newDiscount.setMaxRepetition(oldDiscount.getMaxRepetition());
        if(newDiscount.getMaxDiscount() == 0)
            newDiscount.setMaxDiscount(oldDiscount.getMaxDiscount());
        if(newDiscount.getStartDate() == null)
            System.err.println("Shit. Empty StartDate In Setting New Discount's Empty Fields");
        if(newDiscount.getFinishDate() == null)
            newDiscount.setFinishDate(oldDiscount.getFinishDate());

        if(newDiscount.getFinishDate().getTime() <= newDiscount.getStartDate().getTime())
            return Notification.INVALID_FINISH_DATE_EARLIER_THAN_START_DATE;
        else if(newDiscount.getFinishDate().getTime() <= System.currentTimeMillis())
            return Notification.INVALID_FINISH_DATE_EARLIER_THAN_CURRENT_DATE;

        return null;
    }

    public Notification isDiscountComplete(Discount discount) {
        Notification notification = null;

        if(discount.getCode() == null || discount.getCode().isEmpty())
            notification = Notification.EMPTY_DISCOUNT_CODE;
        else if(discount.getCode().length() > 16)
            notification = Notification.INVALID_DISCOUNT_CODE_LENGTH;
        else if(discount.getStartDate() == null)
            discount.setStartDate(new Date(System.currentTimeMillis()));
        else if(discount.getFinishDate() == null)
            notification = Notification.EMPTY_DISCOUNT_FINISH_DATE;
        else if(discount.getStartDate().getTime() < System.currentTimeMillis())
            notification = Notification.INVALID_START_DATE;
        else if(discount.getStartDate().getTime() >= discount.getFinishDate().getTime())
            notification = Notification.INVALID_FINISH_DATE_FOR_START_DATE;
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

    public ArrayList<Off> getAllUnApprovedOffs() {
        synchronized (offLock1) {
            try {
                OffTable offTable = OffTable.getInstance();
                offTable.removeOutDatedOffs();
                return offTable.getAllUnApprovedOffs();
            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }
            return new ArrayList<>();
        }
    }

    public Notification modifyOffApprove(String offID, boolean flag){
        synchronized (offLock1) {
            try {
                OffTable offTable = OffTable.getInstance();
                if (flag){
                    offTable.approveOffByID(offID);
                    return Notification.ACCEPT_OFF_REQUEST;
                } else{
                    offTable.removeOffByID(offID);
                    return Notification.DECLINE_REQUEST;
                }
            } catch (SQLException | ClassNotFoundException e) {
                return Notification.UNKNOWN_ERROR;
            }
        }
    }

    public Notification modifyOffEditingApprove(String offID, boolean isAccepted)
    {
        try {
            OffTable offTable = OffTable.getInstance();
            Off editingOff = offTable.getSpecificEditingOff(offID);
            if(isAccepted)
            {
                offTable.removeOffByID(editingOff.getOffID());
                editingOff.setStatus(1);
                offTable.addOff(editingOff);
                offTable.removeEditingOff(editingOff.getOffID());
                acceptEditingOffImages(offID);
                return Notification.OFF_EDITING_ACCEPTED;
            } else {
                offTable.removeEditingOff(offID);
                offTable.changeOffStatus(offID, 1);
                declineEditingOffImages(offID);
                return Notification.OFF_EDITING_DECLINED;
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    private void declineEditingOffImages(String offID) {
        synchronized (offLock1) {
            if(ProductControl.getController().doesEditingOffHaveImage(offID)) {
                OffTable.getInstance().removeEditingOffImage(offID);
            }
        }
    }

    private void acceptEditingOffImages(String offID) {
        synchronized (offLock1) {
            try {
                OffTable offTable = OffTable.getInstance();
                if(ProductControl.getController().doesOffHaveImage(offID))
                    offTable.removeOffImage(offID);
                if(ProductControl.getController().doesEditingOffHaveImage(offID)) {
                    offTable.setOffImage(offID, ProductControl.getController().getEditingOffImageFileByID(offID));
                    declineEditingOffImages(offID);
                }
            } catch (IOException e) {
                //:)
            }
        }
    }

    public ArrayList<Comment> getAllUnApprovedComments() {
        try {
            return ProductTable.getInstance().getAllUnApprovedComments();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error In #getAllUnApprovedComments");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Notification modifyCommentApproval(String commentID, boolean flag) {
        try {
            if (flag){
                ProductTable.getInstance().modifyCommentApproval(commentID, 1);
                return Notification.ACCEPTING_COMMENT;
            }
            ProductTable.getInstance().modifyCommentApproval(commentID, 3);
            return Notification.DECLINE_COMMENT;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public void getGiftDiscount() {
        try {
            Date currentDate = new Date(new java.util.Date().getTime());
            Date finishDate = new Date((new java.util.Date().getTime() + (long) 6.048e+8));
            Discount discount = new Discount(generateDiscountID(), "Gift-" + currentDate.toString(), currentDate, finishDate, 10, 2000, 1);
            int customerNum;
            AccountTable accountTable = AccountTable.getInstance();
            DiscountTable discountTable = DiscountTable.getInstance();
            if (accountTable.getAllAccounts().size() > 5) {
                for (int i = 0; i < 5; i++) {
                    customerNum = ((int) (Math.random() * 100000000)) % accountTable.getAllAccounts().size();
                    discountTable.addGiftDiscount(discount, accountTable.getAllAccounts().get(customerNum).getUsername());
                }
            } else {
                customerNum = ((int) (Math.random() * 100000000)) % accountTable.getAllAccounts().size();
                discountTable.addGiftDiscount(discount, accountTable.getAllAccounts().get(customerNum).getUsername());
            }
            accountTable.updatePeriod("Ya Zahra");
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
    }

    public Notification addAddedDiscount(Discount discount, ArrayList<String> discountsAddedUsers) {
        discount.setCustomersWithRepetition(new HashMap<>());

        if(discount.getID() == null || discount.getID().isEmpty()) {
            for (String addedUser : discountsAddedUsers) {
                discount.addCustomerWithRepetition(addedUser, 0);
            }
        } else {
            for (String addedUser : discountsAddedUsers) {
                if(!discount.isCustomerInDiscount(addedUser)) {
                    discount.addCustomerWithRepetition(addedUser, 0);
                }
            }
        }

        return addDiscount(discount);
    }

    public ArrayList<Product> getAllNotApprovedProducts() {
        try {
            OffTable.getInstance().removeOutDatedOffs();
            return ProductTable.getInstance().getAllNotApprovedProducts();
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification modifyEditingProductApprove(String productID, boolean approved) {
        try {
            EditingProductTable editingProductTable = EditingProductTable.getInstance();

            if(approved) {
                editingProductTable.transferEditingImages(productID);
                return acceptEditingProductByID(productID);
            }
            else {
                editingProductTable.removeAllEditingProductImages(productID);
                return ProductControl.getController().removeEditingProductById(productID);
            }
        } catch (IOException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification modifyProductApprove(String productID, boolean approved) {
        if(approved)
            return approveProductByID(productID);
        else
            return ProductControl.getController().removeProductById(productID);
    }

    private Notification approveProductByID(String id){
        try {
            ProductTable productTable = ProductTable.getInstance();
            productTable.setProductStatus(id, 1);
            productTable.setProductApprovalDate(id);
            return Notification.ACCEPT_ADDING_PRODUCT;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }

        return Notification.UNKNOWN_ERROR;
    }

    private Notification acceptEditingProductByID(String editingProductID) {
        try {
            EditingProductTable editingProductTable = EditingProductTable.getInstance();
            ProductTable productTable = ProductTable.getInstance();
            VendorTable vendorTable = VendorTable.getInstance();

            Product editingProduct = editingProductTable.getEditingProductWithID(editingProductID);
            editingProduct.setApprovalDate(productTable.getProductByID(editingProductID).getApprovalDate());
            editingProduct.setSeen(productTable.getProductByID(editingProductID).getSeen());
            editingProductTable.removeProductById(editingProductID);
            productTable.removeProductByID(editingProduct.getID());
            if(editingProduct.isCountable())
                vendorTable.addCountableProduct(editingProduct, editingProduct.getSellerUserName());
            else
                vendorTable.addUnCountableProduct(editingProduct, editingProduct.getSellerUserName());
            productTable.setProductStatus(editingProduct.getID(), 1);
            return Notification.ACCEPT_EDITING_PRODUCT;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Category> getAllCategories() {
        try {
            return CategoryTable.getInstance().getAllCategories();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
