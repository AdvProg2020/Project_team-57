package controller.account;

import controller.Control;
import javafx.scene.image.Image;
import model.db.AccountTable;
import model.db.LogTable;
import model.db.OffTable;
import model.db.VendorTable;
import model.existence.Account;
import model.existence.Log;
import model.existence.Off;
import notification.Notification;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;


public class AccountControl extends Control implements IOValidity {
    private static AccountControl customerControl = null;
    private static String currentLogID;

    public static String getCurrentLogID() {
        return currentLogID;
    }

    public static void setCurrentLogID(String currentLogID) {
        AccountControl.currentLogID = currentLogID;
    }

    public Account getAccount() {
        try {
            //System.out.println(AccountTable.getAccountByUsername(Control.getUsername()));
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
            if(oldPassword == null || oldPassword.isEmpty())
                return Notification.EMPTY_OLD_PASSWORD;
            if(newPassword == null || newPassword.isEmpty())
                return Notification.EMPTY_NEW_PASSWORD;
            if(!AccountTable.isPasswordCorrect(Control.getUsername(), oldPassword))
                return Notification.WRONG_OLD_PASSWORD;
            if (oldPassword.equals(newPassword))
                return Notification.SAME_PASSWORD_ERROR;
            if (newPassword.length() < 8 || newPassword.length() > 16)
                return Notification.ERROR_PASSWORD_LENGTH_EDIT;
            if (!this.isPasswordValid(newPassword))
                return Notification.ERROR_PASSWORD_FORMAT_EDIT;
            AccountTable.editField(Control.getUsername(), "Password", newPassword);
            return Notification.CHANGE_PASSWORD_SUCCESSFULLY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification editField(String fieldName, String newValue) {
        try {
            /*if (AccountTable.getValueByField(Control.getUsername(), fieldName) != null &&
                    AccountTable.getValueByField(Control.getUsername(), fieldName).equals(newValue))
                return Notification.SAME_FIELD_ERROR;*/

            if(isNewValueValid(fieldName, newValue)) {
                AccountTable.editField(Control.getUsername(), fieldName, newValue);
                return Notification.EDIT_FIELD_SUCCESSFULLY;
            } else {
                return InvalidField(fieldName, newValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Notification.UNKNOWN_ERROR;
        }
    }

    private Notification InvalidField(String fieldName, String newValue) {
        Notification notification = null;

        switch (fieldName) {
            case "FirstName" :
                if(newValue == null || newValue.length() == 0)
                    notification = Notification.EMPTY_FIRST_NAME_EDIT;
                else
                    notification = Notification.ERROR_FIRST_NAME_LENGTH_EDIT;
                break;
            case "LastName" :
                if(newValue == null || newValue.length() == 0)
                    notification = Notification.EMPTY_LAST_NAME_EDIT;
                else
                    notification = Notification.ERROR_LAST_NAME_LENGTH_EDIT;
                break;
            case "Email" :
                notification = Notification.ERROR_EMAIL_LENGTH_EDIT;
                break;
            case "Brand" :
                notification = Notification.ERROR_BRAND_LENGTH_EDIT;
                break;
            default :
                System.out.println("Shit. Error In Checking Field Validity");
                break;
        }

        return notification;
    }

    public boolean isNewValueValid(String fieldName, String newValue) {
        boolean fieldValidity = false;

        switch (fieldName) {
            case "FirstName" :
            case "LastName" :
                fieldValidity = newValue != null && newValue.length() != 0 && newValue.length() <= 25;
                break;
            case "Email" :
            case "Brand" :
                fieldValidity = newValue == null || newValue.length() <= 35;
                break;
            default :
                System.out.println("Shit. Error In Checking Field Validity");
                break;
        }

        return fieldValidity;
    }

    public Notification addMoney(String moneyString) {
        try {
            double money = Double.parseDouble(moneyString);

            if(money == 0)
                return Notification.INVALID_ADDING_DOUBLE_MONEY;

            AccountTable.changeCredit(Control.getUsername(), money);
            return Notification.RISE_MONEY_SUCCESSFULLY;
        } catch (NumberFormatException e) {
            System.out.println("Shit");
            return Notification.INVALID_ADDING_DOUBLE_MONEY;
        } catch (Exception e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Notification getMoney(String moneyString) {
        try {
            double money = Double.parseDouble(moneyString);

            if(money == 0)
                return Notification.INVALID_ADDING_DOUBLE_MONEY;

            if (AccountTable.getCredit(Control.getUsername()) < money)
                return Notification.LACK_BALANCE_ERROR;

            AccountTable.changeCredit(Control.getUsername(), -money);
            return Notification.GET_MONEY_SUCCESSFULLY;
        } catch (NumberFormatException e) {
            return Notification.INVALID_ADDING_DOUBLE_MONEY;
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

    public ArrayList<Account> getAllAccounts() {
        try {
            return AccountTable.getAllAccounts();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getAllUsernames() {
        ArrayList<String> allUsernames = new ArrayList<>();
        try {
            for (Account account : AccountTable.getAllUsers()) {
                allUsernames.add(account.getUsername());
            }
            return allUsernames;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Notification deleteUserWithUsername(String username){
        try {
            AccountTable.deleteUserWithUsername(username);
            return Notification.DELETE_USER;
        } catch (SQLException e) {
           return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public ArrayList<String> getAdminsUsernames(){
        ArrayList<String> adminsUsernames = new ArrayList<>();
        try {
            for (Account admin : AccountTable.getAllAdmins()) {
                adminsUsernames.add(admin.getUsername());
            }
            return adminsUsernames;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getAllCustomerNames() {
        ArrayList<String> allCustomers = new ArrayList<>();
        try {
            for(Account account : AccountTable.getAllCustomers())
            {
                allCustomers.add(account.getUsername());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return allCustomers;
    }

    public Off getOffByID(String offID) {
        try {
            Off off = OffTable.getSpecificOff(offID);
            return off;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isThereOffInEditingTable(String offID) {
        try {
            return OffTable.isThereEditingOffWithID(offID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Off getOffFromEditingTable(String offID) {
        try {
            return OffTable.getSpecificEditingOffByID(offID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Off getVendorOff(String offID) {
        try {
            if(OffTable.isThereEditingOffWithID(offID))
                return OffTable.getSpecificEditingOffByID(offID);
            return OffTable.getSpecificOff(offID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Log.ProductOfLog getProductOfLog(String productID){
        try {
            for (Log.ProductOfLog productOfLog : LogTable.getCustomerLogByID(getCurrentLogID()).getAllProducts()) {
                if (productID.equals(productOfLog.getProductID()))
                    return productOfLog;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Account> getModifiedAccounts(Account.AccountType accountType) {
        try {
            switch (accountType) {
                case ADMIN:
                    return AccountTable.getAllAdmins();
                case VENDOR:
                    return AccountTable.getAllVendors();
                case CUSTOMER:
                    return AccountTable.getAllCustomers();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Image getProfileImageByUsername(String username) {
        try {
            if(doesUserHaveImage(username))
                return new Image(AccountTable.getProfileImageInputStream(username));
            return new Image(AccountTable.getProfileImageInputStream("1"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doesUserHaveImage(String username) {
        return AccountTable.getUserImageFilePath(username) != null;
    }

}
