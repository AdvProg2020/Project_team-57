package controller.user;

import controller.Control;
import notification.Notification;

import java.util.regex.Pattern;

public class IOAccountControl extends Control {
    private static IOAccountControl accountControl;
    private boolean isLogedIn;
    private String typeOfClient;

    public static IOAccountControl getInstance() {
        if (accountControl == null)
            return new IOAccountControl();

        return IOAccountControl.accountControl;
    }

    public Notification register(String username, String password, String type) {
        if (/*is there username with name*/ true) {
            return Notification.INVALID_USERNAME;
        } else if (!isPasswordValid(password)) {
            return Notification.INVALID_PASSWORD;
        } else {
            return Notification.REGISTER_SUCCESSFUL;
        }
    }

    public Notification login(String username, String password) {
        if (/*is there username with name*/ true) {
            return Notification.INVALID_USERNAME;
        } else if (/*is password correct*/ true) {
            return Notification.WRONG_PASSWORD;
        } else {
            return Notification.LOGIN_SUCCESSFUL;
        }
    }

    public boolean isPasswordValid(String password) {
        if (password.length() > 16 || password.length() < 8 || Pattern.matches(password, "^[A-Za-z0-9]+$"))
            return false;

        return true;
    }

    public boolean isLogedIn() {
        return isLogedIn;
    }

    public String getTypeOfClient() {
        return typeOfClient;
    }
}