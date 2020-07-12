package server.server;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import notification.Notification;
import server.server.handler.AccountHandler;
import server.server.handler.PictureHandler;
import server.server.handler.ProductHandler;
import server.server.handler.SaleHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Server {
    public static int SERVER_PORT;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private Gson gson;
    private HashMap<String, String> authTokens;

    public Server() {
        try {
            serverSocket = new ServerSocket(0);
            SERVER_PORT = serverSocket.getLocalPort();
            System.out.println("PORT: " + SERVER_PORT);
            mapper = new ObjectMapper();
            this.authTokens = new HashMap<>();
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
            switch (type) {
                case ACCOUNT:
                    new AccountHandler(outStream, inStream, this, input).start();
                    break;
                case SALE:
                    new SaleHandler(outStream, inStream, this, input).start();
                    break;
                case PRODUCT:
                    new ProductHandler(outStream, inStream, this, input).start();
                    break;
                case PICTURE_SEND:
                case PICTURE_GET:
                    new PictureHandler(outStream, inStream, this, input, type).start();
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

    public HashMap<String, String> getAuthTokens() {
        return authTokens;
    }

    public void addAuth(String authToken, String username) {
        authTokens.put(authToken, username);
    }

    public boolean removeAuth(String authToken) {
        boolean answer = authTokens.containsKey(authToken);
        if(authTokens.containsKey(authToken)) {
            authTokens.remove(authToken);
        } else {
            System.err.println("Shit. Error In Removing AuthToken");
        }
        return answer;
    }

    public String makeAuth() {
        String auth;
        do {
            auth = generateRandomAuth();
        } while (authTokens.containsKey(auth));
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

    public String getUsernameByAuth (String auth) {
        if(authTokens.containsKey(auth)) {
            return authTokens.get(auth);
        } else {
            System.err.println("Shit. Error IN Get Username");
        }

        return null;
    }
}
