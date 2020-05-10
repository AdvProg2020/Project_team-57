package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AccountControl;
import view.menu.Menu;
import view.menu.UserMenu;

import java.util.HashMap;
import java.util.Objects;

public class UserProcessor extends ListicOptionProcessor {
    private static AccountControl accountControl = AccountControl.getController();
    private static UserProcessor userProcessor = null;

    private UserProcessor(){
        this.functionsHashMap = new HashMap<>();
        functionsHashMap.put("Accept Request", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return acceptRequest(objects);
            }
        });
        functionsHashMap.put("Decline Request", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return declineRequest(objects);
            }
        });
        functionsHashMap.put("Delete User", new FunctioningOption() {
            @Override
            public Menu doTheThing(Object... objects) {
                return deleteUser(objects);
            }
        });

    }

    public static UserProcessor getInstance(){
        if(userProcessor == null)
            userProcessor = new UserProcessor();

        return userProcessor;
    }

    public static UserMenu setMenu(String json, String ID){
        UserMenu userMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, UserMenu.class);
        userMenu.setAccount(accountControl.getAccountByUsername(ID));
        return userMenu;
    }

    public Menu acceptRequest(Object... objects){
        Object[] parameters = objects.clone();
        Menu menu = (Menu)objects[0];
        String userName = (String)objects[1];

        accountControl.modifyApprove(userName, 1);
        return menu;
    }

    public Menu declineRequest(Object... objects){
        Object[] parameters = objects.clone();
        Menu menu = (Menu)objects[0];
        String userName = (String)objects[1];

        accountControl.modifyApprove(userName, 0);
        return menu;
    }

    public Menu deleteUser(Object... objects)
    {
        Object[] parameters = objects.clone();
        Menu menu = (Menu)parameters[0];
        String userName = (String)parameters[1];
        accountControl.deleteUserWithUsername(userName);
        return menu;
    }
}
