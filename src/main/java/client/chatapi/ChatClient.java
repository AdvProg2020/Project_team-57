package client.chatapi;

import client.api.Command;
import client.view.ChatProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.model.existence.Message;
import server.server.Response;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final static String IP = "127.0.0.1";
    private static int PORT = 54476;
    private String auth;
    private String contactUsername;
    private Socket restlessSocket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private Gson gson;
    private ChatProcessor chatProcessor;

    public ChatClient(String auth, String contactUsername) {
        this.auth = auth;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            restlessSocket = new Socket(IP, PORT);
            if(contactUsername == null) {
                Command command = new Command("take me", Command.HandleType.ACCOUNT);
                command.setAuthToken(this.auth);
                post(gson.toJson(command));
                waitForContact();
            } else {
                Command<String> command = new Command<>("start chat", Command.HandleType.ACCOUNT, contactUsername);
                command.setAuthToken(this.auth);
                post(gson.toJson(command));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String post(String commandJson) throws IOException {
        outStream = new DataOutputStream(new BufferedOutputStream(restlessSocket.getOutputStream()));
        inStream = new DataInputStream(new BufferedInputStream(restlessSocket.getInputStream()));
        outStream.writeUTF(commandJson);
        outStream.flush();
        return inStream.readUTF();
    }

    public void setChatProcessor(ChatProcessor chatProcessor) {
        this.chatProcessor = chatProcessor;
    }

    private void waitForContact() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String json = inStream.readUTF();
                    Response<String> response = gson.fromJson(json, TypeToken.getParameterized(Response.class, (Class<String>)String.class).getType());
                    contactUsername = response.getDatum();
                    chatProcessor.startChat();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public String getContactUsername() {
        return contactUsername;
    }

    public void setContactUsername(String contactUsername) {
        this.contactUsername = contactUsername;
    }

    public void startListening() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String json = inStream.readUTF();
                        Message message = gson.fromJson(json, Message.class);
                        chatProcessor.writeMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
