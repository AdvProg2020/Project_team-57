package server.controller;

import server.controller.account.AdminControl;
import server.controller.account.IOValidity;
import server.model.db.AccountTable;
import server.model.db.CartTable;
import server.model.db.DiscountTable;
import server.model.db.OffTable;
import server.model.existence.Account;
import notification.Notification;

import java.sql.SQLException;

public class IOControl implements IOValidity {
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
            if (AccountTable.getInstance().isUsernameFree(account.getUsername())) {
                AccountTable.getInstance().addAccount(account);
                return Notification.REGISTER_SUCCESSFUL;
            } else
                return Notification.ERROR_FULL_USERNAME;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification login(Account account){
        try {
            AccountTable accountTable = AccountTable.getInstance();

            if (!accountTable.isUsernameFree(account.getUsername())) {
                if (accountTable.isPasswordCorrect(account.getUsername(), account.getPassword())) {
                    if(accountTable.isUserApproved(account.getUsername())) {
                        String type = accountTable.getTypeByUsername(account.getUsername());
                        CartTable cartTable = CartTable.getInstance();
                        if (type.equals("Customer")) {
                            cartTable.addTempToUsername(account.getUsername());
                        }
                        cartTable.removeTemp();
                        if (type.equals("Customer") && accountTable.didPeriodPass("Ya Zahra"))
                            AdminControl.getController().getGiftDiscount();
                        //TODO(FOR MEDIA)
                        //AccountControl.getController().initAudios();
                        DiscountTable.getInstance().removeOutDatedDiscounts();
                        OffTable.getInstance().removeOutDatedOffs();
                        return Notification.LOGIN_SUCCESSFUL;
                    } else {
                        return Notification.USER_NOT_APPROVED;
                    }
                } else
                    return Notification.WRONG_PASSWORD;
            } else
                return Notification.ERROR_FREE_USERNAME;
        } catch (SQLException | ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public boolean isThereAdmin() {
        try {
            return AccountTable.getInstance().isThereAdmin();
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