package server.controller.account;

import notification.Notification;
import server.controller.product.ProductControl;
import server.model.db.*;
import server.model.existence.Account;
import server.model.existence.Account.*;
import server.model.existence.Off;
import server.model.existence.Product;
import server.server.RandomGenerator;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static server.controller.Lock.*;


public class AccountControl implements IOValidity, RandomGenerator {
    private static AccountControl customerControl = null;

    public static AccountControl getController() {
        if (customerControl == null)
            customerControl = new AccountControl();
        return customerControl;
    }

    public Account getAccountByUsername(String username){
        try {
            if(isUsernameValid(username))
                return AccountTable.getAccountByUsername(username);
        } catch (Exception e) {
            //:)
            return new Account();
        }
        Account account = new Account();
        account.setType("Hacker");
        return account;
    }

    public Notification changePassword(String username, String oldPassword, String newPassword) {
        try {
            if(isUsernameValid(username)) {
                if (oldPassword == null || oldPassword.isEmpty())
                    return Notification.EMPTY_OLD_PASSWORD;
                if (newPassword == null || newPassword.isEmpty())
                    return Notification.EMPTY_NEW_PASSWORD;
                if (!AccountTable.isPasswordCorrect(username, oldPassword))
                    return Notification.WRONG_OLD_PASSWORD;
                if (oldPassword.equals(newPassword))
                    return Notification.SAME_PASSWORD_ERROR;
                if (newPassword.length() < 8 || newPassword.length() > 16)
                    return Notification.ERROR_PASSWORD_LENGTH_EDIT;
                if (!this.isPasswordValid(newPassword))
                    return Notification.ERROR_PASSWORD_FORMAT_EDIT;
                AccountTable.editField(username, "Password", newPassword);
                return Notification.CHANGE_PASSWORD_SUCCESSFULLY;
            } else {
                return Notification.FUCK_YOU;
            }
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification addMoney(String username, double money) {
        try {
            if(isUsernameValid(username)) {
                if (money != 0)
                    AccountTable.changeCredit(username, money);

                return Notification.RISE_MONEY_SUCCESSFULLY;
            } else {
                return Notification.FUCK_YOU;
            }
        } catch (NumberFormatException e) {
            return Notification.INVALID_ADDING_DOUBLE_MONEY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification getMoney(String username, double money) {
        try {
            if(isUsernameValid(username))
            if(money > 0) {
                if (AccountTable.getCredit(username) < money)
                    return Notification.LACK_BALANCE_ERROR;

                AccountTable.changeCredit(username, -money);
            } else {
                return Notification.FUCK_YOU;
            }

            return Notification.GET_MONEY_SUCCESSFULLY;
        } catch (NumberFormatException e) {
            return Notification.INVALID_ADDING_DOUBLE_MONEY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification modifyApprove(String username, int flag) {
        try {
            synchronized (IO_LOCK) {
                VendorTable.modifyApprove(username, flag);
                if (flag == 0)
                    return Notification.DECLINE_REQUEST;
                else
                    return Notification.ACCEPT_ADD_VENDOR_REQUEST;
            }
        } catch (SQLException | ClassNotFoundException throwable) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public synchronized Notification deleteUserWithUsername(String username) {
        try {
            synchronized (IO_LOCK) {
                if(getAccountByUsername(username).getType().equals("Vendor")) {
                    for (Product product : VendorTable.getProductsWithUsername(username)) {
                        ProductControl.getController().removeProductById(product.getID());
                    }
                    for (Off vendorOff : OffTable.getVendorOffs(username)) {
                        String ID = vendorOff.getOffID();
                        OffTable.removeOffByID(ID);
                        if(ProductControl.getController().doesOffHaveImage(ID))
                            OffTable.removeOffImage(ID);
                        if(ProductControl.getController().isOffEditing(ID)) {
                            OffTable.removeEditingOff(ID);
                            if(ProductControl.getController().doesEditingOffHaveImage(ID))
                                OffTable.removeEditingOffImage(ID);
                        }
                    }
                } else {
                    synchronized (COMMENT_SCORE_LOCK) {
                        ProductTable.removeAllUserComments(username);
                        ProductTable.removeAllUserScores(username);
                    }
                    CartTable.removeAllCustomerCartProducts(username);
                }
                AccountTable.deleteUserWithUsername(username);
                AccountTable.deleteProfileImage(username);
                return Notification.DELETE_USER;
            }
        } catch (SQLException | ClassNotFoundException e) {
           return Notification.UNKNOWN_ERROR;
        }
    }

    public Off getOffByID(String offID) {
        try {
            if(isGeneralIDValid('o', offID)) {
                Off off = OffTable.getSpecificOff(offID);
                return off;
            }else {
                Off off = new Off();
                off.setVendorUsername("Fuck");
                return off;
            }
        } catch (SQLException | ClassNotFoundException e) {
            //:)
        }
        return new Off();
    }

    public ArrayList<Account> getModifiedAccounts(Account.AccountType accountType, String... searchs) {
        if(searchs == null || searchs.length == 0) {
            try {
                switch (accountType) {
                    case ADMIN:
                        return AccountTable.getAllAdmins();
                    case VENDOR:
                        synchronized (IO_LOCK) {
                            return AccountTable.getAllVendors();
                        }
                    case CUSTOMER:
                        return AccountTable.getAllCustomers();
                }
            } catch (SQLException | ClassNotFoundException e) {
                //:)
            }
            return new ArrayList<>();
        } else {
            ArrayList<Account> accounts = getModifiedAccounts(accountType);
            accounts.removeIf(account -> {
                boolean result = false;
                for (int i = 0; i < searchs.length; i++) {
                    if(account.getUsername().equals(searchs[i])) {
                        result = true;
                    }
                }
                return result;
            });
            return accounts;
        }
    }

    public FileInputStream getUserImageInputStream(String username) {
        try {
            if(isUsernameValid(username)) {
                synchronized (SUPPORTER_LOCK) {
                    if (!AccountTable.isUsernameFreeForSupporter(username)) {
                        synchronized (SUPPORTER_IMAGE_LOCK) {
                            return AccountTable.getSupporterDefaultImageInputStream();
                        }
                    }
                }
            } else {
                return null;
            }

            synchronized (USER_IMAGE_LOCK) {
                String imageInput = doesUserHaveImage(username) ? username : "1";
                return AccountTable.getProfileImageInputStream(imageInput);
            }
        } catch (FileNotFoundException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean doesUserHaveImage(String username) {
        if(isUsernameValid(username))
            return AccountTable.getUserImageFilePath(username) != null;
        else
            return false;
    }

    public void setAccountPicture(String username, File pictureFile) {
        if(isUsernameValid(username)) {
            synchronized (USER_IMAGE_LOCK) {
                if (pictureFile == null) {
                    if (doesUserHaveImage(username))
                        AccountTable.deleteProfileImage(username);
                } else {
                    if (doesUserHaveImage(username)) {
                        AccountTable.deleteProfileImage(username);
                    }
                    try {
                        AccountTable.setProfileImage(username, pictureFile);
                    } catch (IOException e) {
                        //:)
                    }
                }
            }
        }
    }

    public FileOutputStream getAccountPictureOutputStream(String username, String pictureExtension) {
        if(isUsernameValid(username)) {
            if (doesUserHaveImage(username)) {
                AccountTable.deleteProfileImage(username);
            }
            try {
                return AccountTable.getProfileImageOutputStream(username, pictureExtension);
            } catch (IOException e) {
                System.err.println("Error In #getAccountPictureOutputStream");
                e.printStackTrace();
            }
        }

        return null;
    }

    public Notification editAccount(Account newAccount) {
        try {
            if(isUsernameValid(newAccount.getUsername())) {
                Account oldAccount = AccountTable.getAccountByUsername(newAccount.getUsername());
                if (newAccount.getFirstName() != null)
                    if (!newAccount.getFirstName().isEmpty() &&
                            !newAccount.getFirstName().equals(oldAccount.getFirstName())) {
                        AccountTable.editField(newAccount.getUsername(), "FirstName", newAccount.getFirstName());
                    }
                if (newAccount.getLastName() != null)
                    if (!newAccount.getLastName().isEmpty() &&
                            !newAccount.getLastName().equals(oldAccount.getLastName())) {
                        AccountTable.editField(newAccount.getUsername(), "LastName", newAccount.getLastName());
                    }
                if (newAccount.getEmail() == null || !newAccount.getEmail().equals(oldAccount.getEmail())) {
                    AccountTable.editField(newAccount.getUsername(), "Email", newAccount.getEmail() == null ? "" : newAccount.getEmail());
                }
                if (oldAccount.getType().equals("Vendor")) {
                    if (newAccount.getBrand() == null || !newAccount.getBrand().equals(oldAccount.getBrand())) {
                        AccountTable.editField(newAccount.getUsername(), "Brand", newAccount.getBrand() == null ? "" : newAccount.getBrand());
                    }
                }
                return Notification.EDIT_FIELD_SUCCESSFULLY;
            } else {
                return Notification.FUCK_YOU;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Notification.UNKNOWN_ERROR;
    }

    public double getCredit(String username) {
        try {
            if (isUsernameValid(username)) {
                Account account = AccountTable.getAccountByUsername(username);
                return account.getCredit();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public ArrayList<Account> convertUsernameToAccounts(ArrayList<String> usernames) {
        ArrayList<Account> accounts = new ArrayList<>();
        for (String username : usernames) {
            accounts.add(getAccountByUsername(username));
        }
        return accounts;
    }

    public Double getWage() {
        try {
            synchronized (WAGE_LOCK) {
                return AccountTable.getWage();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Double getMinimumWallet() {
        try {
            synchronized (MINIMUM_WALLET_LOCK) {
                return AccountTable.getMinimumWallet();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public ArrayList<Supporter> getAllSupporters() {
        try {
            return AccountTable.getAllSupporters();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean isUserSupporter(String username) {
        try {
            if(isUsernameValid(username)) {
                return !AccountTable.isUsernameFreeForSupporter(username);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Supporter getSupporterByUsername(String supporterUsername) {
        try {
            if(isUsernameValid(supporterUsername)) {
                return AccountTable.getSupporter(supporterUsername);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
