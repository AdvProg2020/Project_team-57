package server.model.db;

import server.model.existence.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryTable extends Database {
    public static boolean isThereCategoryWithName(String name) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Categories WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, name);
        return preparedStatement.executeQuery().next();
    }

    public static Category getCategoryWithName(String categoryName) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Categories WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, categoryName);
        return new Category(preparedStatement.executeQuery());
    }

    public static ArrayList<Category> getSubCategories(String parentCategoryName) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Categories WHERE ParentCategory = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, parentCategoryName);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Category> allSubcategories = new ArrayList<>();
        while (resultSet.next()){
            allSubcategories.add(new Category(resultSet));
        }
        return allSubcategories;
    }

    public static void addCategory(Category category) throws SQLException, ClassNotFoundException {
        String command = "INSERT INTO Categories(Name, Features, ParentCategory) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, category.getName());
        preparedStatement.setString(2, category.getFeatures());
        preparedStatement.setString(3, category.getParentCategory());
        preparedStatement.execute();
    }

    public static void setCategoryParentName(String category, String newParentName) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Categories SET ParentCategory = ? WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, newParentName);
        preparedStatement.setString(2, category);
        preparedStatement.execute();
    }

    public static void removeCategoryWithName(String categoryName) throws SQLException, ClassNotFoundException {
        String command = "DELETE FROM Categories WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, categoryName);
        preparedStatement.execute();
    }

    public static ArrayList<Category> getAllCategories() throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Categories";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        ArrayList<Category> allCategories = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            allCategories.add(new Category(resultSet));
        }
        return allCategories;
    }

    public static void changeCategoryName(String initialName, String newName) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Categories SET Name = ? WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, initialName);
        preparedStatement.execute();
    }

    public static void changeCategoryFeatures(String categoryName, String newFeatures) throws SQLException, ClassNotFoundException {
        String command = "UPDATE Categories SET Features = ? WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, newFeatures);
        preparedStatement.setString(2, categoryName);
        preparedStatement.execute();
    }

    public static boolean isThereSubCategories(String categoryName) throws SQLException, ClassNotFoundException {
        String command = "SELECT * FROM Categories WHERE ParentCategory = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, categoryName);
        return preparedStatement.executeQuery().next();
    }

    public static String getParentCategory(String category) throws SQLException, ClassNotFoundException {
        String command = "SELECT ParentCategory FROM Categories WHERE Name = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setString(1, category);
        return preparedStatement.executeQuery().getString("ParentCategory");
    }
}
