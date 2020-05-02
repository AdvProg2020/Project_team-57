package controller.account;

import controller.Control;
import model.db.AccountTable;
import model.existence.Account;
import notification.Notification;


public class AccountControl extends Control {
    private static AccountControl accountControl = null;

    public Account getAccount() {
        try {
            return AccountTable.getAccountWithName(Control.getUsername());
        } catch (Exception e) {
            return null;
        }
    }

    public Notification changePassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword))
            return Notification.SAME_PASSWORD_ERROR;
        if (newPassword.length() < 8 || newPassword.length() > 16)
            return Notification.ERROR_PASSWORD_LENGTH;
        if (!isPasswordValid(newPassword))
            return Notification.ERROR_PASSWORD_FORMAT;
        try {
            AccountTable.editField(Control.getUsername(), "Password", newPassword);
            return Notification.CHANGE_PASSWORD_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification editField(String fieldName, String newValue) {
        try {
            if (AccountTable.getValueWithField(Control.getUsername(), fieldName).equals(newValue))
                return Notification.SAME_FIELD_ERROR;
            AccountTable.editField(Control.getUsername(), fieldName, newValue);
            return Notification.EDIT_FIELD_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification addMoney(double money) {
        try {
            AccountTable.changeCredit(Control.getUsername(), money);
            return Notification.RISE_MONEY_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification getMoney(double money) {
        try {
            if (AccountTable.getCredit(Control.getUsername()) < money)
                return Notification.LACK_BALANCE_ERROR;
            AccountTable.changeCredit(Control.getUsername(), -money);
            return Notification.GET_MONEY_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    private boolean isPasswordValid(String password) {
        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') &&
                    !(c >= '0' && c <= '9') && (c != '_') && (c != '-'))
                return false;
        }
        boolean flag = false;
        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);
            if (c >= '0' && c <= '9') {
                flag = true;
                break;
            }
        }
        if (!flag)
            return false;
        flag = false;
        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                flag = true;
                break;
            }
        }
        if (!flag)
            return false;
        return true;
    } // duplicate

    public static AccountControl getController() {
        if (accountControl == null)
            accountControl = new AccountControl();
        return accountControl;
    }
}
