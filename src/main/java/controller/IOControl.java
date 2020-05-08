package controller;

import controller.account.ValidPassword;
import model.db.AccountTable;
import model.existence.Account;
import notification.Notification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class IOControl extends Control implements ValidPassword {
    private static IOControl ioControl = null;

    public Notification register(Account account) {
        if (account.getUsername().length() < 6 || account.getUsername().length() > 16)
            return Notification.ERROR_USERNAME_LENGTH;
        if (!isUsernameValid(account.getUsername()))
            return Notification.ERROR_USERNAME_FORMAT;
        if (account.getPassword().length() < 8 || account.getPassword().length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!this.isPasswordValid(account.getPassword()))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            if (AccountTable.isUsernameFree(account.getUsername())) {
                AccountTable.addAccount(account.getUsername(), account.getPassword(), account.getType());
                return Notification.REGISTER_SUCCESSFUL;
            } else
                return Notification.ERROR_FULL_USERNAME;
        } catch (SQLException e) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e){
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification login(Account account){
        if (account.getUsername().length() < 6 || account.getUsername().length() > 16)
            return Notification.ERROR_USERNAME_LENGTH;
        if (!isUsernameValid(account.getUsername()))
            return Notification.ERROR_USERNAME_FORMAT;
        if (account.getPassword().length() < 8 || account.getPassword().length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!this.isPasswordValid(account.getPassword()))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            if (!AccountTable.isUsernameFree(account.getUsername())) {
                if (AccountTable.isPasswordCorrect(account.getUsername(), account.getPassword())) {
                    if(AccountTable.isUserApproved(account.getUsername())) {
                        Control.setType(AccountTable.getTypeByUsername(account.getUsername()));
                        Control.setUsername(account.getUsername());
                        Control.setLoggedIn(true);
                        return Notification.LOGIN_SUCCESSFUL;
                    } else {
                        return Notification.USER_NOT_APPROVED;
                    }
                } else
                    return Notification.WRONG_PASSWORD;
            } else
                return Notification.ERROR_FREE_USERNAME;
        } catch (SQLException e) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    private boolean isUsernameValid(String username)
    {
        Character[] validChars = {'-', '_', '$', '%', '@', '.', '*', '&', '+'};
        ArrayList<Character> validCharacters = new ArrayList<Character>(Arrays.asList(validChars));
        for (int i = 0; i < username.length(); ++i) {
            char c = username.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') &&
                    !(c >= '0' && c <= '9') && !(validCharacters.contains(c)))
                return false;
        }
        return true;
    }

    public boolean isThereAdmin() {
        try {
            return AccountTable.isThereAdmin();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IOControl getController() {
        if (ioControl == null)
            ioControl = new IOControl();
        return ioControl;
    }
}