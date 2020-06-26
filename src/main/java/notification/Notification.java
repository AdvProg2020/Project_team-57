package notification;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public enum Notification {
    REGISTER_SUCCESSFUL("Registered successfully", Alert.AlertType.INFORMATION, "Successful", "Congratulations"),
    LOGIN_SUCCESSFUL("Logged in successfully", Alert.AlertType.INFORMATION, "Successful", "Congratulations"),
    ERROR_USERNAME_LENGTH("Username must contain 6 to 16 characters!", Alert.AlertType.ERROR, "Register Error", "Username Length Not Valid"),
    ERROR_USERNAME_FORMAT("Username must contain only word characters!", Alert.AlertType.ERROR, "Register Error", "Username Format Not Valid"),
    ERROR_FULL_USERNAME("Username Already Exists", Alert.AlertType.ERROR, "Register Error", "Full Username"),
    ERROR_FREE_USERNAME("This Username is not registered Yet", Alert.AlertType.ERROR, "Login Error", "Free Username"),
    ERROR_PASSWORD_LENGTH("Password must contain 8 to 16 characters!", Alert.AlertType.ERROR, "Register Error", "Password Length Not Valid"),
    ERROR_PASSWORD_LENGTH_EDIT("Password must contain 8 to 16 characters!", Alert.AlertType.ERROR, "Change Password Error", "New Password Length Not Valid"),
    ERROR_PASSWORD_FORMAT("Password's length must be between 8 and 16, and contain both letters and numbers", Alert.AlertType.ERROR, "Register Error", "Password Format Not Valid"),
    ERROR_PASSWORD_FORMAT_EDIT("Password's length must be between 8 and 16, and contain both letters and numbers", Alert.AlertType.ERROR, "Change Password Error", "New Password Format Not Valid"),
    UNKNOWN_ERROR("An unknown error occurred", Alert.AlertType.ERROR, "Unknown Error", "Shit!!!!"),
    WRONG_PASSWORD("WRONG PASSWORD!", Alert.AlertType.ERROR, "Login Error", "Wrong Password"),
    SAME_PASSWORD_ERROR("What are you doing??\uD83D\uDE15\nPasswords are identical",
            Alert.AlertType.ERROR, "Change Password Error", "Duplicate Password"),
    CHANGE_PASSWORD_SUCCESSFULLY("Password was changed successfully\uD83D\uDE10", Alert.AlertType.INFORMATION, "Change Successful", "Congratulations"),
    SAME_FIELD_ERROR("Are you ok???\uD83D\uDE10\nEntered value is identical with initial value", Alert.AlertType.ERROR, "Edit Account Error", "Duplicate Value"),
    EDIT_FIELD_SUCCESSFULLY("Value edited successfully\uD83E\uDD29", Alert.AlertType.INFORMATION, "Edit Successful", "Congratulations"),
    RISE_MONEY_SUCCESSFULLY("Inventory has increased! Be Joyful Man",
            Alert.AlertType.INFORMATION, "Adding Money Successful", "You Should Celebrate"),
    LACK_BALANCE_ERROR("Anything else???\uD83D\uDE12\nInventory is not enough!",
            Alert.AlertType.ERROR, "Subtracting Money Error", "Lack Of Balance, Bro"),
    GET_MONEY_SUCCESSFULLY("Money has got successfully! \nYou Have Reached Your Wishes?? Shop Lifter 😒😒",
            Alert.AlertType.INFORMATION, "Subtracting Money Successful", "Hey Rude"),
    WRONG_OLD_PASSWORD("You have Some Alzheimer Issue ?! \nYou have Entered Wrong Old Password",
            Alert.AlertType.ERROR, "Change Password Error", "Old Password Incorrect"),
    REMOVE_PRODUCT_SUCCESSFULLY("Product removed successfully", Alert.AlertType.INFORMATION, "Well Done 🙄", "Reached Your Desire? 😒"),
    DECLINE_REQUEST("Request declined"),
    ACCEPT_ADD_VENDOR_REQUEST("The seller was added to the store", Alert.AlertType.INFORMATION, "Successful", "Congratulations"),
    USER_NOT_APPROVED("Wait for The Lord to Prove your Account, may the 4th be with you.\uD83D\uDE08", Alert.AlertType.ERROR, "Login Error", "User Not Approved"),
    PRODUCT_NOT_AVAILABLE("This Product is Unavailable. You can't Edit its Specs."),
    ADD_PRODUCT("Successfully Added. Hope You Will Have A Good Sale If The Lord Accepts your Product",
            Alert.AlertType.INFORMATION, "Adding Product Successful", "Product Request"),
    ACCEPT_ADDING_PRODUCT("Product added to store successfully \uD83D\uDC8B"),
    ACCEPT_EDITING_PRODUCT("Product Editing Accepted. \uD83E\uDD11"),
    DELETE_USER("User was deleted successfully", Alert.AlertType.INFORMATION, "Successful", "Congratulations"),
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
    CART_PRODUCT_REMOVED("SuccessFully Deleted This Product From Your Cart.\nBut Don't Do That Again. Man Ghahram \uD83D\uDE2D\uD83D\uDE2D",
            Alert.AlertType.INFORMATION, "Cart Product Delete", "Deleted Successfully"),
    NOT_YOUR_CART_PRODUCT("Wait WTF \uD83D\uDE10 This is Product Isn't in your cart.",
            Alert.AlertType.ERROR, "Cart Product Delete", "Cart Product Delete Error"),
    CATEGORY_ADDED("SuccessFully Added The Category.", Alert.AlertType.INFORMATION, "Add Category Successful", "Congratulations"),
    DUPLICATE_CATEGORY_NAME("This Category Name Is Already Used. \uD83D\uDE36", Alert.AlertType.ERROR, "Edit Category Error", "Duplicate Name"),
    SAME_CATEGORY_FIELD_ERROR("Are you ok???\uD83D\uDE10\nEntered value is identical with initial value", Alert.AlertType.ERROR, "Edit Category Error", "Duplicate Value"),
    CATEGORY_DELETED("Category And It's Product Deleted In Whole. \uD83D\uDE0E", Alert.AlertType.INFORMATION,
            "Delete Successful", "Congratulations"),
    CATEGORY_MODIFIED("Category Modified Successfuly.", Alert.AlertType.INFORMATION, "Modify Successful", "Congratulations"),
    CATEGORY_NOT_FOUND("Do You Think This Category Exists ? \nCause, Category With This Name Not Found. \uD83E\uDD2B",
            Alert.AlertType.ERROR, "Remove Category Error", "Category Not Found"),
    PARENT_CATEGORY_NOT_FOUND("There Is No Parent Category With This Name. \uD83E\uDD2B", Alert.AlertType.ERROR, "Edit Category Error", "invalid Parent Name"),
    INVALID_CATEGORY_NAME("Length Of Category Name Must Be Between 6 & 16.", Alert.AlertType.ERROR, "Edit Category Error", "Invalid Name"),
    INVALID_FEATURES("Length Of Category Features Must Be Less Than 100.", Alert.AlertType.ERROR, "Edit Category Error", "Invalid Features"),
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
    ADD_DISCOUNT("Remember! \nNowadays, Customers Will Be Absorbed By Discountable Admins", Alert.AlertType.INFORMATION, "Discount Added Successfully", "Congratulations"),
    EMPTY_DISCOUNT_CODE("Are You Crazy With Discount Code ?!!!! \uD83D\uDE21 \nHow Are Customers Supposed To Get That Fucking Discount ?!!! \uD83D\uDE21",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Discount Code"),
    EMPTY_DISCOUNT_START_DATE("Congratulations !!!! \uD83D\uDE10 \nYour Discount Code Won't Start In Our Universe. \uD83D\uDE10",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Discount Start Date"),
    EMPTY_DISCOUNT_FINISH_DATE("Congratulations !!!! \uD83D\uDE10 \nYour Discount Code Won't End In Our Universe. \uD83D\uDE10 \nEnter The Finish Date \uD83D\uDE10",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Discount Finish Date"),
    EMPTY_DISCOUNT_PERCENT("Do You Call YourSelf An Admin ?!!! \uD83E\uDD28 \nYour Discount Code Doesn't Have A Discount Percent. \uD83D\uDE11",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Discount Percent"),
    EMPTY_MAX_DISCOUNT("Do You Think I'll Think You Will Be That Generous That You Won't Enter The Max Discount? \uD83E\uDD28 \nStop This Fucking Feint Show And Enter The Fucking Max Discount. \uD83D\uDE12",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Max Discount"),
    EMPTY_DISCOUNT_MAX_REPETITION("Do You Think I'll Think You Will Be That Generous That You Won't Enter The Max Repetition? \uD83E\uDD28 \nStop This Fucking Feint Show And Enter The Fucking Max Repetition. \uD83D\uDE12",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Max Repetition"),
    EMPTY_DISCOUNT_CUSTOMERS_LIST("You Want to Create Discount Without Adding Customers In It ? \nGuessed So  \uD83D\uDE11 ",
            Alert.AlertType.ERROR, "Add Discount Error", "Empty Discount Customers List"),
    INVALID_DISCOUNT_CODE("Your Code Is Invalid. \nRemember! Its Length Must Be Between 6 And 16"),
    INVALID_FINISH_DATE_EARLIER_THAN_START_DATE("The Date You Entered Is Earlier Than The Starting Date Of Discount.",
            Alert.AlertType.ERROR, "Edit Discount Error", "Invalid Finish Date"),
    INVALID_START_DATE("The Date You Entered Is Earlier Than The Current Date",
            Alert.AlertType.ERROR, "Add Discount Error", "Invalid Start Date"),
    INVALID_FINISH_DATE_FOR_START_DATE("The Finish Date Is Earlier Than The Starting Date Of Discount.",
            Alert.AlertType.ERROR, "Add Discount Error", "Invalid Finish Date"),
    INVALID_FINISH_DATE_EARLIER_THAN_CURRENT_DATE("The Date You Entered Is Earlier Than The Current Date."),
    INVALID_DISCOUNT_PERCENT("Discount Percentage Must Be Between 0 And 100 Obviously \uD83D\uDE15"),
    INVALID_MAX_DISCOUNT("Max Discount Must Be A Positive Amount"),
    INVALID_MAX_REPETITION("Max Repetition Must Be A Positive Amount"),
    UNCOMPLETED_OFF_NAME("Off Name Must Be Set!",
        Alert.AlertType.ERROR, "Add Off Error", "Invalid Name"),
    NOT_SET_FINISH_DATE("Off Finish time must be set",
            Alert.AlertType.ERROR, "Add Off Error", "Invalid Finish"),
    START_DATE_BEFORE_NOW("Off Start Date must be After Current Time",
            Alert.AlertType.ERROR, "Add Off Error", "Invalid Start"),
    START_DATE_AFTER_FINISH_DATE("Off Finish Date must be After Start Date",
            Alert.AlertType.ERROR, "Add Off Error", "Invalid Finish"),
    OUT_BOUND_OF_PERCENT("Percent must be between 0 to 100",
            Alert.AlertType.ERROR, "Add Off Error", "Invalid Percent"),
    ADD_OFF("Off Added Successfully. Wai For The A King Or Queen To Accept It.",
            Alert.AlertType.INFORMATION, "Add Off Successful", "Successfully added"),
    EDIT_OFF("Off Edited Successfully. Wai For The A King Or Queen To Accept It.",
            Alert.AlertType.INFORMATION, "Edit Off Successful", "Successfully Edited"),
    ACCEPT_OFF_REQUEST("Offs were applied successfully"),
    EMPTY_OFF_PRODUCTS("You haven't Entered Off Products",
            Alert.AlertType.ERROR, "Add Off Error", "No Off Product Found"),
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
    UNAVAILABLE_CART_PRODUCT("One Of Your Cart Products Is Not Approved Yet. ",
            Alert.AlertType.ERROR, "Purchase Error", "Unavailable Cart Product"),
    CART_PRODUCT_OUT_OF_STOCK("One Of Your Cart Products Is Out Of Stock. ",
            Alert.AlertType.ERROR, "Purchase Error", "Product Out Of Stock"),
    CANT_AFFORD_CART("Come Back When You Have Some Money, Buddy. ",
            Alert.AlertType.ERROR, "Purchase Error", "Can't Afford Cart"),
    PURCHASED_SUCCESSFULLY("I Don't Know How I'm Going To Live With MySelf " +
            "\nIf I Don't Stay True To What I Believe. " +
            "\n+Desmond T.Doss (Hacksaw Ridge)" +
            "\nPurchased Successfully. 😐",
            Alert.AlertType.INFORMATION, "Purchased", "Purchased Successfully"),
    UPDATE_SCORE("Score updated successfully babe"),
    SET_SCORE("Score done"),
    ERROR_TITLE_LENGTH("Title length must be between 0 & 16 characters"),
    ERROR_COMMENT_LENGTH("Comment length must be between 0 & 100 characters"),
    ADD_COMMENT("Comment was sent to manager, wait for approve!!"),
    ACCEPTING_COMMENT("Comment added successfully"),
    DECLINE_COMMENT("Comment decline successfully"),
    PURCHASED_SUPERBLY("Do You Have A Bed Somewhere?" +
            "\nWe Gave You A Super Discount With 85% Off." +
            "\nPurchased Successfully But Who Cares About That ?\uD83D\uDE0B",
            Alert.AlertType.INFORMATION, "Purchased", "Purchased Superbly"),
    PURCHASED_GOODLY("There Are No Two Words In English Language More Harmful Than GOOD JOB!" +
            "\n+Whiplash" +
            "\nPurchased Successfully \uD83D\uDE1C",
            Alert.AlertType.INFORMATION, "Purchased", "Purchased Goodly"),
    INVALID_MIN_PRICE("Minimum Price Must Be A Positive Number. "),
    MIN_PRICE_BIGGER_THAN_MAX_PRICE("Minimum Price Must Be Smaller Than Maximum Price. "),
    SET_PRICE_FILTERS("Price Filters Are Set Successfully. "),
    ERROR_FIRST_NAME_LENGTH("FirstName length must be less than 25 characters", Alert.AlertType.ERROR,
            "Register Error", "Invalid FirstName"),
    ERROR_LAST_NAME_LENGTH("LastName length must be less than 25 characters", Alert.AlertType.ERROR,
            "Register Error", "Invalid LastName"),
    ERROR_FIRST_NAME_LENGTH_EDIT("FirstName length must be less than 25 characters",
            Alert.AlertType.ERROR, "Edit Account Error", "Invalid FirstName"),
    ERROR_LAST_NAME_LENGTH_EDIT("LastName length must be less than 25 characters", Alert.AlertType.ERROR,
            "Edit Account Error", "Invalid LastName"),
    ERROR_EMAIL_LENGTH_EDIT("Email length must be less than 35 characters", Alert.AlertType.ERROR,
            "Edit Account Error", "Invalid Email"),
    ERROR_BRAND_LENGTH_EDIT("Brand length must be less than 35 characters", Alert.AlertType.ERROR,
            "Edit Account Error", "Invalid Brand"),
    EMPTY_OLD_PASSWORD("Hey You!!!! \nWhere Are You Going?      Enter Your Old Password", Alert.AlertType.ERROR,
            "Change Password Error", "Empty Old Password"),
    EMPTY_NEW_PASSWORD("Are You Dumb Or Something??? \nAre You Gonna Replace Your Password With Empty?? \nI Mean, Seriously???"
            , Alert.AlertType.ERROR, "Change Password Error", "Empty New Password"),
    INVALID_ADDING_DOUBLE_MONEY("Dude, Please Enter A Valid Money??? \nI Wanna Know Seriously With \nWhat Do You Get High With",
            Alert.AlertType.ERROR, "Add Money Error", "Invalid Adding Money"),
    INVALID_SUBTRACTING_DOUBLE_MONEY("Dude, Please Enter A Valid Money??? \nI Wanna Know Seriously With \nWhat Do You Get High With",
            Alert.AlertType.ERROR, "Subtract Money Error", "Invalid Subtracting Money"),
    EMPTY_FIRST_NAME_EDIT("Please Enter Some Non-Empty First Name, Dude. " +
            "\nWe Can't Have SomeOne Anonymous In Our Shop. \nIt's Not Safe!!!! 😱😱😱",
            Alert.AlertType.ERROR, "Edit Account Error", "Empty FirstName"),
    EMPTY_LAST_NAME_EDIT("Please Enter Some Non-Empty Last Name, Dude. " +
            "\nWe Can't Have SomeOne Anonymous In Our Shop. \nIt's Not Safe!!!! 😱😱😱",
            Alert.AlertType.ERROR, "Edit Account Error", "Empty LastName"),
    NOT_SELECTED_CATEGORY("You haven't selected Any Category", Alert.AlertType.ERROR, "Jesus", "Hey You"),
    CANT_DELETE_CATEGORY("You Can't Delete All Products Category. That's The Main Node Of All Categories Sire!", Alert.AlertType.ERROR, "Category Delete Failed", "Cant Delete All Products"),
    INVALID_DISCOUNT_CODE_LENGTH("Discount Code's Length Must Be Less Than 16.", Alert.AlertType.ERROR,
            "Add Discount Error", "Invalid Discount Code"),
    //Todo Setting Alerts
    //("You Have Entered An Invalid Category Name")
    EMPTY_PRODUCT_PRICE("Please Enter The Price",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EMPTY_PRODUCT_NAME("Please Enter The Name",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EMPTY_PRODUCT_CATEGORY("Please Enter The Category",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    INVALID_PRODUCT_CATEGORY("You Have Entered An Invalid Category Name",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EMPTY_PRODUCT_COUNT("Please Enter The Count",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EMPTY_PRODUCT_AMOUNT("Please Enter The Amount",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EMPTY_PRODUCT_BRAND("Please Enter The Brand",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EMPTY_PRODUCT_DESCRIPTION("Please Enter The Discription",
            Alert.AlertType.ERROR, "Product Request Error", "Product Request"),
    EDIT_PRODUCT("You Edited Your Product Successfully. Wait For The Lord's Approval",
            Alert.AlertType.INFORMATION, "Editing Product Successful", "Product Request"),
    DECLINE_EDITING_PRODUCT("Editing Off Requests Denied Successfully Great King.",
            Alert.AlertType.INFORMATION, "Editing Product Accepted", "Product Request");


    private String message;
    private Alert alert;

    Notification(String message) {
        this.message = message;
    }

    Notification(String message, Alert.AlertType alertType, String title, String header, ButtonType... buttonTypes) {
        Alert alert = new Alert(alertType, message, buttonTypes);
        alert.setTitle(title);
        alert.setHeaderText(header);
        this.alert = alert;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Alert getAlert() {
        return alert;
    }
}
