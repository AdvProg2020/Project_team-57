package controller.user;

import controller.Control;
import notification.Notification;

public class UserControl extends Control {
    protected String name;
    private static UserControl userControl;


    public static UserControl getInstance(){
        if(userControl == null)
            return new UserControl();

        return UserControl.userControl;
    }
}