package view.process;

import controller.account.AccountControl;
import model.existence.Account;
import view.menu.ListicOptionMenu;
import view.menu.Menu;

import java.util.HashMap;

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

    public static void setMenu(ListicOptionMenu userMenu, String ID){
        userMenu.setOption(accountControl.getAccountByUsername(ID));
    }

    public Menu acceptRequest(Object... objects){
        ListicOptionMenu menu = (ListicOptionMenu)objects[0];
        Account account = (Account)objects[1];

        System.out.println(accountControl.modifyApprove(account.getUsername(), 1).getMessage());
        return menu.getParentMenu();
    }

    public Menu declineRequest(Object... objects){
        ListicOptionMenu menu = (ListicOptionMenu)objects[0];
        Account account = (Account)objects[1];

        System.out.println(accountControl.modifyApprove(account.getUsername(), 0).getMessage());
        return menu.getParentMenu();
    }

    public Menu deleteUser(Object... objects) {
        ListicOptionMenu menu = (ListicOptionMenu) objects[0];
        Account account = (Account) objects[1];

        System.out.println(accountControl.deleteUserWithUsername(account.getUsername()).getMessage());
        return menu.getParentMenu();
    }

}
