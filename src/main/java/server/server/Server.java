package server.server;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import notification.Notification;
import server.server.handler.AccountHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
    public static int SERVER_PORT;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private Gson gson;
    private ArrayList<String> authTokens;

    public Server() {
        try {
            serverSocket = new ServerSocket(0);
            SERVER_PORT = serverSocket.getLocalPort();
            System.out.println(SERVER_PORT);
            mapper = new ObjectMapper();
            this.authTokens = new ArrayList<>();
            gson = new GsonBuilder().setPrettyPrinting().create();
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        while (true) {
            System.out.println("Server Listening...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client Accepted");
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            String input = inStream.readUTF();
            ObjectNode objectNode = mapper.readValue(input, ObjectNode.class);
            Command.HandleType type = gson.fromJson(objectNode.get("type").asText(), Command.HandleType.class);

            System.out.println("Type : " + type);
            switch (type) {
                case ACCOUNT:
                    new AccountHandler(outStream, inStream, this, input).start();
                    break;
                default:
                    outStream.writeUTF(getUnknownError());
                    outStream.flush();
            }
        }
    }

    public String getUnknownError() {
        Response response = new Response(Notification.UNKNOWN_ERROR);
        return gson.toJson(response);
    }

    private String getMessage(String input) throws JsonProcessingException {
        ObjectNode objectNode = mapper.readValue(input, ObjectNode.class);
        return objectNode.get("message").asText();
    }

    public static void main(String[] args) {
        new Server();
    }

    public ArrayList<String> getAuthTokens() {
        return authTokens;
    }

    public void addAuth(String authToken) {
        this.authTokens.add(authToken);
    }

    public String makeAuth() {
        String auth;
        do {
            auth = generateRandomAuth();
        } while (authTokens.contains(auth));
        return auth;
    }

    public String generateRandomAuth (){
        StringBuilder ID = new StringBuilder();
        for(int i = 0; i < 12; ++i) {
            int x = (((int) (Math.random() * 1000000)) % 75) + 48;
            if(x == 92) {
                i--;
                continue;
            }
            ID.append(Character.valueOf((char)x));
        }
        return ID.toString();
    }
}
