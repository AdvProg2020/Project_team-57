package client.api;

import java.util.Arrays;
import java.util.List;

public class Command<T> {

    public static enum HandleType {
        ACCOUNT, PICTURE_SEND, PICTURE_GET,
    }

    private HandleType type;
    private String authToken;
    private String message;
    private List<T> data;

    public Command() {
    }

    @SafeVarargs
    public Command(String message, HandleType type, T... data) {
        this.message = message;
        this.type = type;
        this.data = Arrays.asList(data);
    }

    public Command(String authToken, String message, List<T> data) {
        this.authToken = authToken;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public HandleType getType() {
        return type;
    }
}
