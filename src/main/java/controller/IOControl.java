package controller;

import model.db.AccountTable;
import model.existence.Account;
import notification.Notification;

import java.sql.SQLException;

public class IOControl extends Control {
    private static IOControl ioControl = null;


    public Notification register(Account account) {
        if (account.getUsername().length() < 6 || account.getUsername().length() > 16)
            return Notification.ERROR_USERNAME_LENGTH;
        if (account.getUsername().contains("\\W"))
            return Notification.ERROR_USERNAME_FORMAT;
        if (account.getPassword().length() < 8 || account.getPassword().length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!isPasswordValid(account.getPassword()))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            if (AccountTable.isUsernameFree(account.getUsername())) {
                AccountTable.addAccount(account.getUsername(), account.getPassword(), account.getType());
                return Notification.REGISTER_SUCCESSFUL;
            } else
                return Notification.ERROR_FREE_USERNAME;
        } catch (SQLException e) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e){
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification login(Account account){
        if (account.getUsername().length() < 6 || account.getUsername().length() > 16)
            return Notification.ERROR_USERNAME_LENGTH;
        if (account.getUsername().contains("\\W"))
            return Notification.ERROR_USERNAME_FORMAT;
        if (account.getPassword().length() < 8 || account.getPassword().length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!isPasswordValid(account.getPassword()))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            if (AccountTable.isUsernameFree(account.getUsername())) {
                if (AccountTable.isPasswordCorrect(account.getUsername(), account.getPassword())) {
                    Control.setType(account.getType());
                    Control.setUsername(account.getUsername());
                    Control.setLoggedIn(true);
                    return Notification.LOGIN_SUCCESSFUL;
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

    private boolean isPasswordValid(String password) {
        if (password.contains("[^0-9a-zA-Z-_]"))
            return false;
        if (!password.contains("[a-z || A-Z]"))
            return false;
        if (!password.contains("[0-9]"))
            return false;
        return true;
    }

    public static IOControl getController() {
        if (ioControl == null)
            ioControl = new IOControl();
        return ioControl;
    }
}