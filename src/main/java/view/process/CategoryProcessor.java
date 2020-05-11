package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AdminControl;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.ArrayList;

public class CategoryProcessor extends ListicOptionProcessor {
    CategoryProcessor categoryProcessor = null;
    AdminControl adminControl = AdminControl.getController();

    private CategoryProcessor() {
        functionsHashMap.put("Edit Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return editCategory(objects);
            }
        });
        functionsHashMap.put("Remove Category", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return removeCategory(objects);
            }
        });

    }

    public static CategoryProcessor getInstance() {
        if(categoryProcessor == null)
            categoryProcessor = new CategoryProcessor();

        return categoryProcessor;
    }

    ListicOptionMenu setMenu(String json, String categoryName) {
        ListicOptionMenu categoryMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ListicOptionMenu.class);
        categoryMenu.setOption(adminControl.getCategoryByName(categoryMenu));
        return categoryMenu;
    }

    public Menu editCategory(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Category oldCategory = (Category) objects[1];

        String fieldName = null, fieldValue = null;
        boolean flag = true;

        ArrayList<String> availableFields = new ArrayList<String>();
        availableFields.add("Name");
        availableFields.add("Features");
        availableFields.add("ParentName");

        while(flag) {
            System.out.println("Please Enter The Field You Wanna Edit :");

            fieldName = setOptionsForArrayList(availableFields);

            if(fieldName.equals("Cancel"))
                return menu;
            else if(fieldName != null)
                flag = false;
        }

        System.out.println("Please Enter The New Value Of Your Field :");
        fieldValue = scanner.nextLine().trim();

        Category newCategory = new Category();

        if(fieldName.equals("Name")) {
            newCategory.setName(fieldValue);
            System.out.println(adminControl.editCategoryName(oldCategory, newCategory).getMessage());
        } else if(fieldName.equals("Features")) {
            newCategory.setFeatures(fieldValue);
            System.out.println(adminControl.editCategoryFeatures(oldCategory, newCategory).getMessage());
        } else if(fieldName.equals("ParentName")) {
            newCategory.setParentName(fieldValue);
            System.out.println(adminControl.editCategoryParentName(oldCategory, newCategory).getMessage());
        }

        return menu;
    }

    public Menu removeCategory(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Category category = (Category) objects[1];
        System.out.println(adminControl.removeCategory(category).getMessage());
        return menu.getParentMenu();
    }
}
