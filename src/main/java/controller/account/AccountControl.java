package controller.account;

import controller.Control;
import controller.product.ProductControl;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.db.*;
import model.existence.Account;
import model.existence.Log;
import model.existence.Off;
import model.existence.Product;
import notification.Notification;
import view.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;


public class AccountControl extends Control implements IOValidity {
    private static AccountControl customerControl = null;
    private static String currentLogID;
    private boolean isMusicPlaying = false;
    private ArrayList<Audio> audios;
    private long musicCounter = 0;

    public void initAudios() {
        switch (getType()) {
            case "Admin" :
                audios = Audio.getAdminMusics();
                break;
            case "Vendor" :
                audios = Audio.getVendorMusics();
                break;
            case "Customer" :
                audios = Audio.getCustomerMusics();
         }
         isMusicPlaying = false;
        musicCounter = 0;
    }

    public void stopMusics() {
        if(audios != null) {
            for (Audio audio : audios) {
                audio.stop();
            }
        }
    }

    public long getMusicCount() {
        if(audios == null)
            return 0;
        return audios.size();
    }

    public void stopPlayingMusic() {
        audios.get((int) (musicCounter % audios.size())).music.stop();
    }

    public static class Audio {
        private static ArrayList<Audio> adminAudios;
        private static ArrayList<Audio> vendorAudios;
        private static ArrayList<Audio> customerAudios;
        private String artist;
        private String name;
        private MediaPlayer music;

        private Audio(String artist, String name, MediaPlayer music) {
            this.artist = artist;
            this.name = name;
            this.music = music;
        }

        private static ArrayList<Audio> getAdminMusics() {
            if(adminAudios != null)
                return adminAudios;
            try {
                adminAudios = new ArrayList<>();
                adminAudios.add(makeAudio("Mohsen Chavoshi", "Dele Man", "Admin","Mohsen Chavoshi Dele Man.mp3"));
                adminAudios.add(makeAudio("Mohsen Chavoshi", "Amire Bi Gazand", "Admin","Mohsen Chavoshi Amire Bi Gazand.mp3"));
                return adminAudios;
            } catch (URISyntaxException e) {
                //:)
            }
            return new ArrayList<>();
        }

        public static ArrayList<Audio> getVendorMusics() {
            if(vendorAudios != null)
                return vendorAudios;
            try {
                vendorAudios = new ArrayList<>();
                vendorAudios.add(makeAudio("Benyamin Bahadori", "Bi Etena", "Vendor","Benyamin Bahadori - Bi Etena.mp3"));
                vendorAudios.add(makeAudio("Shadmehr Aghili", "Alamate Soal", "Vendor","Shadmehr-Aghili-Alamate-Soal.mp3"));
                return vendorAudios;
            } catch (URISyntaxException e) {
                //:)
            }
            return new ArrayList<>();
        }
        public static ArrayList<Audio> getCustomerMusics() {
            if(customerAudios != null)
                return customerAudios;
            try {
                customerAudios = new ArrayList<>();
                customerAudios.add(makeAudio("Shayea & Hidden", "Mosser", "Customer","Shayea-Mosser_FT_Mehrad_Hidden.mp3"));
                customerAudios.add(makeAudio("Imagine Dragons", "Believer", "Customer","Imagine Dragons - Believer.mp3"));
                customerAudios.add(makeAudio("Nico Vega", "Beast", "Customer","Beast (Extended Version).mp3"));
                return customerAudios;
            } catch (URISyntaxException e) {
                //:)
            }
            return new ArrayList<>();
        }

        private static Audio makeAudio(String artist, String name, String type, String fileName) throws URISyntaxException {
            return new Audio(artist, name, new MediaPlayer(
                    new Media(Main.class.getResource("Original SoundTracks\\" + type + "\\" + fileName).toURI().toString())));
        }

        public String getName() {
            return name;
        }

        public MediaPlayer getMusic() {
            return music;
        }

        public void stop() {
            music.stop();
        }

        public String getArtist() {
            return artist;
        }
    }

    public static String getCurrentLogID() {
        return currentLogID;
    }

    public static void setCurrentLogID(String currentLogID) {
        AccountControl.currentLogID = currentLogID;
    }

    public Account getAccount() {
        try {
            return AccountTable.getAccountByUsername(Control.getUsername());
        } catch (Exception e) {
            //:)
            return new Account();
        }
    }

