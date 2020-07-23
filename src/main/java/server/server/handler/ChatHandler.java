package server.server.handler;

import com.google.gson.Gson;
import server.controller.account.AccountControl;
import server.server.Server;

import java.io.*;
import java.net.Socket;

public class ChatHandler extends Handler {
    private final DataInputStream guiderInput;
    private final DataOutputStream guiderOutput;
    private boolean isChatOpen;

    public ChatHandler(Socket clientSocket, Socket guideSocket, Server server, String input) throws IOException {
        super(new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))
                , new DataInputStream(new BufferedInputStream(clientSocket.getInputStream())),
                server,
                input, clientSocket);
        this.guiderOutput = new DataOutputStream(new BufferedOutputStream(guideSocket.getOutputStream()));
        this.guiderInput = new DataInputStream(new BufferedInputStream(guideSocket.getInputStream()));
        this.isChatOpen = true;
    }

    @Override
    protected String handle() {
        return null;
    }

    @Override
    public void run() {
        Thread clientThread = null, guiderThread = null;
        while (isChatOpen()) {
            if (clientThread == null || !clientThread.isAlive()) {
                clientThread = new Thread(() -> {
                    try {
                        String clientMessage = inStream.readUTF();
                        ChatHandler.Message message = new Gson().fromJson(clientMessage, ChatHandler.Message.class);
                        if (!message.getAlert().equals("send message")) {
                            setChatOpen(false);
                        }
                        sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                clientThread.start();
            }
            if (guiderThread == null || !guiderThread.isAlive()) {
                guiderThread = new Thread(() -> {
                    try {
                        String guiderMessage = guiderInput.readUTF();
                        ChatHandler.Message message = new Gson().fromJson(guiderMessage, ChatHandler.Message.class);
                        if (!message.getAlert().equals("send message")) {
                            setChatOpen(false);
                        }
                        sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                guiderThread.start();
            }
        }
    }

    private void sendMessage(Message message) throws IOException {
        if (AccountControl.getController().getAccountByUsername(message.getSenderName()).getType().equals("Customer")) {
            guiderOutput.writeUTF(new Gson().toJson(message));
            guiderOutput.flush();
        } else if (AccountControl.getController().getAccountByUsername(message.getSenderName()).getType().equals("Supporter")) {
            outStream.writeUTF(new Gson().toJson(message));
            outStream.flush();
        }
    }

    public boolean isChatOpen() {
        return isChatOpen;
    }

    public void setChatOpen(boolean chatOpen) {
        isChatOpen = chatOpen;
    }

    public static class Message {
        private final String message;
        private final String alert;
        private final String senderName;

        public Message(String message, String alert, String senderName) {
            this.message = message;
            this.alert = alert;
            this.senderName = senderName;
        }

        public String getMessage() {
            return message;
        }

        public String getAlert() {
            return alert;
        }

        public String getSenderName() {
            return senderName;
        }

    }
}
