public enum Notification {
    REGISTER_SUCCESSFUL("Registered successfully"),
    LOGIN_SUCCESSFUL("Logged in successfully"),
    INVALID_USERNAME("Username is invalid"),
    INVALID_PASSWORD("Password is invalid"),
    WRONG_PASSWORD("WRONG PASSWORD!");

    private String message;

    Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
