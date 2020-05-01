package controller;

public class Control {
    private static Control control = null;
    protected boolean isLoggedIn = false;
    protected String username;
    protected String type;


    public static Control getController(){
        if (control == null){
            control = new Control();
        }
        return control;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
