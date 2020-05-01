package controller;

import notification.Notification;

import java.sql.SQLException;

public class IOControl extends Control {
    private static IOControl ioControl = null;


    public Notification register(Account account) {
        if (account.getUsername().length < 6)
            return Notification.ERROR_USERNAME_LENGTH;
        if (account.getUsername().contains("\\W"))
            return Notification.ERROR_USERNAME_FORMAT;
        if (account.getPassword().length < 8 || account.getPassword().lenth > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!account.getPassword().contains("[a-z || A-Z]") || !account.getPassword().contains("[0-9]") ||
                !account.getPassword().contains("[^a-zA-Z0-9]"))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            if (AccountTable.isUsernameFree(account.getUsername())) {
                AccountTable.addAccount(account.getUsername(), account.getPassword(), account.getType());
                return Notification.REGISTER_SUCCESSFUL;
            } else
                return Notification.ERROR_FREE_USERNAME;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.ERROR_UNKNOWN_ERROR;
        }
    }

    public Notification login(Account account){
        if (account.getUsername().length < 6)
            return Notification.ERROR_USERNAME_LENGTH;
        if (account.getUsername().contains("\\W"))
            return Notification.ERROR_USERNAME_FORMAT;
        if (account.getPassword().length < 8 || account.getPassword().lenth > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!account.getPassword().contains("[a-z || A-Z]") || !account.getPassword().contains("[0-9]") ||
        !account.getPassword().contains("[^a-zA-Z0-9]"))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            if (AccountTable.isUsernameFree(account.getUsername())) {
                if (AccountTable.isPasswordCorrect(account.getUsername(), account.getPassword())) {
                    AccountTable.login(account.getUsername(), account.getPassword());
                    super.type = AccountTable.getType();
                    super.username = account.getUsername();
                    super.isLoggedIn = true;
                    return Notification.LOGIN_SUCCESSFUL;
                } else
                    return Notification.WRONG_PASSWORD;
            }else
                return Notification.ERROR_FREE_USERNAME;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.ERROR_UNKNOWN_ERROR;
        }
    }

    public static IOControl getController(){
        if (ioControl == null)
            ioControl = new IOControl();
        return ioControl;
    }
}