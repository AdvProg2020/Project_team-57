package server.controller;

public enum Lock {
    IO_LOCK, USER_IMAGE_LOCK, SUPPORTER_LOCK,
    CATEGORY_LOCK, COMMENT_SCORE_LOCK, EDITING_PRODUCT_LOCK,
    WAGE_LOCK, MINIMUM_WALLET_LOCK,
    DISCOUNT_LOCK, OFF_LOCK, OFF_IMAGE_LOCK, EDITING_OFF_IMAGE_LOCK,
    PURCHASE_LOCK, LOG_LOCK,
    ADMIN_MODIFY_PRODUCT_LOCK, ADMIN_MODIFY_EDIT_PRODUCT_LOCK,
    PRODUCT_FILE_LOCK, EDITING_PRODUCT_FILE_LOCK,
    PRODUCT_FILE_INFO_LOCK, EDITING_PRODUCT_FILE_INFO_LOCK,
    PRODUCT_IMAGE_LOCK, EDITING_PRODUCT_IMAGE_LOCK, CHAT_LOCK,

    //SapahBank
    ACCOUNT_LOCK, TRANSACTION_LOCK
}
