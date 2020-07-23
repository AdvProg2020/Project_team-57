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
import java.util.ArrayList;
import java.util.HashMap;


public class Server implements RandomGenerator{
    public static int SERVER_PORT;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private Gson gson;
    private HashMap<String, String> authTokens;
    private HashMap<String, Property> relics;
    private HashMap<String, Clock> IPs;
    private ArrayList<String> bannedIPs;
    private static final long DOS_CHECK_PERIOD_MILLIS = 10000;
    private static final long DOS_CHECK_COUNTER = 100;

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
            IPs = new HashMap<>();
            bannedIPs = new ArrayList<>();
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ROME
    private void run() throws IOException {
        final Server server = this;
        System.out.println("Server Listening...");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            String clientIP = clientSocket.getInetAddress().getHostAddress();
            if(addToIPs(clientIP)) {
                clientSocket.close();
                continue;
            }
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
                        //:)
                    }
                }
            }.start();
        }
    }

    private boolean addToIPs(String clientIP) {
        if(bannedIPs.contains(clientIP))
            return true;
        if(IPs.containsKey(clientIP)) {
            if(IPs.get(clientIP).addToCounter() > DOS_CHECK_COUNTER) {
                bannedIPs.add(clientIP);
                IPs.remove(clientIP).disableClock();
                return true;
            }
        } else {
            IPs.put(clientIP, new Clock());
        }
        return false;
    }

    public String getUnknownError() {
        Response response = new Response(Notification.UNKNOWN_ERROR);
        return gson.toJson(response);
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
        String auth = generateRandomString(12, s -> authTokens.containsKey(s));
        return auth;
    }

    public void addRelic(String relic) {
        relics.put(relic, new Property());
    }

    public String makeRelic() {
        String relic = generateRandomString(12, s -> relics.containsKey(s));
        return relic;
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
        System.err.println("Command : " + bankCommand);
        return BankAPI.getInstance().postAndGet(bankCommand);
    }

    public String payReceipt(String receiptID) {
        String bankCommand = "pay " + receiptID;
        return BankAPI.getInstance().postAndGet(bankCommand);
    }

    private static class Clock {
        private int counter;
        private Thread clock;
        private boolean off;

        public Clock() {
            this.counter = 1;
            this.off = false;
            this.clock = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!off) {
                            Thread.sleep(DOS_CHECK_PERIOD_MILLIS);
                            counter = 1;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            clock.start();
        }

        private int addToCounter() {
            return (++counter);
        }

        private void disableClock() {
            this.off = true;
        }
    }
}
