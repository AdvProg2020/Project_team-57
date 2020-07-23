package server.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer {
    private ServerSocket serverSocket;
    private Gson gson;
    private HashMap<String, Socket> chatters;

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(0);
        System.out.println("Chat Server Started To Listen On Port: " + serverSocket.getLocalPort());
        chatters = new HashMap<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

/*    public void run() {
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
    }*/
}
