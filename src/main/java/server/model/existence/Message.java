package server.model.existence;

public class Message {
    private final String message;
    private final boolean endAlert;
    private final String senderName;

    public Message(String message, boolean endAlert, String senderName) {
        this.message = message;
        this.endAlert = endAlert;
        this.senderName = senderName;
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
