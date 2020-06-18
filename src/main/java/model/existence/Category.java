package model.existence;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Category {
    private String name;
    private String features;
    private String parentCategory;


    public Category(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("Name");
        this.features = resultSet.getString("Features");
        this.parentCategory = resultSet.getString("ParentCategory");
    }

    public Category() {
    }

    public Category(Category category) {
        this.name = category.name;
        this.features = category.features;
        this.parentCategory = category.parentCategory;
    }

    @Override
    public Category clone() {
        Category category = new Category();
        category.setName(this.name);
        category.setFeatures(this.features);
        category.setParentCategory(this.parentCategory);
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }
}
