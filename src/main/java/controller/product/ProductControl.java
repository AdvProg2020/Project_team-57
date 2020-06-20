package controller.product;

import controller.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import model.db.*;
import model.existence.Category;
import model.existence.Comment;
import model.existence.Off;
import model.existence.Product;
import notification.Notification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class ProductControl extends Control {
    private static ProductControl productControl = null;
    private boolean isOffListic;
    private String listicOffID;
    private String currentProduct;
    private Product[] comparingProducts = null;


    public String getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(String currentProduct) {
        this.currentProduct = currentProduct;
    }

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
            if (ProductTable.getProductByID(ID).getStatus() == 2)
                return Notification.PRODUCT_NOT_AVAILABLE;

//            if (checkFieldEquality(fieldName, newField, ID))
//                return Notification.SAME_FIELD_ERROR;
            if(newField != null && !newField.isEmpty()) {
                if (fieldName.equals("Category") && !CategoryTable.isThereCategoryWithName(newField))
                    return Notification.INVALID_CATEGORY_NAME;

                if (ProductTable.getProductByID(ID).getStatus() == 1)
                    ProductTable.setProductStatus(ID, 3);

                if (EditingProductTable.isIDFree(ID))
                    EditingProductTable.addProduct(ProductTable.getProductByID(ID));

                editSpecificField(fieldName, newField, ID);
            }

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
            if (fieldName.equals("ProductName") || fieldName.equals("Brand") ||
                    fieldName.equals("Category") || fieldName.equals("Description")) {
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

    public ArrayList<Product> getAllShowingProducts() {
        try {
            ArrayList<Product> showingProducts = convertIDsToProducts(filterProducts());
            filterProductsWithPrice(showingProducts);
            sortProducts(showingProducts);
            return showingProducts;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getAllShowingProductNames() {
        ArrayList<String> showingProductNames = new ArrayList<>();
        try {
            ArrayList<Product> showingProducts = convertIDsToProducts(filterProducts());
            filterProductsWithPrice(showingProducts);
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
            filterProductsWithPrice(showingProducts);
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
            filterOnName(filteredProductIds, filter.getFilterNames());
        }
        return filteredProductIds;
    }

    private void filterProductsWithPrice(ArrayList<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            double productPrice = getProductPriceForVendor(products.get(i));

            if(!(productPrice <= getFinishPeriod() && productPrice >= getStartPeriod())) {
                products.remove(products.get(i));
                i--;
            }
        }
    }

    private double getProductPriceForVendor(Product product) {
        try {
            double productPriceForVendor = 0;

            if(OffTable.isThereProductInOff(product.getID())) {
                productPriceForVendor = OffTable.getOffPercentByProductID(product.getID());
            } else {
             productPriceForVendor = product.getPrice();
            }

            return productPriceForVendor;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
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

    private void filterOnName(ArrayList<String> filteredProductIds, ArrayList<String> filterNames) {
        filteredProductIds.removeIf(filterProductId -> {
            boolean result = true;

            try {
                for (String filterName : filterNames) {
                    if(ProductTable.getProductByID(filterProductId).getName().contains(filterName)) {
                        result = false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        });
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

    public ArrayList<String> getAllComparingProductNames() {
        ArrayList<String> comparingProductNames = new ArrayList<>();
        try {
            String firstProductCategory = ProductTable.getProductByID(comparingProducts[0].getID()).getCategory();
            while (CategoryTable.getParentCategory(firstProductCategory) != null &&
                    !CategoryTable.getParentCategory(firstProductCategory).equals("All Products"))
                firstProductCategory = CategoryTable.getParentCategory(firstProductCategory);
            for (Product product : convertIDsToProducts(filterOnCategory(firstProductCategory))) {
                comparingProductNames.add(product.getName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return comparingProductNames;
    }

    public ArrayList<String> getAllComparingProductIDs() {
        ArrayList<String> comparingProductIDs = new ArrayList<>();
        try {
            String firstProductCategory = ProductTable.getProductByID(comparingProducts[0].getID()).getCategory();
            while (CategoryTable.getParentCategory(firstProductCategory) != null &&
                    !CategoryTable.getParentCategory(firstProductCategory).equals("All Products"))
                firstProductCategory = CategoryTable.getParentCategory(firstProductCategory);
            for (String productID : filterOnCategory(firstProductCategory)) {
                comparingProductIDs.add(productID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return comparingProductIDs;
    }

    public double getAverageScore(String productID){
        try {
            double averageScore = 0;
            for (Integer score : ProductTable.getAllScores(productID)) {
                averageScore += score;
            }
            return averageScore / ProductTable.getAllScores(productID).size();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public Notification addComment(String title, String content) {
        if (title.length() > 16)
            return Notification.ERROR_TITLE_LENGTH;
        if (content.length() > 100)
            return Notification.ERROR_COMMENT_LENGTH;
        Comment comment = new Comment();
        comment.setTitle(title);
        comment.setContent(content);
        comment.setStatus(2);
        comment.setCustomerUsername(Control.getUsername());
        comment.setProductID(this.currentProduct);
        String commentID;
        try {
            do {
                commentID = generateCommentID();
            } while (ProductTable.isThereCommentByID(commentID));
            comment.setCommentID(commentID);
            ProductTable.addComment(comment);
            return Notification.ADD_COMMENT;
        } catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    private String generateCommentID() {
        StringBuilder commentID = new StringBuilder("c");
        char[] validChars = {'0', '2', '1', '3', '5', '8', '4', '9', '7', '6'};

        for (int i = 0; i < 7; ++i)
            commentID.append(validChars[((int) (Math.random() * 1000000)) % validChars.length]);

        return commentID.toString();
    }

    public ArrayList<String> getShowingCommentTitles(){
        ArrayList<String> allShowingCommentTitles = new ArrayList<>();
        try {
            for (Comment comment : ProductTable.getAllLoggedInUserComment(Control.getUsername(), getCurrentProduct())) {
                allShowingCommentTitles.add(comment.getTitle());
            }
            for (Comment comment : ProductTable.getAllApprovedCommentsOnThisProduct(getCurrentProduct())) {
                allShowingCommentTitles.add(comment.getTitle());
            }
            return allShowingCommentTitles;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<String> getShowingCommentIDs(){
        ArrayList<String> allShowingCommentIDs = new ArrayList<>();
        try {
            for (Comment comment : ProductTable.getAllLoggedInUserComment(Control.getUsername(), getCurrentProduct())) {
                allShowingCommentIDs.add(comment.getCommentID());
            }
            for (Comment comment : ProductTable.getAllApprovedCommentsOnThisProduct(getCurrentProduct())) {
                allShowingCommentIDs.add(comment.getCommentID());
            }
            return allShowingCommentIDs;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Comment getCommentByID(String commentID) {
        try {
            return ProductTable.getCommentByID(commentID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Image getProductImageByID(String ID, int number) {
        try {
            if(doesProductHaveImage(ID))
                return new Image(ProductTable.getProductImageInputStream(ID, number));
            return new Image(ProductTable.getProductImageInputStream("1", 1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean doesProductHaveImage(String ID) {
        return ProductTable.getProductImageFilePath(ID, 1) != null;
    }

    public boolean doesProductHaveImageWithNumber(String ID, int number) {
        return ProductTable.getProductImageFilePath(ID, number) != null;

    }

    public int getProductImagesNumberByID(String productID) {
        int counter = 0;
        for(int i = 1; i < 6; ++i) {
            if(doesProductHaveImageWithNumber(productID, i))
                counter++;
        }
        return counter;
    }

    public TreeItem<Category> getCategoryTableRoot() {
        try {
            TreeItem rootCategory = new TreeItem(CategoryTable.getCategoryWithName("All Products"));
            setSubCategories(rootCategory);
            return rootCategory;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSubCategories(TreeItem parentCategoryTreeItem) throws SQLException, ClassNotFoundException {
        Category parentCategory = (Category) parentCategoryTreeItem.getValue();
        for (Category subCategory : CategoryTable.getSubCategories(parentCategory.getName())) {
            TreeItem subCategoryTreeItem = new TreeItem(subCategory);
            parentCategoryTreeItem.getChildren().add(subCategoryTreeItem);
            setSubCategories(subCategoryTreeItem);
        }
    }

    public void addProductPicture(String productID, File pictureFile) {
        if(pictureFile != null) {
            try {
                ProductTable.addImage(productID, getProductImagesNumberByID(productID) + 1, pictureFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void editProductPicture(String productID, File pictureFile, int imageNumber) {
        if(pictureFile != null) {
            try {
                ProductTable.deleteImage(productID, imageNumber);
                ProductTable.addImage(productID, imageNumber, pictureFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteProductImage(String productID, int imageNumber) {
        try {
            ProductTable.deleteImage(productID, imageNumber);
            for(int i = imageNumber + 1; doesProductHaveImageWithNumber(productID, i); ++i) {
                ProductTable.reNumProductImage(productID, i, i-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
