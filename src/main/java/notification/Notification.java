package notification;

public enum Notification {
    REGISTER_SUCCESSFUL("Registered successfully"),
    LOGIN_SUCCESSFUL("Logged in successfully"),
    ERROR_USERNAME_LENGTH("Username must contain 6 to 16 characters!"),
    ERROR_USERNAME_FORMAT("Username must contain only word characters!"),
    ERROR_FREE_USERNAME("Username doesn't exists"),
    ERROR_PASSWORD_LENGTH("Password must contain 8 to 16 characters!"),
    ERROR_PASSWORD_FORMAT("Password contains some illegal characters"),
    UNKNOWN_ERROR("An unknown error occured"),
    WRONG_PASSWORD("WRONG PASSWORD!"),
    SAME_PASSWORD_ERROR("What are you doing??\uD83D\uDE15\nPasswords are identical"),
    CHANGE_PASSWORD_SUCCESSFULLY("Password was changed successfully\uD83D\uDE10"),
    SAME_FIELD_ERROR("Are you ok???\uD83D\uDE10\nEntered value is identical with initial value"),
    EDIT_FIELD_SUCCESSFULLY("Value edited successfully\uD83E\uDD29"),
    RISE_MONEY_SUCCESSFULLY("Inventory has increased!"),
    LACK_BALANCE_ERROR("Anything else???\uD83D\uDE12\nInventory is not enough!"),
    GET_MONEY_SUCCESSFULLY("Money has got successfully!");

    private String message;

    Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
