package controller.account;

public class Admin extends AccountControl{
    private static Admin adminControl = null;

    public static Admin getController() {
        if (adminControl == null)
            adminControl = new Admin();

        return adminControl;
    }

}
