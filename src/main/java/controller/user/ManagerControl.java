package controller.user;

public class ManagerControl extends UserControl {
    private static ManagerControl managerControl;

    public static ManagerControl getInstance(){
        if(managerControl == null)
            return new ManagerControl();

        return ManagerControl.managerControl;
    }
}
