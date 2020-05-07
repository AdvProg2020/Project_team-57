package controller.account;

import controller.Control;
import model.db.AccountTable;
import model.db.VendorTable;
import model.existence.Account;
import notification.Notification;


import java.sql.SQLException;
import java.util.ArrayList;


public class AccountControl extends Control implements ValidPassword{
    private static AccountControl customerControl = null;

    public Account getAccount() {
        try {
            return AccountTable.getAccountByUsername(Control.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Account getAccountByUsername(String username){
        try {
            return AccountTable.getAccountByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Notification changePassword(String oldPassword, String newPassword) {
        try {
            if(!AccountTable.isPasswordCorrect(Control.getUsername(), oldPassword))
                return Notification.WRONG_OLD_PASSWORD;
            if (oldPassword.equals(newPassword))
                return Notification.SAME_PASSWORD_ERROR;
            if (newPassword.length() < 8 || newPassword.length() > 16)
                return Notification.ERROR_PASSWORD_LENGTH;
            if (!this.isPasswordValid(newPassword))
                return Notification.ERROR_PASSWORD_FORMAT;
            AccountTable.editField(Control.getUsername(), "Password", newPassword);
            return Notification.CHANGE_PASSWORD_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification editField(String fieldName, String newValue) {
        try {
            if (AccountTable.getValueByField(Control.getUsername(), fieldName) != null &&
                    AccountTable.getValueByField(Control.getUsername(), fieldName).equals(newValue))
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

    public Notification modifyApprove(String username, int flag) {
        try {
            VendorTable.modifyApprove(username, flag);
            if (flag == 0)
                return Notification.DECLINE_REQUEST;
            else
                return Notification.ACCEPT_ADD_VENDOR_REQUEST;
        } catch (SQLException throwable) {
            return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public ArrayList<String> getUnapprovedUsernames() {
        ArrayList<String> allUsernames = new ArrayList<>();
        try {

            for (Account account : VendorTable.getUnApprovedVendors()) {
                allUsernames.add(account.getUsername());
            }
            return allUsernames;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AccountControl getController() {
        if (customerControl == null)
            customerControl = new AccountControl();
        return customerControl;
    }
}
