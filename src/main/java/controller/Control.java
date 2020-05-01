package controller;

public class Control {
    private static Control control = null;
    private static boolean isLoggedIn = false;
    private static String username;
    private static String type;


    public static Control getController(){
        if (control == null){
            control = new Control();
        }
        return control;
    }

    public static boolean isLoggedIn() {
        return Control.isLoggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Control.isLoggedIn = loggedIn;
    }

    public static String getUsername() {
        return Control.username;
    }

    public static void setUsername(String username) {
        Control.username = username;
    }

    public static String getType() {
        return Control.type;
    }

    public static void setType(String type) {
        Control.type = type;
    }
}
