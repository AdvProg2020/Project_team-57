package server.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.controller.account.AccountControl;
import server.model.existence.Message;
import server.server.handler.Handler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private ServerSocket serverSocket;
    private Gson gson;
    private boolean isChatOpen;

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(0);
        gson = new GsonBuilder().setPrettyPrinting().create();

    }

    public void run() {
        Thread clientThread = null, guiderThread = null;
        while (isChatOpen()) {
            if (clientThread == null || !clientThread.isAlive()) {
                clientThread = new Thread(() -> {
                    try {
                        String clientMessage = inStream.readUTF();
                        Message message = new Gson().fromJson(clientMessage, Message.class);
                        if (message.isEndAlert()) {
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
                        Message message = new Gson().fromJson(guiderMessage, Message.class);
                        if (message.isEndAlert()) {
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
}
