package controller;

import controller.account.AccountControl;
import controller.account.AdminControl;
import controller.account.IOValidity;
import model.db.AccountTable;
import model.db.CartTable;
import model.db.DiscountTable;
import model.db.OffTable;
import model.existence.Account;
import notification.Notification;

import java.sql.SQLException;

public class IOControl extends Control implements IOValidity {
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
        if (account.getFirstName().length() > 25)
            return Notification.ERROR_FIRST_NAME_LENGTH;
        if (account.getLastName().length() > 25)
            return Notification.ERROR_LAST_NAME_LENGTH;
        try {
            if (AccountTable.isUsernameFree(account.getUsername())) {
                AccountTable.addAccount(account);
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
        try {
            if (!AccountTable.isUsernameFree(account.getUsername())) {
                if (AccountTable.isPasswordCorrect(account.getUsername(), account.getPassword())) {
                    if(AccountTable.isUserApproved(account.getUsername())) {
                        Control.setType(AccountTable.getTypeByUsername(account.getUsername()));
                        Control.setUsername(account.getUsername());
                        Control.setLoggedIn(true);
                        if (Control.getType().equals("Customer")) {
                            CartTable.addTempToUsername(account.getUsername());
                        }
                        CartTable.removeTemp();
                        if (Control.getType().equals("Customer") && AccountTable.didPeriodPass("Ya Zahra"))
                            AdminControl.getController().getGiftDiscount();
                        AccountControl.getController().initAudios();
                        DiscountTable.removeOutDatedDiscounts();
                        OffTable.removeOutDatedOffs();
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

    public boolean isThereAdmin() {
        try {
            return AccountTable.isThereAdmin();
        } catch (Exception e) {
            //:)
            return false;
        }
    }

    public static IOControl getController() {
        if (ioControl == null)
            ioControl = new IOControl();
        return ioControl;
    }
}