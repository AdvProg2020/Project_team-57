package server.server;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import notification.Notification;
import server.model.existence.Account;
import server.server.bank.BankAPI;
import server.server.handler.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server {
    public static int SERVER_PORT;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private Gson gson;
    private HashMap<String, String> authTokens;
    private HashMap<String, Property> relics;

    public static final String MARKET_BANK_USERNAME = "boosmarket";
    public static final String MARKET_BANK_PASSWORD = "a1234567";
    public static final String MARKET_BANK_ACCOUNT_NUMBER = "10001";

    public Server() {
        try {
            serverSocket = new ServerSocket(0);
            SERVER_PORT = serverSocket.getLocalPort();
            System.out.println("PORT: " + SERVER_PORT);
            mapper = new ObjectMapper();
            this.authTokens = new HashMap<>();
            this.relics = new HashMap<>();
            gson = new GsonBuilder().setPrettyPrinting().create();
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        final Server server = this;
        while (true) {
            System.out.println("Server Listening...");
            Socket clientSocket = serverSocket.accept();
            new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("Client Accepted");
                        DataInputStream inStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                        String input = inStream.readUTF();
                        ObjectNode objectNode = mapper.readValue(input, ObjectNode.class);
                        Command.HandleType type = gson.fromJson(objectNode.get("type").asText(), Command.HandleType.class);
                        switch (type) {
                            case GENERAL:
                                new GeneralHandler(outStream, inStream, server, input).start();
                                break;
                            case ACCOUNT:
                                new AccountHandler(outStream, inStream, server, input).start();
                                break;
                            case SALE:
                                new SaleHandler(outStream, inStream, server, input).start();
                                break;
                            case PRODUCT:
                                new ProductHandler(outStream, inStream, server, input).start();
                                break;
                            case PICTURE_SEND:
                            case PICTURE_GET:
                                new PictureHandler(outStream, inStream, server, input, type).start();
                                break;
                            default:
                                outStream.writeUTF(getUnknownError());
                                outStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
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
            auth = generateRandomString();
        } while (authTokens.containsKey(auth));
        return auth;
    }

    public void addRelic(String relic) {
        relics.put(relic, new Property());
    }

    public String makeRelic() {
        String relic;
        do {
            relic = generateRandomString();
        }while (relics.containsKey(relic));
        return relic;
    }

    public String generateRandomString(){
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
        return "";
    }

    public Property getPropertyByRelic(String relic) {
        if(relics.containsKey(relic)) {
            return relics.get(relic);
        } else {
            System.err.println("Shit. Error IN Get Property");
        }
        return null;
    }


    //Bank Part
    public String getBankAuthToken(String username, String password) {
        String bankCommand = "get_token " + username + " " + password;
        return BankAPI.getInstance().postAndGet(bankCommand);
    }

    public String getReceipt(String token, String receiptType, String moneyString, String description, String... accountIDs) {
        String sourceID = null, destID = null;

        switch (receiptType) {
            case "deposit":
                sourceID = "-1";
                destID = accountIDs[0];
                break;
            case "withdraw":
                sourceID = accountIDs[0];
                destID = "-1";
                break;
            case "move":
                sourceID = accountIDs[0];
                destID = accountIDs[1];
                break;
            default:
                System.err.println("Error In #getReceipt. WrongType");
                return null;
        }

        String bankCommand = "create_receipt " + token + " " + receiptType + " " + moneyString + " " + sourceID + " " + destID + " " + description;
        return BankAPI.getInstance().postAndGet(bankCommand);
    }

    public String payReceipt(String receiptID) {
        String bankCommand = "pay " + receiptID;
        return BankAPI.getInstance().postAndGet(bankCommand);
    }
}
