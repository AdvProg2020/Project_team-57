package server.controller.product;

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

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class ProductControl {
    private static ProductControl productControl = null;

    public Product getProductById(String productId) {
        try {
            Product product = ProductTable.getInstance().getProductByID(productId);
/*            setProductOffPrice(product);*/
            return product;
        } catch (Exception e) {
            return new Product();
        }
    }

    public synchronized Notification removeProductById(String productId) {
        try {
            ProductTable productTable = ProductTable.getInstance();
            EditingProductTable editingProductTable = EditingProductTable.getInstance();
            OffTable offTable = OffTable.getInstance();

            productTable.removeProductByID(productId);
            if (!editingProductTable.isIDFree(productId))
                editingProductTable.removeProductById(productId);
            offTable.removeProductFromOffs(productId);
            offTable.removeProductFromEditingOffs(productId);
            CartTable.getInstance().deleteProductFromCarts(productId);
            productTable.removeAllProductComments(productId);
            productTable.deleteProductFromScores(productId);
            productTable.removeAllProductImages(productId);
            editingProductTable.removeAllEditingProductImages(productId);
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
            ProductTable productTable = ProductTable.getInstance();
            EditingProductTable editingProductTable = EditingProductTable.getInstance();

            if (productTable.getProductByID(ID).getStatus() == 2)
                return Notification.PRODUCT_NOT_AVAILABLE;

//            if (checkFieldEquality(fieldName, newField, ID))
//                return Notification.SAME_FIELD_ERROR;
            if(newField != null && !newField.isEmpty()) {
                if (fieldName.equals("Category") && !CategoryTable.getInstance().isThereCategoryWithName(newField))
                    return Notification.INVALID_CATEGORY_NAME;

                if (productTable.getProductByID(ID).getStatus() == 1)
                    productTable.setProductStatus(ID, 3);

                if (editingProductTable.isIDFree(ID))
                    editingProductTable.addProduct(productTable.getProductByID(ID));

                editSpecificField(fieldName, newField, ID);
            }

            return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    private void editSpecificField(String fieldName, String newField, String ID) {
        try {
            ProductTable productTable = ProductTable.getInstance();
            EditingProductTable editingProductTable = EditingProductTable.getInstance();

            if (fieldName.equals("ProductName") || fieldName.equals("Brand") ||
                    fieldName.equals("Category") || fieldName.equals("Description")) {
                editingProductTable.editFieldWithName(ID, fieldName, newField);
            } else if (fieldName.equals("Count"))
                editingProductTable.changeProductCount(ID, Integer.parseInt(newField));

            else if (fieldName.equals("Amount"))
                editingProductTable.changeProductAmount(ID, Double.parseDouble(newField));

            else if (fieldName.equals("Price"))
                editingProductTable.changeProductPrice(ID, Double.parseDouble(newField));
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
            ProductTable productTable = ProductTable.getInstance();
            EditingProductTable editingProductTable = EditingProductTable.getInstance();

            Product product = null;

            if (editingProductTable.isIDFree(ID))
                product = productTable.getProductByID(ID);
            else
                product = editingProductTable.getEditingProductWithID(ID);

            /*setProductOffPrice(product);*/
            return product;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return null;
    }

    public Notification removeEditingProductById(String editingProductID) {
        try {
            EditingProductTable.getInstance().removeProductById(editingProductID);
            ProductTable.getInstance().setProductStatus(editingProductID, 1);
            return Notification.DECLINE_EDITING_PRODUCT;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return Notification.UNKNOWN_ERROR;
    }

    public ArrayList<Product> getAllShowingProducts(Property property) {
        try {
            OffTable.getInstance().removeOutDatedOffs();
            ArrayList<Product> showingProducts = convertIDsToProducts(filterProducts(property));
            filterProductsWithPrice(showingProducts, property.getFilter());
            sortProducts(showingProducts, property.getSort());
            return showingProducts;
        } catch (SQLException | ClassNotFoundException e) {
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
                for (Product product : ProductTable.getInstance().getAllShowingProducts()) {
                    filteredProductIds.add(product.getID());
                }
            } else {
                for (Product product : ProductTable.getInstance().getAllShowingProducts()) {
                    if (OffTable.getInstance().isThereProductInSpecificOff(property.getListicOffID(), product.getID()))
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

            if(OffTable.getInstance().isThereProductInOff(product.getID())) {
                productPriceForVendor = product.getOffPrice();
            } else {
             productPriceForVendor = product.getPrice();
            }

            return productPriceForVendor;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    private ArrayList<Product> convertIDsToProducts(ArrayList<String> productIDs) throws SQLException, ClassNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        for (String productID : productIDs) {
            products.add(ProductTable.getInstance().getProductByID(productID));
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
        ProductTable productTable = ProductTable.getInstance();
        OffTable offTable = OffTable.getInstance();
        CategoryTable categoryTable = CategoryTable.getInstance();

        if (!categoryTable.isThereSubCategories(category) &&
                !productTable.isThereProductWithSpecificCategory(category)) {
            return new ArrayList<>();
        }
        ArrayList<String> productIds = new ArrayList<>();
        if(!property.isOffListic()) {
            for (Product product : productTable.getProductsWithCategory(category)) {
                if (product.getStatus() != 2)
                    productIds.add(product.getID());
            }
        } else {
            for (Product product : productTable.getProductsWithCategory(category)) {
                if (product.getStatus() != 2 && offTable.isThereProductInSpecificOff(property.getListicOffID(), product.getID()))
                    productIds.add(product.getID());
            }
        }

        for (Category subCategory : categoryTable.getSubCategories(category)) {
            productIds.addAll(filterOnCategory(subCategory.getName(), property));
        }
        return productIds;
    }

    private void filterOnName(ArrayList<String> filteredProductIds, ArrayList<String> filterNames) {
        final ProductTable productTable = ProductTable.getInstance();
        filteredProductIds.removeIf(filterProductId -> {
            boolean result = true;

            try {
                for (String filterName : filterNames) {
                    if(productTable.getProductByID(filterProductId).getName().contains(filterName)) {
                        result = false;
                    }
                }
            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }
            return result;
        });
    }

    public void addSeenToProduct(String productID) {
        try {
            ProductTable.getInstance().addSeenToProductWithID(productID);
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
    }

    public Off getOffByProductID(String productID) {
        try {
            return OffTable.getInstance().getOffByProductID(productID);
        } catch (SQLException | ClassNotFoundException e) {
            return new Off();
        }
    }

    public ArrayList <Product> getAllComparingProducts(Property property) {
        try {
            CategoryTable categoryTable = CategoryTable.getInstance();

            OffTable.getInstance().removeOutDatedOffs();
            String firstProductCategory = ProductTable.getInstance().getProductByID(property.getComparingProducts(0).getID()).getCategory();
            while (categoryTable.getParentCategory(firstProductCategory) != null &&
                    !categoryTable.getParentCategory(firstProductCategory).equals("All Products"))
                firstProductCategory = categoryTable.getParentCategory(firstProductCategory);
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
    public int getScore(Comment comment) {
        try {
            ProductTable productTable = ProductTable.getInstance();

            if (productTable.didScore(comment.getCustomerUsername(), comment.getProductID()))
                return productTable.getScore(comment.getCustomerUsername(), comment.getProductID());
            return 0;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return 0;
    }

    public Notification setScore(Comment comment, String username) {
        try {
            ProductTable productTable = ProductTable.getInstance();

            if (productTable.didScore(username, comment.getProductID())) {
                productTable.updateScore(username, comment.getProductID(), comment.getScore());
                productTable.updateProductsAvgScore(comment.getProductID());
                return Notification.UPDATE_SCORE;
            }
            productTable.setScore(comment.getCustomerUsername(), comment.getProductID(), comment.getScore());
            productTable.updateProductsAvgScore(comment.getProductID());
            return Notification.SET_SCORE;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error In #setScore");
            e.printStackTrace();
        }

        return Notification.UNKNOWN_ERROR;
    }
    //Comment Scoring

    public double getAverageScore(String productID) {
        try {
            ProductTable productTable = ProductTable.getInstance();

            double averageScore = 0;
            for (Integer score : productTable.getAllScores(productID)) {
                averageScore += score;
            }
            return averageScore / productTable.getAllScores(productID).size();
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return 1;
    }

    public Notification addComment(Comment comment, String username) {
        comment.setStatus(2);
        comment.setCustomerUsername(username);
        String commentID;

        try {
            ProductTable productTable = ProductTable.getInstance();

            do {
                commentID = generateCommentID();
            } while (productTable.isThereCommentByID(commentID));
            comment.setCommentID(commentID);
            productTable.addComment(comment);

            //Todo Approving Score Haminjori Nemishe Score Gozasht. Bas Taeid Beshe
            if(comment.getScore() != 0)
                setScore(comment, username);

            return Notification.ADD_COMMENT;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error In #addComment");
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

    public ArrayList<Comment> getAllProductComments(String productId, String username, String type) {
        ArrayList<Comment> productComments = new ArrayList<>();

        try {
            ProductTable productTable = ProductTable.getInstance();

            for (Comment comment : productTable.getAllApprovedCommentsOnThisProduct(productId)) {
                comment.setScore(getScore(comment));
                productComments.add(comment);
            }

            if(type != null && type.equals("Customer")) {
                for (Comment comment : productTable.getAllLoggedInUserComment(username, productId)) {
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
            return ProductTable.getInstance().getCommentByID(commentID);
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }

        return new Comment();
    }

    public Image getProductImageByID(String ID, int number) {
        try {
            if(doesProductHaveImage(ID)) {
                FileInputStream fileInputStream = ProductTable.getInstance().getProductImageInputStream(ID, number);
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                return image;
            }
            FileInputStream fileInputStream = ProductTable.getInstance().getProductImageInputStream("1", 1);
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    public FileInputStream getProductImageFileInputStreamByID(String ID, int number) {
        try {
            String productID = doesProductHaveImage(ID) ? ID : ("" + 1);
            return ProductTable.getInstance().getProductImageInputStream(productID, number);
        } catch (FileNotFoundException e) {
            //:)
        }
        return null;
    }

    public FileInputStream getEditingProductImageFileInputStreamByID(String ID, int number) {
        try {
            String productID = doesEditingProductHaveImage(ID) ? ID : ("" + 1);
            return EditingProductTable.getInstance().getEditingProductImageInputStream(productID, number);
        } catch (FileNotFoundException e) {
            //:)
        }
        return null;
    }

    public Image getOffImageByID(String offID) {
        try {
        if(doesOffHaveImage(offID)) {
            FileInputStream fileInputStream = OffTable.getInstance().getOffImageInputStream(offID);
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        }
        FileInputStream fileInputStream = OffTable.getInstance().getOffImageInputStream("1");
        Image image = new Image(fileInputStream);
        fileInputStream.close();
        return image;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    public void deleteEditingOffPicture(String offID) {
        if(doesEditingOffHaveImage(offID)) {
            OffTable.getInstance().removeEditingOffImage(offID);
        }
    }

    public Image getEditingOffImageByID(String offID) {
        try {
            if(doesEditingOffHaveImage(offID)) {
                FileInputStream fileInputStream = OffTable.getInstance().getEditingOffImageInputStream(offID);
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                return image;
            }
            FileInputStream fileInputStream = OffTable.getInstance().getOffImageInputStream("1");
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    @Deprecated
    public File getOffImageFileByID(String offID) {
        OffTable offTable = OffTable.getInstance();

        if(doesOffHaveImage(offID)) {
            return new File(offTable.getOffImageFilePath(offID));
        } else
            return new File(offTable.getOffImageFilePath("1"));

    }

    public FileInputStream getOffImageFileInputStreamByID(String offID) {
        try {
            return OffTable.getInstance().getOffImageInputStream((doesOffHaveImage(offID) ? offID : "1"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean doesOffHaveImage(String offID) {
        return OffTable.getInstance().getOffImageFilePath(offID) != null;
    }

    public boolean doesProductHaveImage(String ID) {
        return ProductTable.getInstance().getProductImageFilePath(ID, 1) != null;
    }

    public boolean doesProductHaveImageWithNumber(String ID, int number) {
        return ProductTable.getInstance().getProductImageFilePath(ID, number) != null;
    }

    public void setOffPicture(String offID, File pictureFile) {
        if(pictureFile != null) {
            OffTable offTable = OffTable.getInstance();

            if(doesOffHaveImage(offID)) {
                offTable.removeOffImage(offID);
            }
            try {
                offTable.setOffImage(offID, pictureFile);
            } catch (IOException e) {
                //:)
            }
        }
    }

    public void setEditingOffPicture(String offID, File pictureFile) {
        if(pictureFile != null) {
            if(!pictureFile.getPath().contains("database\\Images\\EditingOffs\\" + offID)) {
                OffTable offTable = OffTable.getInstance();

                if (doesEditingOffHaveImage(offID)) {
                    offTable.removeEditingOffImage(offID);
                }
                try {
                    offTable.setEditingOffImage(offID, pictureFile);
                } catch (IOException e) {
                    //:)
                }
            }
        }
    }

    public boolean doesEditingOffHaveImage(String offID) {
        return OffTable.getInstance().getEditingOffImageFilePath(offID) != null;
    }

    public int getProductImagesNumberByID(String productID) {
        int counter = 0;
        for(int i = 1; i < 6; ++i) {
            if(doesProductHaveImageWithNumber(productID, i))
                counter++;
        }
        return counter;
    }

    public int getEditingProductImagesNumberByID(String productID) {
        int counter = 0;
        for(int i = 1; i < 6; ++i) {
            if(doesEditingProductHaveImageWithNumber(productID, i))
                counter++;
        }
        return counter;
    }

    private boolean doesEditingProductHaveImageWithNumber(String productID, int number) {
        return EditingProductTable.getInstance().getEditingProductImageFilePath(productID, number) != null;
    }

    @Deprecated
    public TreeItem<Category> getCategoryTableRoot() {
        try {
            TreeItem rootCategory = new TreeItem(CategoryTable.getInstance().getCategoryWithName("All Products"));
            setSubCategories(rootCategory);
            return rootCategory;
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return null;
    }

    public void setSubCategories(TreeItem parentCategoryTreeItem) throws SQLException, ClassNotFoundException {
        Category parentCategory = (Category) parentCategoryTreeItem.getValue();
        for (Category subCategory : CategoryTable.getInstance().getSubCategories(parentCategory.getName())) {
            TreeItem subCategoryTreeItem = new TreeItem(subCategory);
            parentCategoryTreeItem.getChildren().add(subCategoryTreeItem);
            setSubCategories(subCategoryTreeItem);
        }
    }

    public void addProductPicture(String productID, File pictureFile) {
        if(pictureFile != null) {
            try {
                ProductTable.getInstance().addImage(productID, getProductImagesNumberByID(productID) + 1, pictureFile);
            } catch (IOException e) {
                //:)
            }
        }
    }

    public FileOutputStream getProductPictureOutputStream(String productID, String fileExtension) {
        try {
//            ProductTable.addImage(productID, getProductImagesNumberByID(productID) + 1, pictureFile);
            return ProductTable.getInstance().getProductImageOutputStream(productID, fileExtension, getProductImagesNumberByID(productID) + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FileOutputStream getOffPictureOutputStream(String offID, String fileExtension) {
        try {
            return OffTable.getInstance().getOffImageOutputStream(offID, fileExtension);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean doesEditingProductHaveImage(String ID) {
        return EditingProductTable.getInstance().getEditingProductImageFilePath(ID, 1) != null;
    }

    public void addEditingProductPictures(String productId, ArrayList<File> productImageFiles) {
        try {
            EditingProductTable editingProductTable = EditingProductTable.getInstance();

            ArrayList<File> productNewImageFiles = editingProductTable.copyEditingProductNewImagesInTemp(productId,productImageFiles);
            if(doesEditingProductHaveImage(productId)) {
                int board = getEditingProductImagesNumberByID(productId);
                for(int i = 0; i < board; ++i) {
                    editingProductTable.deleteImage(productId, (i + 1));
                }
            }
            for (int i = 0; i < productNewImageFiles.size(); i++) {
                editingProductTable.addImage(productId, (i + 1), productNewImageFiles.get(i));
            }
            editingProductTable.removeEditingProductTempImages(productId);
        } catch (IOException e) {
            //:)
        }
    }

    public void deleteEditingProductPictures(String productID) {
        try {
            if(doesEditingProductHaveImage(productID)) {
                int board = getEditingProductImagesNumberByID(productID);

                EditingProductTable editingProductTable = EditingProductTable.getInstance();
                for(int i = 0; i < board; ++i) {
                    editingProductTable.deleteImage(productID, (i + 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileOutputStream getEditingProductPictureOutputStream(String productID, String fileExtension) {
        try {
            return EditingProductTable.getInstance().getEditingProductImageOutputStream(productID, fileExtension, getEditingProductImagesNumberByID(productID) + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Image getEditingProductImage(String ID, int number) {
        try {
            if(doesEditingProductHaveImage(ID)) {
                FileInputStream fileInputStream = EditingProductTable.getInstance().getEditingProductImageInputStream(ID, number);
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                return image;
            }
            FileInputStream fileInputStream = EditingProductTable.getInstance().getEditingProductImageInputStream("1", 1);
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<File> getProductImageFiles(Product product) {
        ArrayList<File> imageFiles = new ArrayList<>();
        if(product.getStatus() != 3) {
            imageFiles.addAll(getProductNonEditedImageFiles(product));
        } else {

            EditingProductTable editingProductTable = EditingProductTable.getInstance();
            for(int i = 0; i < getEditingProductImagesNumberByID(product.getID()); ++i) {
                imageFiles.add(
                        new File(editingProductTable.getEditingProductImageFilePath(product.getID(), (i+1)))
                );
            }
        }
        return imageFiles;
    }

    public Image getProductDefaultImage() {
        return getProductImageByID("1", 2);
    }

    public ArrayList<File> getProductNonEditedImageFiles(Product product) {
        ArrayList<File> imageFiles = new ArrayList<>();

        ProductTable productTable = ProductTable.getInstance();
        for(int i = 0; i < getProductImagesNumberByID(product.getID()); ++i) {
            imageFiles.add(
                    new File(productTable.getProductImageFilePath(product.getID(), (i+1)))
            );
        }
        return imageFiles;
    }

    public ArrayList<Product> getAllOffProductsByOffID(String offID, boolean isEditing) {
        try {
            OffTable offTable = OffTable.getInstance();
            return convertIDsToProducts((!isEditing ? offTable.getSpecificOff(offID).getProductIDs() : offTable.getSpecificEditingOff(offID).getProductIDs()));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Off getOffByID(String offID) {
        try {
            return OffTable.getInstance().getSpecificOff(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Off();
    }

    public boolean isThereOffWithID(String offID) {
        try {
            return OffTable.getInstance().isThereOffWithID(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isOffEditing(String offID) {
        try {
            return OffTable.getInstance().isThereEditingOffWithID(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public File getEditingOffImageFileByID(String offID) {

        OffTable offTable = OffTable.getInstance();
        if(doesEditingOffHaveImage(offID)) {
            return new File(offTable.getEditingOffImageFilePath(offID));
        } else
            return new File(offTable.getOffImageFilePath("1"));
    }

    public FileInputStream getEditingOffImageFileInputStreamByID(String offID) {
        try {

            OffTable offTable = OffTable.getInstance();
            if(doesEditingOffHaveImage(offID)) {
                return offTable.getEditingOffImageInputStream(offID);
            } else
                return offTable.getOffImageInputStream("1");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Off getEditingOffByID(String offID) {
        try {
            return OffTable.getInstance().getSpecificEditingOff(offID);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Off();
    }

    public String getProductImageExtensionByNumber(String productID, int imageNumber) {
        return ProductTable.getInstance().getProductImageFileExtension(productID, imageNumber);
    }

    public String getEditingProductImageExtensionByNumber(String productID, int imageNumber) {
        return EditingProductTable.getInstance().getEditingProductImageFileExtension(productID, imageNumber);
    }

    public String getOffImageExtensionByID(String ID) {
        return OffTable.getInstance().getOffImageExtensionByID(ID);
    }

    public String getEditingOffImageExtensionByID(String ID) {
        return OffTable.getInstance().getEditingOffImageExtensionByID(ID);
    }

    public FileOutputStream getEditingOffPictureOutputStream(String offID, String fileExtension) {
        try {
            return OffTable.getInstance().getEditingOffImageOutputStream(offID, fileExtension);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
