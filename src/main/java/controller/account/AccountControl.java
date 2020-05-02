package controller.account;

import controller.Control;
import model.db.AccountTable;
import model.existence.Account;
import notification.Notification;

public class AccountControl extends Control {
    private AccountControl accountControl = null;

    public Account getAccount(){
        return AccountTable.getAccountWithName(Control.getUsername());
    }

    public Notification changePassword(String oldPassword, String newPassword){
        if (oldPassword.equals(newPassword))
            return Notification.SAME_PASSWORD_ERROR;
        if (newPassword.length() < 8 || newPassword.length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!isPasswordValid(newPassword))
            return Notification.ERROR_PASSWORD_FORMAT;
        AccountTable.editField(Control.getUsername(), "Password", newPassword);
        return Notification.CHANGE_PASSWORD_SUCCESSFULLY;
    }

    public Notification editField(String fieldName, String newValue){
        if (AccountTable.getValueWithField(Control.getUsername(), fieldName).equals(newValue))
            return
    }

    private boolean isPasswordValid(String password) {
        for(int i = 0; i < password.length(); ++i)
        {
            char c = password.charAt(i);
            if(!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') &&
                    !(c >= '0' && c <= '9') && (c != '_') && (c != '-'))
                return false;
        }
        boolean flag = false;
        for(int i = 0; i < password.length(); ++i)
        {
            char c =password.charAt(i);
            if(c >= '0' && c <= '9') {
                flag = true;
                break;
            }
        }
        if(!flag)
            return false;
        flag = false;
        for(int i = 0; i < password.length(); ++i)
        {
            char c =password.charAt(i);
            if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                flag = true;
                break;
            }
        }
        if(!flag)
            return false;
        return true;
    } // duplicate

}
