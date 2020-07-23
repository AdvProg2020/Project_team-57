package server.server.bank.sapahbank;

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

    public SapahServer() {
        try {
            serverSocket = new ServerSocket(0);
            SERVER_PORT = serverSocket.getLocalPort();
            System.out.println("Sapah Server Started To Listen On Port: " + SERVER_PORT);
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
            new BankHandler(clientSocket).start();
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
        private HashMap<String, SapahFunction> functions;

        public BankHandler(Socket client) throws IOException {
            this.client = client;
            this.outStream = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            this.inStream = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            this.gson = new GsonBuilder().setPrettyPrinting().create();
            initFunctions();
        }

        private void initFunctions() {
            functions = new HashMap<>();
            functions.put("^create_account (\\S{1,25}) (\\S{1,25}) (\\S{1,16}) (\\S{1,16}) (\\S{1,16})$", args -> createAccount(args[0], args[1], args[2], args[3], args[4]));
            functions.put("^get_token (\\S{1,16}) (\\S{1,16})$", args -> getAuthToken(args[0], args[1]));
            functions.put("^create_receipt (\\S{1,20}) (\\S{1,16}) (\\S+) (\\S{1,16}) (\\S{1,16}) (.{1,100})$", args -> createReceipt(args[0], args[1], args[2], args[3], args[4], args[5]));
            functions.put("^get_transactions (\\S{1,20}) (\\S{1,15})$", args -> getTransactions(args[0], args[1]));
            functions.put("^pay (\\S{1,15})$", args -> payReceipt(args[0]));
            functions.put("^get_balance (\\S{1,25})$", args -> getBalance(args[0]));
        }

        private String getBalance(String auth) {
            try {
                if(!SapahDB.isThereAuth(auth))
                    return "token is invalid";
                if(isAuthExpired(auth))
                    return "token expired";
                return "" + SapahDB.getMoneyWithAccountID(SapahDB.getAccountIDWithAuth(auth));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return "database error";
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
                if(!SapahDB.isThereAccountWithUsername(username) || !SapahDB.getPasswordWithUsername(username).equals(password)) {
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
            return "database error";
        }

        private String createReceipt(String token, String receiptType, String money, String sourceID, String destID, String description) {
            try {
                if(!receiptType.equals("deposit") && !receiptType.equals("withdraw") && !receiptType.equals("move"))
                    return "invalid receipt type";

                if(!isMoneyValid(money))
                    return "invalid money";

                if(!SapahDB.isThereAuth(token))
                    return "token is invalid";
                if(!receiptType.equals("deposit") && !SapahDB.getAccountIDWithAuth(token).equals(sourceID))
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
                SapahDB.createReceipt(receiptID, receiptType, money, sourceID, destID, description);
                return receiptID;
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
                        receipts = SapahDB.getDestIDReceipts(SapahDB.getAccountIDWithAuth(token));
                        break;
                    case "-":
                        receipts = SapahDB.getSourceIDReceipts(SapahDB.getAccountIDWithAuth(token));
                        break;
                    case "*":
                        receipts = SapahDB.getSourceIDReceipts(SapahDB.getAccountIDWithAuth(token));
                        receipts.addAll(SapahDB.getDestIDReceipts(SapahDB.getAccountIDWithAuth(token)));
                        break;
                    default:
                        if(!SapahDB.isThereReceiptWithID(type))
                            return "invalid receipt id";
                        receipts = new ArrayList<>();
                        receipts.add(SapahDB.getReceiptWithID(type));
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

        private String payReceipt(String receiptID) {
            try {
                if(!SapahDB.isThereReceiptWithID(receiptID))
                    return "invalid receipt id";

                if(SapahDB.isReceiptPaid(receiptID))
                    return "receipt is paid before";

                Receipt receipt = SapahDB.getReceiptWithID(receiptID);

                if(!receipt.getType().equals("deposit") && receipt.getMoney() > SapahDB.getMoneyWithAccountID(receipt.getSource()))
                    return "source account does not have enough money";

                if((receipt.getType().equals("deposit") && !SapahDB.isAccountIDValid(receipt.getDestination())) || receipt.getType().equals("withdraw") && !SapahDB.isAccountIDValid(receipt.getSource()))
                    return "invalid account id";
                if(receipt.getType().equals("move") && (!SapahDB.isAccountIDValid(receipt.getSource()) || !SapahDB.isAccountIDValid(receipt.getDestination())))
                    return "invalid account id";

                switch (receipt.getType()) {
                    case "deposit":
                        SapahDB.addMoney(receipt.getDestination(), receipt.getMoney());
                        break;
                    case "withdraw":
                        SapahDB.subtractMoney(receipt.getSource(), receipt.getMoney());
                        break;
                    case "move":
                        SapahDB.addMoney(receipt.getDestination(), receipt.getMoney());
                        SapahDB.subtractMoney(receipt.getSource(), receipt.getMoney());
                        break;
                    default:
                        System.err.println("Error In Pay Receipt. #Type");
                }

                return "done successfully";
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
                            System.out.println("Client Asked: " + command);
                            outStream.writeUTF(functions.get(regex).apply(getMatcherSet(matcher)));
                            outStream.flush();
                            System.out.println(new java.util.Date());
                            continue Jesus;
                        }
                    }
                    if(command.equals("exit")) {
                        System.out.println("Client Asked: " + command);
                        System.out.println(new java.util.Date());
                        break;
                    }
                    outStream.writeUTF("invalid input");
                    System.err.println("Client Asked: Invalid Command");
                    outStream.flush();
                    System.out.println(new java.util.Date());
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
