package server.server.sapahbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.server.RandomGenerator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public SapahServer() {
        try {
            serverSocket = new ServerSocket(0);
            SERVER_PORT = serverSocket.getLocalPort();
            System.out.println("PORT: " + SERVER_PORT);
            mapper = new ObjectMapper();
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
        private static final long EXPIRE_PERIOD = (long) 3.6e+6;
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
            functions.put("^get_token (\\S){1,16} (\\S){1,16}$", args -> getAuthToken(args[0], args[1]));
            functions.put("^create_receipt (\\S){1, 20} (\\S){1,16} (\\S+) (\\S){1,16} (\\S){1,16} (\\S){1,100}$", args -> createReceipt(args[0], args[1], args[2], args[3], args[4], args[5]));

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
            return "database error";
        }

        private String getAuthToken(String username, String password) {
            try {
                if(!SapahDB.isThereAccountWithUsername(username) || !SapahDB.getPasswordWithUsername(password).equals(password)) {
                    return "invalid username or password";
                }
                String authToken = generateRandomString(20, s -> {
                    try {
                        return SapahDB.isThereAuth(s);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                });
                SapahDB.addAuth(authToken, SapahDB.getAccountIDWithUsername(username));
                return authToken;
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

        private String createReceipt(String token, String receiptType, String money, String sourceID, String destID, String description) {
            try {
                if(!receiptType.equals("deposit") && !receiptType.equals("withdraw") && !receiptType.equals("move"))
                    return "invalid receipt type";

                if(!isMoneyValid(money))
                    return "invalid money";

                if(!SapahDB.isThereAuth(token))
                    return "token is invalid";
                if(receiptType.equals("withdraw") && !SapahDB.getAccountIDWithAuth(token).equals(sourceID))
                    return "token is invalid";
                if(isAuthExpired(token))
                    return "token expired";

                if(!sourceID.equals("-1") && !SapahDB.isAccountIDValid(sourceID))
                    return "source account id is invalid";

                if(!destID.equals("-1") && !SapahDB.isAccountIDValid(destID))
                    return "dest account id is invalid";

                if(sourceID.equals(destID))
                    return "equal source and dest account";

                if((receiptType.equals("deposit") && destID.equals("-1")) || receiptType.equals("withdraw") && sourceID.equals("-1"))
                    return "invalid account id";
                if(receiptType.equals("move") && (sourceID.equals("-1") || destID.equals("-1")))
                    return "invalid account id";

                if(description.contains("*"))
                    return "your input contains invalid characters";

                String receiptID = generateRandomNumber(10, s -> {
                    try {
                        return SapahDB.isThereReceiptWithID(s);
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                });
                SapahDB.createReceipt(receiptID, token, receiptType, money, sourceID, destID, description);

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return "database error";
        }

        private boolean isMoneyValid(String money) {
            if(getMatcher("^\\d+$", money).matches())
                return true;
            if(!money.contains("."))
                return false;

            boolean foundDot = false;
            int textSize = money.length();

            for (int i = 0; i < textSize; i++) {
                if(money.charAt(i) < 48 || money.charAt(i) > 57) {
                    if(money.charAt(i) == '.') {
                        if(foundDot) {
                            return false;
                        }
                        foundDot = true;
                    } else {
                        return false;
                    }
                }
            }

            return true;
        }

        private boolean isAuthExpired(String auth) throws SQLException, ClassNotFoundException {
            Date date = SapahDB.getAuthDateWithAuth(auth);
            return System.currentTimeMillis() - date.getTime() > EXPIRE_PERIOD;
        }

        private String getTransactions(String token, String type) {
            try {
                if(!SapahDB.isThereAuth(token))
                    return "invalid token";
                if(isAuthExpired(token))
                    return "token expired";

                ArrayList<Receipt> receipts;
                switch (type) {
                    case "+":
                        break;
                    case "-":
                        break;
                    case "*":
                        break;
                    default:
                        if(!SapahDB.isAccountIDValid(type))
                            return "invalid receipt id";

                }

                StringBuilder stringBuilder = new StringBuilder("");
                for (Receipt receipt : receipts) {
                    stringBuilder.append(gson.toJson(receipt) + "*");
                }
                if(!stringBuilder.toString().equals(""))
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                return stringBuilder.toString();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return "database error";
        }

        @Override
        public void run() {
            try {
                Jesus:
                while(true) {
                    String command = inStream.readUTF().trim();
                    for (String regex : functions.keySet()) {
                        Matcher matcher = getMatcher(regex, command);
                        if(matcher.matches()) {
                            outStream.writeUTF(functions.get(regex).apply(getMatcherSet(matcher)));
                            outStream.flush();
                            continue Jesus;
                        }
                    }
                    outStream.writeUTF("invalid input");
                    outStream.flush();
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
