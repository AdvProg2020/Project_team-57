package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AccountControl;
import view.menu.Menu;
import view.menu.RegisterMenu;

import java.util.HashMap;

public class RegisterProcessor extends ListicOptionProcessor {
    private static AccountControl accountControl = AccountControl.getController();
    private static RegisterProcessor registerProcessor = null;

    private RegisterProcessor(){
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

    }

    public static RegisterProcessor getInstance(){
        if(registerProcessor == null)
            registerProcessor = new RegisterProcessor();

        return registerProcessor;
    }

    public static RegisterMenu setMenu(String json, String ID){
        RegisterMenu registerMenu = new GsonBuilder().setPrettyPrinting().create().fromJson(json, RegisterMenu.class);
        registerMenu.setAccount(accountControl.getAccountByUsername(ID));
        return registerMenu;
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
}
