package notification;

public enum Notification {
    REGISTER_SUCCESSFUL("Registered successfully"),
    LOGIN_SUCCESSFUL("Logged in successfully"),
    ERROR_USERNAME_LENGTH("Username must contain 6 or more characters!"),
    ERROR_USERNAME_FORMAT("Username must contain only word characters!"),
    ERROR_FREE_USERNAME("Username doesn't exists"),
    ERROR_PASSWORD_LENGTH("Password must contain 8 to 16 characters!"),
    ERROR_PASSWORD_FORMAT("Password must include both numbers and letters only"),
    ERROR_UNKNOWN_ERROR("An unknown error occured"),
    INVALID_USERNAME("Username is invalid"),
    INVALID_PASSWORD("Password is invalid"),
    WRONG_PASSWORD("WRONG PASSWORD!"),
    COMMAND_ERROR("Error has occured");

    private String message;

    Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
