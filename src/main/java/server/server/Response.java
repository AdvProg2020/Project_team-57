package server.server;

import notification.Notification;

import java.util.Arrays;
import java.util.List;

public class Response<T> {
    private Notification message;
    private List<T> data;

    @SafeVarargs
    public Response(Notification message, T... data) {
        this.message = message;
        this.data = Arrays.asList(data);
    }

    public Response() { }


    public Notification getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }

}
