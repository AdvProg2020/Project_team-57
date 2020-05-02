package controller.account;

public interface ValidPassword {

    default boolean isPasswordValid(String password) {

        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') &&
                    !(c >= '0' && c <= '9') && (c != '_') && (c != '-'))
                return false;
        }
        boolean flag = false;
        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);
            if (c >= '0' && c <= '9') {
                flag = true;
                break;
            }
        }
        if (!flag)
            return false;
        flag = false;
        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                flag = true;
                break;
            }
        }
        if (!flag)
            return false;
        return true;
    }
}
