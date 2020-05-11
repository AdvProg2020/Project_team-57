package notification;

public enum Notification {
    REGISTER_SUCCESSFUL("Registered successfully"),
    LOGIN_SUCCESSFUL("Logged in successfully"),
    ERROR_USERNAME_LENGTH("Username must contain 6 to 16 characters!"),
    ERROR_USERNAME_FORMAT("Username must contain only word characters!"),
    ERROR_FULL_USERNAME("Username Already Exists"),
    ERROR_FREE_USERNAME("This Username is not registered Yet"),
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
    GET_MONEY_SUCCESSFULLY("Money has got successfully!"),
    WRONG_OLD_PASSWORD("Old Password Incorrect"),
    REMOVE_PRODUCT_SUCCESSFULLY("Product removed successfully"),
    DECLINE_REQUEST("Request declined"),
    ACCEPT_ADD_VENDOR_REQUEST("The seller was added to the store"),
    USER_NOT_APPROVED("Wait for The Lord to Prove your Account, may the 4th be with you.\uD83D\uDE08"),
    PRODUCT_NOT_AVAILABLE("This Product is Unavailable. You can't Edit its Specs."),
    ADD_PRODUCT("Successfully Added. Hope You Will Have A Good Sale If The Lord Accepts your Product"),
    ACCEPT_ADDING_PRODUCT("Product added to store successfully \uD83D\uDC8B"),
    ACCEPT_EDITING_PRODUCT("Product Editing Accepted. \uD83E\uDD11"),
    DELETE_USER("User was deleted successfully"),
    MORE_THAN_INVENTORY_COUNTABLE("Dude where are you looking??\uD83D\uDE15\nThe number entered is greater than the inventory"),
    MORE_THAN_INVENTORY_UNCOUNTABLE("Dude where are you looking??😕The number entered is greater than the inventory"),
    NEGATIVE_NUMBER("Hey where are you going?\uD83D\uDE10 Enter a positive number"),
    ADD_TO_CART("Good added to cart successfully\uD83D\uDE09");

    private String message;

    Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
