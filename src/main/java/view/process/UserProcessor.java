package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AccountControl;
import model.existence.Account;
import view.menu.ListicOptionMenu;
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
        ListicOptionMenu menu = (ListicOptionMenu)objects[0];
        Account account = (Account)objects[1];

        System.out.println(accountControl.modifyApprove(account.getUsername(), 1).getMessage());
        return menu.getParentMenu();
    }

    public Menu declineRequest(Object... objects){
        Object[] parameters = objects.clone();
        ListicOptionMenu menu = (ListicOptionMenu)objects[0];
        Account account = (Account)objects[1];

        System.out.println(accountControl.modifyApprove(account.getUsername(), 0).getMessage());
        return menu.getParentMenu();
    }

    public Menu deleteUser(Object... objects) {
        Object[] parameters = objects.clone();
        Menu menu = (Menu)parameters[0];
        String userName = (String)parameters[1];
        accountControl.deleteUserWithUsername(userName);
        return menu;
    }
}
