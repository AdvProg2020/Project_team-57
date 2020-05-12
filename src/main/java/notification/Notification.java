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
    ADD_TO_CART("Good added to cart successfully\uD83D\uDE09"),
    INVALID_COUNT("Please Enter A Positive Integer Dude \uD83D\uDE12"),
    INVALID_AMOUNT("Please Enter A Positive Real Number Dawg \uD83D\uDE20"),
    INCREASED("Cart Product Increased \uD83D\uDE43"),
    MORE_THAN_CART_COUNTABLE("You Are Subtracting The Counts More Than Your Cart. Use The Remove Option If you Want."),
    DECREASED("Cart Product Decreased \uD83D\uDE2D"),
    MORE_THAN_CART_UNCOUNTABLE("You Are Subtracting The Amount More Than Or Equal To Your Cart.\nUse The Remove Option If you Want."),
    CART_PRODUCT_REMOVED("SuccessFully Deleted This Product From Your Cart.\nBut Don't Do That Again. Man Ghahram \uD83D\uDE2D\uD83D\uDE2D"),
    NOT_YOUR_CART_PRODUCT("Wait WTF \uD83D\uDE10 This is Product Isn't in your cart."),
    CATEGORY_ADDED("SuccessFully Added The Category."),
    DUPLICATE_CATEGORY_NAME("This Category Name Is Already Used. \uD83D\uDE36"),
    CATEGORY_DELETED("Category And It's Product Deleted In Whole. \uD83D\uDE0E"),
    CATEGORY_MODIFIED("Category Modified Successfuly."),
    CATEGORY_NOT_FOUND("Category With This Name Not Found. \uD83E\uDD2B"),
    PARENT_CATEGORY_NOT_FOUND("There Is No Parent Category With This Name. \uD83E\uDD2B"),
    INVALID_CATEGORY_NAME("Length Of Category Name Must Be Between 6 & 16."),
    INVALID_FEATURES("Length Of Category Features Must Be Less Than 100."),
    CATEGORY_FILTERED("Your Computer Just Hacked By A Russian Group." +
            "\nJust Kidding. Category Successfully Filtered \uD83D\uDE06"),
    NAME_FILTERED("Avoid Fast Food, It Can Cause Gastric Cancer.\nAnd Name Successfully Filtered \uD83D\uDE44"),
    CATEGORY_FILTER_DELETED("Brush Your Teeth Every Night.\nAnd Be Sure That Your Category Filter Is Deleted."),
    NAME_FILTER_DELETED("Once Upon A Time In WinterFall... O' Sorry\nYour Filtered Name Deleted Successfully."),
    NAME_FILTERED_DUPLICATE("You Already Filtered This Name Kiddo \uD83D\uDE44"),
    SORT_DISABLED("In A Wold Without Money Group 57 Would Have Been Heroes \uD83E\uDD25" +
            "\nYou Disabled The Current Sort Successfully"),
    SORTED("Have You Ever Seen John Cena? I Haven't \uD83D\uDE44" +
            "\nAlso, Your Sort Is Done");


    private String message;

    Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
