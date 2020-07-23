package server.model.existence;

public class Message {
    private final String message;
    private final String contactUsername;
    private final boolean endAlert;
    private final String senderName;

    public Message(String message, String contactUsername, boolean endAlert, String senderName) {
        this.message = message;
        this.contactUsername = contactUsername;
        this.endAlert = endAlert;
        this.senderName = senderName;
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
