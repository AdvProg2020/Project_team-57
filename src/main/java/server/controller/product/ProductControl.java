package server.controller.product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import server.model.db.*;
import server.model.existence.Category;
import server.model.existence.Comment;
import server.model.existence.Off;
import server.model.existence.Product;
import notification.Notification;
import server.server.Property;
import server.server.Property.*;
import server.server.RandomGenerator;
import static server.controller.Lock.*;


import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class ProductControl implements RandomGenerator {
    private static ProductControl productControl = null;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Deprecated
    public void setProductOffPrice(Product product) throws SQLException, ClassNotFoundException {
        if(OffTable.isThereProductInOff(product.getID())) {
            product.setOnSale(true);
            double offPercent = OffTable.getOffPercentByProductID(product.getID());
            product.setOffPercent(offPercent);
            product.setOffPrice( (1.0 - offPercent / 100) * product.getPrice());
        }
    }

    public Product getProductById(String productId) {
        try {
            Product product = ProductTable.getProductByID(productId);
/*            setProductOffPrice(product);*/
            return product;
        } catch (Exception e) {
            return new Product();
        }
    }

    public synchronized Notification removeProductById(String productId) {
        try {
            ProductTable.removeProductByID(productId);
            if (!EditingProductTable.isIDFree(productId))
                EditingProductTable.removeProductById(productId);
            OffTable.removeProductFromOffs(productId);
            OffTable.removeProductFromEditingOffs(productId);
            CartTable.deleteProductFromCarts(productId);
            ProductTable.removeAllProductComments(productId);
            ProductTable.deleteProductFromScores(productId);

            synchronized (PRODUCT_IMAGE_LOCK) {
                ProductTable.removeAllProductImages(productId);
            }
            synchronized (EDITING_PRODUCT_IMAGE_LOCK) {
                EditingProductTable.removeAllEditingProductImages(productId);
            }
            synchronized (PRODUCT_FILE_LOCK) {
                ProductTable.removeProductFileByID(productId);
            }
            synchronized (EDITING_PRODUCT_FILE_LOCK) {
                EditingProductTable.removeEditingProductFile(productId);
            }

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
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
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
            //:)
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
            Product product = null;

            if (EditingProductTable.isIDFree(ID))
                product = ProductTable.getProductByID(ID);
            else
                product = EditingProductTable.getEditingProductWithID(ID);

            /*setProductOffPrice(product);*/
            return product;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return null;
    }

    public Notification removeEditingProductById(String editingProductID) {
        try {
            synchronized (ADMIN_MODIFY_EDIT_PRODUCT_LOCK) {
                EditingProductTable.removeProductById(editingProductID);
                ProductTable.setProductStatus(editingProductID, 1);
                return Notification.DECLINE_EDITING_PRODUCT;
            }
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Product> getAllShowingProducts(Property property) {
        try {
            OffTable.removeOutDatedOffs();
            ArrayList<Product> showingProducts = convertIDsToProducts(filterProducts(property));
            filterProductsWithPrice(showingProducts, property.getFilter());
            sortProducts(showingProducts, property.getSort());
            return showingProducts;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new ArrayList<>();
    }

    private ArrayList<String> filterProducts(Property property) throws SQLException, ClassNotFoundException {
        ArrayList<String> filteredProductIds = new ArrayList<>();
        Filter filter = property.getFilter();
        if (filter.getFilterCategories().size() != 0) {
            for (String category : filter.getFilterCategories()) {
                for (String productId : filterOnCategory(category, property)) {
                    if (!filteredProductIds.contains(productId))
                        filteredProductIds.add(productId);
                }
            }
        } else {
            if(!property.isOffListic()) {
                for (Product product : ProductTable.getAllShowingProducts()) {
                    filteredProductIds.add(product.getID());
                }
            } else {
                for (Product product : ProductTable.getAllShowingProducts()) {
                    if (OffTable.isThereProductInSpecificOff(property.getListicOffID(), product.getID()))
                        filteredProductIds.add(product.getID());
                }
            }
        }
        if (filter.getFilterNames().size() != 0) {
            filterOnName(filteredProductIds, filter.getFilterNames());
        }
        return filteredProductIds;
    }

    private void filterProductsWithPrice(ArrayList<Product> products, Filter filter) {
        for (int i = 0; i < products.size(); i++) {
            double productPrice = getProductPriceForVendor(products.get(i));
            if(!(productPrice <= filter.getMaxPrice() && productPrice >= filter.getMinPrice())) {
                products.remove(products.get(i));
                i--;
            }
        }
    }

    private double getProductPriceForVendor(Product product) {
        try {
            double productPriceForVendor = 0;

            if(OffTable.isThereProductInOff(product.getID())) {
                productPriceForVendor = product.getOffPrice();
            } else {
             productPriceForVendor = product.getPrice();
            }

            return productPriceForVendor;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
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

    private void sortProducts(ArrayList<Product> products, Sort sort)
    {
        if(sort.getSortType() == Sort.SortType.VIEW && sort.isAscending())
            Collections.sort(products, new Sorting.ViewSortAscending());
        else if(sort.getSortType() == Sort.SortType.VIEW && !sort.isAscending())
            Collections.sort(products, new Sorting.ViewSortDescending());
        else if(sort.getSortType() == Sort.SortType.NAME && sort.isAscending())
            Collections.sort(products, new Sorting.NameSortAscending());
        else if(sort.getSortType() == Sort.SortType.NAME && !sort.isAscending())
            Collections.sort(products, new Sorting.NameSortDescending());
        else if(sort.getSortType() == Sort.SortType.TIME && sort.isAscending())
            Collections.sort(products, new Sorting.TimeSortAscending());
        else if(sort.getSortType() == Sort.SortType.TIME && !sort.isAscending())
            Collections.sort(products, new Sorting.TimeSortDescending());
        else if(sort.getSortType() == Sort.SortType.SCORE && sort.isAscending())
            Collections.sort(products, new Sorting.ScoreSortAscending());
        else if(sort.getSortType() == Sort.SortType.SCORE && !sort.isAscending())
            Collections.sort(products, new Sorting.ScoreSortDescending());
    }

    private ArrayList<String> filterOnCategory(String category, Property property) throws SQLException, ClassNotFoundException {
        if (!CategoryTable.isThereSubCategories(category) &&
                !ProductTable.isThereProductWithSpecificCategory(category)) {
            return new ArrayList<>();
        }
        ArrayList<String> productIds = new ArrayList<>();
        if(!property.isOffListic()) {
            for (Product product : ProductTable.getProductsWithCategory(category)) {
                if (product.getStatus() != 2)
                    productIds.add(product.getID());
            }
        } else {
            for (Product product : ProductTable.getProductsWithCategory(category)) {
                if (product.getStatus() != 2 && OffTable.isThereProductInSpecificOff(property.getListicOffID(), product.getID()))
                    productIds.add(product.getID());
            }
        }

        for (Category subCategory : CategoryTable.getSubCategories(category)) {
            productIds.addAll(filterOnCategory(subCategory.getName(), property));
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
                //:)
            } catch (ClassNotFoundException e) {
                //:)
            }
            return result;
        });
    }

    public void addSeenToProduct(String productID) {
        try {
            ProductTable.addSeenToProductWithID(productID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
    }

    public Off getOffByProductID(String productID) {
        try {
            return OffTable.getOffByProductID(productID);
        } catch (SQLException e) {
            return new Off();
        } catch (ClassNotFoundException e) {
            return new Off();
        }
    }

    public ArrayList <Product> getAllComparingProducts(Property property) {
        try {
            OffTable.removeOutDatedOffs();
            String firstProductCategory = ProductTable.getProductByID(property.getComparingProducts(0).getID()).getCategory();
            while (CategoryTable.getParentCategory(firstProductCategory) != null &&
                    !CategoryTable.getParentCategory(firstProductCategory).equals("All Products"))
                firstProductCategory = CategoryTable.getParentCategory(firstProductCategory);
            ArrayList<Product> comparableProducts = convertIDsToProducts(filterOnCategory(firstProductCategory, property));
            comparableProducts.removeIf(product -> {
                return product.getID().equals(property.getComparingProducts(0).getID()) ;
            });
            return comparableProducts;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //Comment Scoring
    public int getScore(Comment comment){
        try {
            if (ProductTable.didScore(comment.getCustomerUsername(), comment.getProductID()))
                return ProductTable.getScore(comment.getCustomerUsername(), comment.getProductID());
            return 0;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    public Notification setScore(Comment comment, String username){
        try {
            if (ProductTable.didScore(username, comment.getProductID())){
                ProductTable.updateScore(username, comment.getProductID(), comment.getScore());
                ProductTable.updateProductsAvgScore(comment.getProductID());
                return Notification.UPDATE_SCORE;
            }
            ProductTable.setScore(comment.getCustomerUsername(), comment.getProductID(), comment.getScore());
            ProductTable.updateProductsAvgScore(comment.getProductID());
            return Notification.SET_SCORE;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error In #setScore");
            e.printStackTrace();
        }

        return Notification.UNKNOWN_ERROR;
    }
    //Comment Scoring

    public double getAverageScore(String productID){
        try {
            double averageScore = 0;
            for (Integer score : ProductTable.getAllScores(productID)) {
                averageScore += score;
            }
            return averageScore / ProductTable.getAllScores(productID).size();
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return 1;
    }

    public Notification addComment(Comment comment, String username) {
        comment.setStatus(2);
        comment.setCustomerUsername(username);
        String commentID;

        try {
            commentID = "c" + generateRandomNumber(7, s -> {
                try {
                    return ProductTable.isThereCommentByID(s);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return false;
            });
            comment.setCommentID(commentID);
            ProductTable.addComment(comment);

            //Todo Approving Score Haminjori Nemishe Score Gozasht. Bas Taeid Beshe
            if(comment.getScore() != 0)
                setScore(comment, username);

            return Notification.ADD_COMMENT;
        } catch (SQLException | ClassNotFoundException e){
            System.err.println("Error In #addComment");
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Comment> getAllProductComments(String productId, String username, String type) {
        ArrayList<Comment> productComments = new ArrayList<>();

        try {
            for (Comment comment : ProductTable.getAllApprovedCommentsOnThisProduct(productId)) {
                comment.setScore(getScore(comment));
                productComments.add(comment);
            }

            if(type != null && type.equals("Customer")) {
                for (Comment comment : ProductTable.getAllLoggedInUserComment(username, productId)) {
                    comment.setScore(getScore(comment));
                    productComments.add(comment);
                }
            }
            if(username != null && !username.isEmpty()) {
                for (Comment productComment : productComments) {
                    if (username.equals(productComment.getCustomerUsername()))
                        productComment.setCustomerUsername("**You**");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return productComments;
    }

    public Comment getCommentByID(String commentID) {
        try {
            return ProductTable.getCommentByID(commentID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }

        return new Comment();
    }

    public Image getProductImageByID(String ID, int number) {
        try {
            if(doesProductHaveImage(ID)) {
                FileInputStream fileInputStream = ProductTable.getProductImageInputStream(ID, number);
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                return image;
            }
            FileInputStream fileInputStream = ProductTable.getProductImageInputStream("1", 1);
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        } catch (FileNotFoundException e) {
            //:)
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    public FileInputStream getProductImageFileInputStreamByID(String ID, int number) {
        try {
            String productID = doesProductHaveImage(ID) ? ID : ("" + 1);
            return ProductTable.getProductImageInputStream(productID, number);
        } catch (FileNotFoundException e) {
            //:)
        }
        return null;
    }

    public FileInputStream getEditingProductImageFileInputStreamByID(String ID, int number) {
        try {
            String productID = doesEditingProductHaveImage(ID) ? ID : ("" + 1);
            return EditingProductTable.getEditingProductImageInputStream(productID, number);
        } catch (FileNotFoundException e) {
            //:)
        }
        return null;
    }

    public Image getOffImageByID(String offID) {
        try {
        if(doesOffHaveImage(offID)) {
            FileInputStream fileInputStream = OffTable.getOffImageInputStream(offID);
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        }
        FileInputStream fileInputStream = OffTable.getOffImageInputStream("1");
        Image image = new Image(fileInputStream);
        fileInputStream.close();
        return image;
        } catch (FileNotFoundException e) {
            //:)
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    public void deleteEditingOffPicture(String offID) {
        if(doesEditingOffHaveImage(offID)) {
            OffTable.removeEditingOffImage(offID);
        }
    }

    public Image getEditingOffImageByID(String offID) {
        try {
            if(doesEditingOffHaveImage(offID)) {
                FileInputStream fileInputStream = OffTable.getEditingOffImageInputStream(offID);
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                return image;
            }
            FileInputStream fileInputStream = OffTable.getOffImageInputStream("1");
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        } catch (FileNotFoundException e) {
            //:)
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    @Deprecated
    public File getOffImageFileByID(String offID) {
        if(doesOffHaveImage(offID)) {
            return new File(OffTable.getOffImageFilePath(offID));
        } else
            return new File(OffTable.getOffImageFilePath("1"));

    }

    public FileInputStream getOffImageFileInputStreamByID(String offID) {
        try {
            return OffTable.getOffImageInputStream((doesOffHaveImage(offID) ? offID : "1"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean doesOffHaveImage(String offID) {
        return OffTable.getOffImageFilePath(offID) != null;
    }

    public boolean doesProductHaveImage(String ID) {
        synchronized (PRODUCT_IMAGE_LOCK) {
            return ProductTable.getProductImageFilePath(ID, 1) != null;
        }
    }

    public boolean doesProductHaveImageWithNumber(String ID, int number) {
        synchronized (PRODUCT_IMAGE_LOCK) {
            return ProductTable.getProductImageFilePath(ID, number) != null;
        }
    }

    public void setEditingOffPicture(String offID, File pictureFile) {
        if(pictureFile != null) {
            if(!pictureFile.getPath().contains("database\\Images\\EditingOffs\\" + offID)) {
                if (doesEditingOffHaveImage(offID)) {
                    OffTable.removeEditingOffImage(offID);
                }
                try {
                    OffTable.setEditingOffImage(offID, pictureFile);
                } catch (IOException e) {
                    //:)
                }
            }
        }
    }

    public boolean doesEditingOffHaveImage(String offID) {
        return OffTable.getEditingOffImageFilePath(offID) != null;
    }

    public int getProductImagesNumberByID(String productID) {
        synchronized (PRODUCT_IMAGE_LOCK) {
            int counter = 0;
            for(int i = 1; i < 6; ++i) {
                if(doesProductHaveImageWithNumber(productID, i))
                    counter++;
            }
            return counter;
        }
    }

    public int getEditingProductImagesNumberByID(String productID) {
        synchronized (EDITING_PRODUCT_IMAGE_LOCK) {
            int counter = 0;
            for(int i = 1; i < 6; ++i) {
                if(doesEditingProductHaveImageWithNumber(productID, i))
                    counter++;
            }
            return counter;
        }
    }

    private boolean doesEditingProductHaveImageWithNumber(String productID, int number) {
        synchronized (EDITING_PRODUCT_IMAGE_LOCK) {
            return EditingProductTable.getEditingProductImageFilePath(productID, number) != null;
        }
    }

    public FileOutputStream getProductPictureOutputStream(String productID, String fileExtension) {
        try {
            return ProductTable.getProductImageOutputStream(productID, fileExtension, getProductImagesNumberByID(productID) + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FileOutputStream getOffPictureOutputStream(String offID, String fileExtension) {
        try {
            return OffTable.getOffImageOutputStream(offID, fileExtension);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean doesEditingProductHaveImage(String ID) {
        synchronized (EDITING_PRODUCT_IMAGE_LOCK) {
            return EditingProductTable.getEditingProductImageFilePath(ID, 1) != null;
        }
    }

    public void deleteEditingProductPictures(String productID) {
        try {
            if(doesEditingProductHaveImage(productID)) {
                int board = getEditingProductImagesNumberByID(productID);
                for(int i = 0; i < board; ++i) {
                    EditingProductTable.deleteImage(productID, (i + 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileOutputStream getEditingProductPictureOutputStream(String productID, String fileExtension) {
        try {
            return EditingProductTable.getEditingProductImageOutputStream(productID, fileExtension, getEditingProductImagesNumberByID(productID) + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Product> getAllOffProductsByOffID(String offID, boolean isEditing) {
        try {
            return convertIDsToProducts((!isEditing ? OffTable.getSpecificOff(offID).getProductIDs() : OffTable.getSpecificEditingOff(offID).getProductIDs()));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Off getOffByID(String offID) {
        try {
            return OffTable.getSpecificOff(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Off();
    }

    public boolean isThereOffWithID(String offID) {
        try {
            return OffTable.isThereOffWithID(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isOffEditing(String offID) {
        try {
            return OffTable.isThereEditingOffWithID(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public File getEditingOffImageFileByID(String offID) {
        if(doesEditingOffHaveImage(offID)) {
            return new File(OffTable.getEditingOffImageFilePath(offID));
        } else
            return new File(OffTable.getOffImageFilePath("1"));
    }

    public FileInputStream getEditingOffImageFileInputStreamByID(String offID) {
        try {
            if(doesEditingOffHaveImage(offID)) {
                return OffTable.getEditingOffImageInputStream(offID);
            } else
                return OffTable.getOffImageInputStream("1");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Off getEditingOffByID(String offID) {
        try {
            return OffTable.getSpecificEditingOff(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Off();
    }

    public String getProductImageExtensionByNumber(String productID, int imageNumber) {
        return ProductTable.getProductImageFileExtension(productID, imageNumber);
    }

    public String getEditingProductImageExtensionByNumber(String productID, int imageNumber) {
        return EditingProductTable.getEditingProductImageFileExtension(productID, imageNumber);
    }

    public String getOffImageExtensionByID(String ID) {
        return OffTable.getOffImageExtensionByID(ID);
    }

    public String getEditingOffImageExtensionByID(String ID) {
        return OffTable.getEditingOffImageExtensionByID(ID);
    }

    public FileOutputStream getEditingOffPictureOutputStream(String offID, String fileExtension) {
        try {
            return OffTable.getEditingOffImageOutputStream(offID, fileExtension);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addProductFileInfo(String productID, String productFileInfoJson) {
        try {
            ProductTable.addProductFileInfo(productID, productFileInfoJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileOutputStream getProductFileOutputStream(String productID, String fileExtension) {
        try {
            Product.ProductFileInfo productFileInfo = getProductFileInfo(productID);
            return ProductTable.getProductFileOutputStream(productID, productFileInfo.getName(), fileExtension);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Product.ProductFileInfo getProductFileInfo(String productID) {
        try {
            String productFileInfoJson = ProductTable.getProductFileInfo(productID);
            return gson.fromJson(productFileInfoJson, Product.ProductFileInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Product.ProductFileInfo();
    }

    public boolean doesProductHaveFile(String productID) {
        synchronized (PRODUCT_FILE_LOCK) {
            return ProductTable.doesProductHaveFile(productID);
        }
    }

    public String getProductFileExtension(String productID) {
        return getProductFileInfo(productID).getExtension().toLowerCase();
    }

    public FileInputStream getProductFileInputStreamByID(String productID) {
        try {
            Product.ProductFileInfo productFileInfo = getProductFileInfo(productID);
            return ProductTable.getProductFileInputStream(productID, productFileInfo.getName(), productFileInfo.getExtension().toLowerCase());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void editProductFileInfo(String productID, String productFileInfoJson) {
        try {
            EditingProductTable.editProductFileInfo(productID, productFileInfoJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteEditingProductFile(String productID) {
        EditingProductTable.removeEditingProductFile(productID);
    }

    public FileOutputStream getEditingProductFileOutputStream(String productID, String fileExtension) {
        try {
            Product.ProductFileInfo editingProductFileInfo = getEditingProductFileInfo(productID);
            return EditingProductTable.getEditingProductFileOutputStream(productID, editingProductFileInfo.getName(), fileExtension);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Product.ProductFileInfo getEditingProductFileInfo(String productID) {
        try {
            String productFileInfoJson = EditingProductTable.getEditingProductFileInfo(productID);
            return gson.fromJson(productFileInfoJson, Product.ProductFileInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Product.ProductFileInfo();
    }

    public String getEditingProductFileExtension(String productID) {
        return getEditingProductFileInfo(productID).getExtension().toLowerCase();
    }

    public FileInputStream getEditingProductFileInputStreamByID(String productID) {
        try {
            Product.ProductFileInfo productFileInfo = getEditingProductFileInfo(productID);
            return EditingProductTable.getEditingProductFileInputStream(productID, productFileInfo.getName(), productFileInfo.getExtension().toLowerCase());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void initProductFileCountability(String productID) {
        try {
            ProductTable.setProductCountability(productID, true);
            ProductTable.setProductCount(productID, 1);
            ProductTable.setProductAmount(productID, 0);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Boolean doesEditingProductHaveFile(String productID) {
        synchronized (EDITING_PRODUCT_FILE_LOCK) {
            return EditingProductTable.doesEditingProductHaveFile(productID);
        }
    }
}
