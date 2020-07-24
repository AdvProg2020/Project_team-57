package server.model.existence;

public class Message {
    private String message;
    private String contactUsername;
    private boolean endAlert;
    private String senderName;

    public Message(String message, String contactUsername, boolean endAlert, String senderName) {
        this.message = message;
        this.contactUsername = contactUsername;
        this.endAlert = endAlert;
        this.senderName = senderName;
    }

    public Message(boolean endAlert) {
        this.endAlert = endAlert;
    }

    public Message() {
    }

    public String getContactUsername() {
        return contactUsername;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public boolean isEndAlert() {
        return endAlert;
    }

}