    public Account getAccountByUsername(String username){
        try {
            return AccountTable.getAccountByUsername(username);
        } catch (Exception e) {
            //:)
            return new Account();
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
            if(isNewValueValid(fieldName, newValue)) {
                AccountTable.editField(Control.getUsername(), fieldName, newValue);
                return Notification.EDIT_FIELD_SUCCESSFULLY;
            } else {
                return InvalidField(fieldName, newValue);
            }
        } catch (Exception e) {
            //:)
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

    public static AccountControl getController() {
        if (customerControl == null)
            customerControl = new AccountControl();
        return customerControl;
    }

    public ArrayList<Account> getAllAccounts() {
        try {
            return AccountTable.getAllAccounts();
        } catch (SQLException e) {
            //:)
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            //:)
            return new ArrayList<>();
        }
    }

    public Notification deleteUserWithUsername(String username){
        try {
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
                ProductTable.removeAllUserComments(username);
                ProductTable.removeAllUserScores(username);
                CartTable.removeAllCustomerCartProducts(username);
            }
            AccountTable.deleteUserWithUsername(username);
            AccountTable.deleteProfileImage(username);
            return Notification.DELETE_USER;
        } catch (SQLException e) {
           return Notification.UNKNOWN_ERROR;
        } catch (ClassNotFoundException e) {
            return Notification.UNKNOWN_ERROR;
        }
    }

    public Off getOffByID(String offID) {
        try {
            Off off = OffTable.getSpecificOff(offID);
            return off;
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new Off();
    }

    public boolean isThereOffInEditingTable(String offID) {
        try {
            return OffTable.isThereEditingOffWithID(offID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return false;
    }

    public Off getOffFromEditingTable(String offID) {
        try {
            return OffTable.getSpecificEditingOff(offID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return null;
    }

    public Off getVendorOff(String offID) {
        try {
            if(OffTable.isThereEditingOffWithID(offID))
                return OffTable.getSpecificEditingOff(offID);
            return OffTable.getSpecificOff(offID);
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new Off();
    }

    public Log.ProductOfLog getProductOfLog(String productID){
        try {
            for (Log.ProductOfLog productOfLog : LogTable.getCustomerLogByID(getCurrentLogID()).getAllProducts()) {
                if (productID.equals(productOfLog.getProductID()))
                    return productOfLog;
            }
        } catch (SQLException e) {
            //:)
        } catch (ClassNotFoundException e) {
            //:)
        }
        return new Log.ProductOfLog();
    }

    public ArrayList<Account> getModifiedAccounts(Account.AccountType accountType, String... searchs) {
        if(searchs == null || searchs.length == 0) {
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
                //:)
            } catch (ClassNotFoundException e) {
                //:)
            }
            return new ArrayList<>();
        } else {
            ArrayList<Account> accounts = getModifiedAccounts(accountType);
            accounts.removeIf(account -> {
                boolean result = true;
                for (int i = 0; i < searchs.length; i++) {
                    if(account.getUsername().contains(searchs[i])) {
                        result = false;
                    }
                }
                return result;
            });
            return accounts;
        }
    }

    public Image getProfileImageByUsername(String username) {
        try {
            if(doesUserHaveImage(username))
            {
                FileInputStream fileInputStream = AccountTable.getProfileImageInputStream(username);
                Image image = new Image(fileInputStream);
                fileInputStream.close();
                return image;
            }
            FileInputStream fileInputStream = AccountTable.getProfileImageInputStream("1");
            Image image = new Image(fileInputStream);
            fileInputStream.close();
            return image;
        } catch (FileNotFoundException e) {
            //:)
        } catch (IOException e) {
            //:)
        }
        return null;
    }

    public boolean doesUserHaveImage(String username) {
        return AccountTable.getUserImageFilePath(username) != null;
    }

    public void setAccountPicture(String username, File pictureFile) {
        if(pictureFile == null) {
            if(doesUserHaveImage(username))
                AccountTable.deleteProfileImage(username);
        } else {
            if(doesUserHaveImage(username)) {
                AccountTable.deleteProfileImage(username);
            }
            try {
                AccountTable.setProfileImage(username, pictureFile);
            } catch (IOException e) {
                //:)
            }
        }

    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public void setMusicPlaying(boolean musicPlaying) {
        isMusicPlaying = musicPlaying;
    }

    public boolean modifyPlayingMusic() {
        if(!isMusicPlaying) {
            audios.get((int) (musicCounter % audios.size())).getMusic().play();
        } else {
            audios.get((int) (musicCounter % audios.size())).getMusic().pause();
        }
        isMusicPlaying = !isMusicPlaying;
        return isMusicPlaying;
    }

    public Audio getPlayingMusic() {
        return audios.get((int) (musicCounter % audios.size()));
    }

    public long getMusicCounter() {
        return musicCounter;
    }

    public void setMusicCounter(long musicCounter) {
        this.musicCounter = musicCounter;
    }
}
