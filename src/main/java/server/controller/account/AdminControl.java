package server.controller.account;


import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.jmx.remote.security.NotificationAccessController;
import server.controller.product.ProductControl;
import server.model.db.*;
import server.model.existence.*;
import server.model.existence.Account.*;
import notification.Notification;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import static server.controller.Lock.*;


public class AdminControl extends AccountControl{
    private static AdminControl adminControl = null;

    public static AdminControl getController() {
        if (adminControl == null)
            adminControl = new AdminControl();

        return adminControl;
    }

    public Notification addCategory(Category category)
    {
        try {
            synchronized (CATEGORY_LOCK) {
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
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }

        return Notification.UNKNOWN_ERROR;
    }

    public Notification removeCategory(Category category) {
        try {
            synchronized (CATEGORY_LOCK) {
                if (category == null)
                    return Notification.NOT_SELECTED_CATEGORY;
                if(category.getName().equals("All Products"))
                    return Notification.CANT_DELETE_CATEGORY;
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
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }

        return Notification.UNKNOWN_ERROR;
    }

    public Notification editCategory(Category oldCategory, Category newCategory, String fieldName) {
        synchronized (CATEGORY_LOCK) {
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
    }

    public Notification editCategoryName(Category oldCategory, Category newCategory)
    {
        try {
            if(!oldCategory.getName().equals(newCategory.getName()))
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
            return Notification.CATEGORY_MODIFIED;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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
            return Notification.CATEGORY_MODIFIED;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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
                //:)
            } catch (ClassNotFoundException e) {
                //:)
            }
        }
        return Notification.CATEGORY_MODIFIED;
    }

    public ArrayList<Discount> getAllDiscounts() {
        try {
            synchronized (DISCOUNT_LOCK) {
                DiscountTable.removeOutDatedDiscounts();
                return DiscountTable.getAllDiscountCodes();
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification removeDiscountByID(String ID) {
        try {
            synchronized (DISCOUNT_LOCK) {
                DiscountTable.removeDiscountCode(ID);
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.DELETED_DISCOUNT;
    }

    public Notification addDiscount(Discount discount) {
        Notification notification = null;

        try {
            synchronized (DISCOUNT_LOCK) {
                if(discount.getID() != null && !discount.getID().isEmpty()) {
                    if((notification = setNewDiscountsEmptyFields(discount)) == null) {
                        DiscountTable.removeDiscountCode(discount.getID());
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

                String ID = "d" + generateRandomNumber(7, s -> {
                    try {
                        return DiscountTable.isThereDiscountWithID(s);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                });
                discount.setID(ID);
                DiscountTable.addDiscount(discount);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error In #addDiscount");
        }
        return notification;
    }

    public Notification setNewDiscountsEmptyFields(Discount newDiscount) throws SQLException, ClassNotFoundException {
        Discount oldDiscount = DiscountTable.getDiscountByID(newDiscount.getID());

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

    public ArrayList<Off> getAllUnApprovedOffs() {
        try {
            synchronized (OFF_LOCK) {
                OffTable.removeOutDatedOffs();
                return OffTable.getAllUnApprovedOffs();
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification modifyOffApprove(String offID, boolean flag) {
        try {
            synchronized (OFF_LOCK) {
                if (flag) {
                    OffTable.approveOffByID(offID);
                    return Notification.ACCEPT_OFF_REQUEST;
                } else {
                    OffTable.removeOffByID(offID);
                    return Notification.DECLINE_REQUEST;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification modifyOffEditingApprove(String offID, boolean isAccepted) {
        try {
            Off editingOff = OffTable.getSpecificEditingOff(offID);
            if(isAccepted) {
                synchronized (OFF_LOCK) {
                    OffTable.removeOffByID(editingOff.getOffID());
                    editingOff.setStatus(1);
                    OffTable.addOff(editingOff);
                    OffTable.removeEditingOff(editingOff.getOffID());
                }
                acceptEditingOffImages(offID);
                return Notification.OFF_EDITING_ACCEPTED;
            } else {
                synchronized (OFF_LOCK) {
                    OffTable.removeEditingOff(offID);
                    OffTable.changeOffStatus(offID, 1);
                }

                synchronized (EDITING_OFF_IMAGE_LOCK) {
                    declineEditingOffImages(offID);
                }

                return Notification.OFF_EDITING_DECLINED;
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    private void declineEditingOffImages(String offID) {
        if(ProductControl.getController().doesEditingOffHaveImage(offID)) {
            OffTable.removeEditingOffImage(offID);
        }
    }

    private void acceptEditingOffImages(String offID) {
        try {
            synchronized (OFF_IMAGE_LOCK) {
                if(ProductControl.getController().doesOffHaveImage(offID))
                    OffTable.removeOffImage(offID);
            }

            synchronized (EDITING_OFF_IMAGE_LOCK) {
                if(ProductControl.getController().doesEditingOffHaveImage(offID)) {
                    synchronized (OFF_IMAGE_LOCK) {
                        OffTable.setOffImage(offID, ProductControl.getController().getEditingOffImageFileByID(offID));
                    }
                    declineEditingOffImages(offID);
                }
            }

        } catch (IOException e) {
            //:)
        }

    }

    public ArrayList<Comment> getAllUnApprovedComments() {
        try {
            synchronized (COMMENT_SCORE_LOCK) {
                return ProductTable.getAllUnApprovedComments();
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error In #getAllUnApprovedComments");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Notification modifyCommentApproval(String commentID, boolean flag){
        try {
            synchronized (COMMENT_SCORE_LOCK) {
                if (flag) {
                    ProductTable.modifyCommentApproval(commentID, 1);
                    return Notification.ACCEPTING_COMMENT;
                }
                ProductTable.modifyCommentApproval(commentID, 3);
                return Notification.DECLINE_COMMENT;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public void getGiftDiscount() {
        try {
            Date currentDate = new Date(new java.util.Date().getTime());
            Date finishDate = new Date((new java.util.Date().getTime() + (long) 6.048e+8));
            String ID = "d" + generateRandomNumber(7, s -> {
                try {
                    return DiscountTable.isThereDiscountWithID(s);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            });
            Discount discount = new Discount(ID, "Gift-" + currentDate.toString(), currentDate, finishDate, 10, 2000, 1);
            int customerNum;
            if (AccountTable.getAllAccounts().size() > 5) {
                for (int i = 0; i < 5; i++) {
                    customerNum = ((int) (Math.random() * 100000000)) % AccountTable.getAllAccounts().size();
                    DiscountTable.addGiftDiscount(discount, AccountTable.getAllAccounts().get(customerNum).getUsername());
                }
            } else {
                customerNum = ((int) (Math.random() * 100000000)) % AccountTable.getAllAccounts().size();
                DiscountTable.addGiftDiscount(discount, AccountTable.getAllAccounts().get(customerNum).getUsername());
            }
            AccountTable.updatePeriod("Ya Zahra");
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
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
            OffTable.removeOutDatedOffs();
            return ProductTable.getAllNotApprovedProducts();
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    public Notification modifyEditingProductApprove(String productID, boolean approved) {
        try {
            if(ProductControl.getController().doesProductHaveFile(productID)) {
                synchronized (EDITING_PRODUCT_FILE_LOCK) {
                    if(approved) {
                        ProductTable.removeProductFileByID(productID);
                        EditingProductTable.transferEditingFiles(productID);
                    }
                    EditingProductTable.removeEditingProductFile(productID);
                }
            }

            if(approved) {
                synchronized (EDITING_PRODUCT_IMAGE_LOCK) {
                    EditingProductTable.transferEditingImages(productID);
                }
                return acceptEditingProductByID(productID);
            }
            else {
                synchronized (EDITING_PRODUCT_IMAGE_LOCK) {
                    EditingProductTable.removeAllEditingProductImages(productID);
                }
                return ProductControl.getController().removeEditingProductById(productID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification modifyProductApprove(String productID, boolean approved) {
        synchronized (ADMIN_MODIFY_PRODUCT_LOCK) {
            if(approved)
                return approveProductByID(productID);
            else
                return ProductControl.getController().removeProductById(productID);
        }
    }

    private Notification approveProductByID(String id){
        try {
            ProductTable.setProductStatus(id, 1);
            ProductTable.setProductApprovalDate(id);
            return Notification.ACCEPT_ADDING_PRODUCT;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }

        return Notification.UNKNOWN_ERROR;
    }

    private Notification acceptEditingProductByID(String editingProductID) {
        try {
            synchronized (ADMIN_MODIFY_EDIT_PRODUCT_LOCK) {
                Product editingProduct = EditingProductTable.getEditingProductWithID(editingProductID);
                editingProduct.setApprovalDate(ProductTable.getProductByID(editingProductID).getApprovalDate());
                editingProduct.setSeen(ProductTable.getProductByID(editingProductID).getSeen());
                EditingProductTable.removeProductById(editingProductID);
                ProductTable.removeProductByID(editingProduct.getID());
                if(editingProduct.isCountable())
                    VendorTable.addCountableProduct(editingProduct, editingProduct.getSellerUserName());
                else
                    VendorTable.addUnCountableProduct(editingProduct, editingProduct.getSellerUserName());
                ProductTable.setProductStatus(editingProduct.getID(), 1);
                return Notification.ACCEPT_EDITING_PRODUCT;
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Category> getAllCategories() {
        synchronized (CATEGORY_LOCK) {
            try {
                return CategoryTable.getAllCategories();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public Notification setMarketWage(Double wage) {
        try {
            synchronized (WAGE_LOCK) {
                AccountTable.setMarketWage(wage);
                return Notification.CHANGED_SUCCESSFUL;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public Notification setMinimumWallet(Double minimumWallet) {
        try {
            synchronized (MINIMUM_WALLET_LOCK) {
                AccountTable.setMinimumWallet(minimumWallet);
                return Notification.CHANGED_SUCCESSFUL;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public List<Log> getAllLogs() {
        try {
            synchronized (LOG_LOCK) {
                return LogTable.getAllCustomerLogs().stream().sorted((log1, log2) -> log1.getDate().compareTo(log2.getDate())).collect(Collectors.toList());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void modifyLogDeliveryStatus(String logID, int status) {
        try {
            synchronized (LOG_LOCK) {
                LogTable.setLogDeliveryStatus(logID, status);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Notification addSupporter(Supporter supporter) {
        if (supporter.getUsername().length() < 6 || supporter.getUsername().length() > 16)
            return Notification.ERROR_USERNAME_LENGTH;
        if (!isUsernameValid(supporter.getUsername()))
            return Notification.ERROR_USERNAME_FORMAT;
        if (supporter.getPassword().length() < 8 || supporter.getPassword().length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!this.isPasswordValid(supporter.getPassword()))
            return Notification.ERROR_PASSWORD_FORMAT;
        if (supporter.getFirstName().length() > 25)
            return Notification.ERROR_FIRST_NAME_LENGTH;
        if (supporter.getLastName().length() > 25)
            return Notification.ERROR_LAST_NAME_LENGTH;
        try {
            if (AccountTable.isUsernameFreeForSupporter(supporter.getUsername())) {
                AccountTable.addSupporter(supporter);
                return Notification.REGISTER_SUCCESSFUL;
            } else
                return Notification.ERROR_FULL_USERNAME;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }


    public Notification deleteSupporter(String supporterUsername) {
        try {
            AccountTable.deleteSupporter(supporterUsername);
            return Notification.DELETE_USER;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return Notification.UNKNOWN_ERROR;
        }
    }
}
