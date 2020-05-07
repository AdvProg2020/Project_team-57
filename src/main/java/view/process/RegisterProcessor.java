package view.process;

import com.google.gson.GsonBuilder;
import controller.account.AccountControl;
import view.menu.RegisterMenu;

public class RegisterProcessor extends ListicOptionProcessor {
    private static AccountControl accountControl = AccountControl.getController();
    private static RegisterProcessor registerProcessor = null;

    private RegisterProcessor(){

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

}
