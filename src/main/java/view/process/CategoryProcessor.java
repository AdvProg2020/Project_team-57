package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AdminControl;
import model.existence.Category;
import notification.Notification;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryProcessor extends ListicOptionProcessor {
    private static CategoryProcessor categoryProcessor = null;
    private static AdminControl adminControl = AdminControl.getController();

    private CategoryProcessor() {
        functionsHashMap = new HashMap<>();
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

    public static ListicOptionMenu setMenu(String json, String categoryName) {
        ListicOptionMenu categoryMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, ListicOptionMenu.class);
        categoryMenu.setOption(adminControl.getCategoryByName(categoryName));
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

        Category newCategory = new Category(oldCategory);
        Notification notification = null;

        if(fieldName.equals("Name")) {
            newCategory.setName(fieldValue);
            notification = adminControl.editCategoryName(oldCategory, newCategory);
        } else if(fieldName.equals("Features")) {
            newCategory.setFeatures(fieldValue);
            notification = adminControl.editCategoryFeatures(oldCategory, newCategory);
        } else if(fieldName.equals("ParentName")) {
            newCategory.setParentCategory(fieldValue);
            notification = adminControl.editCategoryParentName(oldCategory, newCategory);
        }

        System.out.println(notification.getMessage());

        if(notification.equals(Notification.CATEGORY_MODIFIED)) {
            menu.setOption(newCategory);
        } else {
            menu.setOption(oldCategory);
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
