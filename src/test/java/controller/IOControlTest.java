package controller;

import model.existence.Account;
import notification.Notification;
import org.junit.Assert;
import org.junit.Test;

public class IOControlTest {

    @Test
    public void registerTestSuccessful() {
        IOControl ioControl = IOControl.getController();
        Account account = new Account("Username1", "a1234567");
        account.setFirstName("Sapah");
        account.setLastName("Sapah");
        account.setType("Admin");
        Assert.assertEquals(Notification.REGISTER_SUCCESSFUL, ioControl.register(account));
    }

    @Test
    public void registerTestInvalidPassword() {
        IOControl ioControl = IOControl.getController();
        Account account = new Account("Username1", "11234567");
        account.setFirstName("Sapah");
        account.setLastName("Sapah");
        account.setType("Admin");
        Assert.assertEquals(Notification.ERROR_PASSWORD_FORMAT, ioControl.register(account));
    }

    @Test
    public void registerTestInvalidUsername() {
        IOControl ioControl = IOControl.getController();
        Account account = new Account("Username(1)", "a1234567");
        account.setFirstName("Sapah");
        account.setLastName("Sapah");
        account.setType("Admin");
        Assert.assertEquals(Notification.ERROR_USERNAME_FORMAT, ioControl.register(account));
    }

    @Test
    public void registerTestUsernamePasswordLength() {
        IOControl ioControl = IOControl.getController();
        Account account = new Account("User", "a123");
        account.setFirstName("Sapah");
        account.setLastName("Sapah");
        account.setType("Admin");
        Assert.assertEquals(Notification.ERROR_USERNAME_LENGTH, ioControl.register(account));

        account.setUsername("Username1");
        Assert.assertEquals(Notification.ERROR_PASSWORD_LENGTH, ioControl.register(account));
    }

    @Test
    public void registerTestFirstNameLastNameLength() {
        IOControl ioControl = IOControl.getController();
        Account account = new Account("Username1", "a1234567");
        account.setFirstName("SapahSapahSapahSapahSapahSapahSapahSapahSapah");
        account.setLastName("SapahSapahSapahSapahSapahSapahSapahSapahSapah");
        account.setType("Admin");
        Assert.assertEquals(Notification.ERROR_FIRST_NAME_LENGTH, ioControl.register(account));

        account.setFirstName("Sepanta");
        Assert.assertEquals(Notification.ERROR_LAST_NAME_LENGTH, ioControl.register(account));
    }
}
