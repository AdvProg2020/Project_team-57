package server.server;

import notification.Notification;

import java.util.Arrays;
import java.util.List;

public class Response<T> {
    private Notification message;
    private List<T> data;
    private String additionalString;

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

    public T getData(int i) {
        return data.get(i);
    }

    public T getDatum() {
        return data.get(0);
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setMessage(Notification message) {
        this.message = message;
    }

    public String getAdditionalString() {
        return additionalString;
    }

    public void setAdditionalString(String additionalString) {
        this.additionalString = additionalString;
    }

}
