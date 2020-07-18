package server.server.sapahbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.server.RandomGenerator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionalInterface
interface SapahFunction {
    String apply(String... args);
}

public class SapahServer {
    public static int SERVER_PORT;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private Gson gson;
    private ArrayList<String> authTokens;

    public SapahServer() {
        try {
            serverSocket = new ServerSocket(0);
            SERVER_PORT = serverSocket.getLocalPort();
            System.out.println("PORT: " + SERVER_PORT);
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
            System.out.println("Sapah Listening...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client Accepted");
            new BankHandler(clientSocket, this).start();
        }
    }

    public static void main(String[] args) {
        new SapahServer();
    }

    private static class BankHandler extends Thread implements RandomGenerator {
        private Socket client;
        private DataOutputStream outStream;
        private DataInputStream inStream;
        private Gson gson;
        private SapahServer server;
        private HashMap<String, SapahFunction> functions;

        public BankHandler(Socket client, SapahServer server) throws IOException {
            this.client = client;
            this.outStream = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            this.inStream = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            this.gson = new GsonBuilder().setPrettyPrinting().create();
            this.server = server;
            initFunctions();
        }

        private void initFunctions() {
            functions = new HashMap<>();
            functions.put("^create_account (\\S){1,25} (\\S){1,25} (\\S){1,16} (\\S){1,16} (\\S){1,16}$", args -> createAccount(args[0], args[1], args[2], args[3], args[4]));
        }


        private String createAccount(String firstName, String lastName, String username, String password, String repeatPassword) {
            try {
                if(SapahDB.isThereAccountWithUsername(username))
                    return "username is not available";
                if(!password.equals(repeatPassword))
                    return "passwords do not match";

                String accountID = generateRandomNumber(10, s -> {
                    try {
                        return SapahDB.isThereAccountWithID(s);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                });

                SapahDB.createAccount(accountID, firstName, lastName, username, password);
                return accountID;
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public void run() {
            try {
                while(true) {
                    String command = inStream.readUTF().trim();
                    for (String regex : functions.keySet()) {
                        Matcher matcher = getMatcher(regex, command);
                        if(matcher.matches()) {
                            outStream.writeUTF(functions.get(regex).apply(getMatcherSet(matcher)));
                            outStream.flush();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private Matcher getMatcher(String regex, String command) {
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(command);
        }

        private String[] getMatcherSet(Matcher matcher) {
            String[] args = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                args[i] = matcher.group(i+1);
            }
            return args;
        }
    }
}
