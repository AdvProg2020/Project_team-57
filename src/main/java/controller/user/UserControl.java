package controller.user;

import controller.Control;

public class UserControl extends Control {
    private static UserControl userControl;

    public static UserControl getInstance(){
        if(userControl == null)
            return new UserControl();

        return UserControl.userControl;
    }
}
