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
            "\nAlso, Your Sort Is Done"),
    DELETED_DISCOUNT("Successfully Deleted The Discount Code.\nToss A Coin To Your Witcher O' Valley Of Plenty. \uD83E\uDD11"),
    ADD_DISCOUNT("Remember! \nNowadays, Customers Will Be Absorbed By Discountable Admins"),
    EMPTY_DISCOUNT_CODE("Are You Crazy With Discount Code ?!!!! \uD83D\uDE21 \nHow Are Customers Supposed To Get That Fucking Discount ?!!! \uD83D\uDE21"),
    EMPTY_DISCOUNT_START_DATE("Congratulations !!!! \uD83D\uDE10 \nYour Discount Code Won't Start In Our Universe. \uD83D\uDE10"),
    EMPTY_DISCOUNT_FINISH_DATE("Congratulations !!!! \uD83D\uDE10 \nYour Discount Code Won't End In Our Universe. \uD83D\uDE10 \nEnter The Finish Date \uD83D\uDE10"),
    EMPTY_DISCOUNT_PERCENT("Do You Call YourSelf An Admin ?!!! \uD83E\uDD28 \nYour Discount Code Doesn't Have A Discount Percent. \uD83D\uDE11"),
    EMPTY_MAX_DISCOUNT("Do You Think I'll Think You Will Be That Generous That You Won't Enter The Max Discount? \uD83E\uDD28 \nStop This Fucking Feint Show And Enter The Fucking Max Discount. \uD83D\uDE12"),
    EMPTY_DISCOUNT_MAX_REPETITION("Do You Think I'll Think You Will Be That Generous That You Won't Enter The Max Repetition? \uD83E\uDD28 \nStop This Fucking Feint Show And Enter The Fucking Max Repetition. \uD83D\uDE12"),
    EMPTY_DISCOUNT_CUSTOMERS_LIST("You Want to Create Discount Without Adding Customers In It ? \nGuessed So  \uD83D\uDE11 "),
    INVALID_DISCOUNT_CODE("Your Code Is Invalid. \nRemember! Its Length Must Be Between 6 And 16"),
    INVALID_FINISH_DATE_EARLIER_THAN_START_DATE("The Date You Entered Is Earlier Than The Starting Date Of Discount."),
    INVALID_FINISH_DATE_EARLIER_THAN_CURRENT_DATE("The Date You Entered Is Earlier Than The Current Date."),
    INVALID_DISCOUNT_PERCENT("Discount Percentage Must Be Between 0 And 100 Obviously \uD83D\uDE15"),
    INVALID_MAX_DISCOUNT("Max Discount Must Be A Positive Amount"),
    INVALID_MAX_REPETITION("Max Repetition Must Be A Positive Amount"),
    UNCOMPLETED_OFF_NAME("Name of Off is empty!"),
    NOT_SET_FINISH_DATE("Off Finish time must be set"),
    OUT_BOUND_OF_PERCENT("Percent must be between 0 to 100"),
    ADD_OFF("Off added successfully"),
    ACCEPT_OFF_REQUEST("Offs were applied successfully"),
    EMPTY_OFF_PRODUCTS("You haven't Entered Off Products"),
    DUPLICATE_OFF_VALUE("This Value Is Duplicate To The Last One. Choose Another One \uD83E\uDD10"),
    OFF_EDITED("You Successfully Modified Your Off Bro.\n" +
            "Isn't This The Best Store Management Program You've Ever Seen? \uD83D\uDE07"),
    WRONG_OFF_FINISH_DATE("Finish Date Must Be After Current And Off Start Date \uD83D\uDE12"),
    INVALID_OFF_PERCENT("Off Percentage Must Be Between 0 And 100 Obviously. \uD83D\uDE23"),
    OFF_REMOVED("-After All This Time Severus?\n+Always\n*Harry Potter And Deathly Hallows\n(Off Removed Successfully Too)"),
    OFF_EDITING_ACCEPTED("+I May Not Be A Smart Man, But I Know What Love Is.\n*Forrest Gump\n(You Accepted Editing Off) "),
    OFF_EDITING_DECLINED("+Sometimes This Program Makes Me Wonder, Which Would Be Worse,\n" +
            "To Live As A Monster Or Die As A Good Man.\n*Shutter Island\n" +
            "(You Successfully Declined Editing Off Request.)"),
    UNAVAILABLE_CART_PRODUCT("One Of Your Cart Products Is Not Approved Yet. "),
    CART_PRODUCT_OUT_OF_STOCK("One Of Your Cart Products Is Out Of Stock. "),
    CANT_AFFORD_CART("Come Back When You Have Some Money, Buddy. "),
    PURCHASED_SUCCESSFULLY("I Don't Know How I'm Going To Live With MySelf " +
            "\nIf I Don't Stay True To What I Believe. " +
            "\n+Desmond T.Doss (Hacksaw Ridge)" +
            "\nPurchased Successfully. 😐"),
    UPDATE_SCORE("Score updated successfully babe"),
    SET_SCORE("Score done"),
    ERROR_TITLE_LENGTH("Title length must be between 0 & 16 characters"),
    ERROR_COMMENT_LENGTH("Comment length must be between 0 & 100 characters"),
    ADD_COMMENT("Comment was sent to manager, wait for approve!!"),
    ACCEPTING_COMMENT("Comment added successfully"),
    DECLINE_COMMENT("Comment decline successfully"),
    PURCHASED_SUPERLY("Do You Have A Bed Somewhere?" +
            "\nWe Gave You A Super Discount With 85% Off." +
            "\nPurchased Successfully But Who Cares About That ?\uD83D\uDE0B"),
    PURCHASED_GOODELY("There Are No Two Words In English Language More Harmful Than GOOD JOB!" +
            "\n+Whiplash" +
            "\nPurchased Successfully \uD83D\uDE1C");



    private String message;

    Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
