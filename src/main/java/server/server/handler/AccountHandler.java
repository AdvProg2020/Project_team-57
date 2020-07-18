package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.IOControl;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.account.CustomerControl;
import server.controller.account.VendorControl;
import server.model.existence.Account;
import server.model.existence.Log;
import server.server.Response;
import server.server.Server;

import java.io.*;
import java.util.List;

public class AccountHandler extends Handler {
    private final IOControl ioControl = IOControl.getController();
    private final AccountControl accountControl = AccountControl.getController();
    private final AdminControl adminControl = AdminControl.getController();
    private final VendorControl vendorControl = VendorControl.getController();
    private final CustomerControl customerControl = CustomerControl.getController();

    public AccountHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        switch (message) {
            case "register":
                return register();
            case "login":
                return login();
            case "log out":
                return logOut();
            case "get login type":
                return getType();
            case "get login username" :
                return getUsername();
            case "get login account":
                return getLoggedInAccount();
            case "get account by username":
                return getAccountByUsername();
            case "is there admin":
                return isThereAdmin();
            case "does user have image":
                return doesUserHaveImage();
            case "delete user image":
                return deleteUserImage();
            case "edit account info":
                return editAccountInfo();
            case "change password":
                return changePassword();
            case "add money":
                return addMoney();
            case "subtract money":
                return subtractMoney();
            case "get account credit":
                return getCredit();
            case "modified accounts" :
                return getModifiedAccounts();
            case "modify user approve":
                return modifyUserApprove();
            case "get all customers with search":
                return getAllCustomersWithSearch();
            case "get all customers":
                return getAllCustomers();
            case "get vendor logs":
                return getAllLogs("Vendor");
            case "get customer logs":
                return getAllLogs("Customer");
            case "get product buyers":
                return getProductBuyers();
            case "delete user":
                return deleteUser();
            case "set minimum wallet money":
                return setMinimumWallet();
            case "set market wage":
                return setMarketWage();
            case "get wage":
                return getWage();
            case "get minimum wallet":
                return getMinimumWallet();
            default:
                return null/*server.getUnknownError()*/;
        }
    }

    private String getMinimumWallet() {
        Response<Double> response = new Response<>(Notification.PACKET_NOTIFICATION, adminControl.getMinimumWallet());
        return gson.toJson(response);
    }

    private String getWage() {
        Response<Double> response = new Response<>(Notification.PACKET_NOTIFICATION, adminControl.getWage());
        return gson.toJson(response);
    }

    private String setMarketWage() {
        Command<Double> command = commandParser.parseToCommand(Command.class, (Class<Double>) Double.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            return gson.toJson(new Response(adminControl.setMarketWage(command.getDatum())));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String setMinimumWallet() {
        Command<Double> command = commandParser.parseToCommand(Command.class, (Class<Double>) Double.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            return gson.toJson(new Response(adminControl.setMinimumWallet(command.getDatum())));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String deleteUser() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>) String.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            return gson.toJson(new Response(accountControl.deleteUserWithUsername(command.getDatum())));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String modifyUserApprove() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>) String.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            String username = command.getData(0), isApproved = command.getData(1);
            return gson.toJson(new Response(accountControl.modifyApprove(username, (isApproved.equals("true") ? 1 : 0))));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getProductBuyers() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Vendor")) {
            Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION);
            response.setData(vendorControl.getProductBuyers(server.getPropertyByRelic(command.getRelic()).getProductIDForBuyers()));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getAllLogs(String type) {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals(type)) {
            return gson.toJson(new Response<Log>
                    (Notification.PACKET_NOTIFICATION,
                            (type.equalsIgnoreCase("vendor") ?
                            vendorControl.getAllVendorLogs(server.getUsernameByAuth(command.getAuthToken())).toArray(new Log[0])
                            :
                            customerControl.getAllLogs(server.getUsernameByAuth(command.getAuthToken())).toArray(new Log[0]))));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getAllCustomers() {
        Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION);
        response.setData(adminControl.getModifiedAccounts(Account.AccountType.CUSTOMER));
        return gson.toJson(response);
    }

    private String getAllCustomersWithSearch() {
        Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION);
        response.setData(adminControl.getModifiedAccounts(Account.AccountType.CUSTOMER, commandParser.parseDatum(Command.class, (Class<String>)String.class)));
        return gson.toJson(response);
    }

    private String getModifiedAccounts() {
        Command<Account.AccountType> command = commandParser.parseToCommand(Command.class, (Class<Account.AccountType>) Account.AccountType.class);
        if(accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION);
            response.setData(accountControl.getModifiedAccounts(command.getDatum(), accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getUsername()));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String logOut() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Response response = new Response(server.removeAuth(command.getAuthToken()) ? Notification.PACKET_NOTIFICATION : Notification.UNKNOWN_ERROR);
        return gson.toJson(response);
    }

    private String getAccountByUsername() {
        String username = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION, accountControl.getAccountByUsername(username));
        return gson.toJson(response);
    }

    private String getCredit() {
        String username = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        double credit = accountControl.getCredit(username);
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, Double.toString(credit));
        return gson.toJson(response);
    }

    private String subtractMoney() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String username = server.getUsernameByAuth(command.getAuthToken());
        double currentMoney = accountControl.getCredit(username);
        Notification notification = Notification.LACK_BALANCE_ERROR;
        if(currentMoney >= getDouble(command.getDatum())) {
            notification = doWithDraw(command.getData(1), command.getData(2), command.getData(3), command.getData(0));
            if(notification == Notification.SUCCESSFUL_TRANSACTION) {
                notification = accountControl.getMoney(username, getDouble(command.getDatum()));
            }
        }
        Response response = new Response(notification);
        return gson.toJson(response);
    }

    private Notification doWithDraw(String username, String password, String bankNumber, String moneyString) {
        String bankAuthToken = server.getBankAuthToken(username, password);
        if(bankAuthToken.equals("invalid username or password")) {
            return Notification.INVALID_TRANSACTION_INFO;
        }

        String receiptID = server.getReceipt(bankAuthToken, "move", moneyString, "Fuck", Server.MARKET_BANK_ACCOUNT_NUMBER, bankNumber);
        if(receiptID.equals("source account id is invalid")) {
            return Notification.INVALID_TRANSACTION_INFO;
        }
        server.payReceipt(receiptID);
        return Notification.SUCCESSFUL_TRANSACTION;
    }

    private String addMoney() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String username = server.getUsernameByAuth(command.getAuthToken());
        Notification notification = doDeposit(command.getData(1), command.getData(2), command.getData(3), command.getData(0));
        if(notification == Notification.SUCCESSFUL_TRANSACTION) {
            notification = accountControl.addMoney(username, getDouble(command.getDatum()));
        }
        Response response = new Response(notification);
        return gson.toJson(response);
    }

    private Notification doDeposit(String username, String password, String bankNumber, String moneyString) {
        String bankAuthToken = server.getBankAuthToken(username, password);
        if(bankAuthToken.equals("invalid username or password")) {
            return Notification.INVALID_TRANSACTION_INFO;
        }

        String receiptID = server.getReceipt(bankAuthToken, "move", moneyString, "Fuck", bankNumber, Server.MARKET_BANK_ACCOUNT_NUMBER);
        if(receiptID.equals("dest account id is invalid")) {
            return Notification.INVALID_TRANSACTION_INFO;
        }
        if(server.payReceipt(receiptID).equals("done successfully")) {
            return Notification.SUCCESSFUL_TRANSACTION;
        }
        return Notification.NOT_ENOUGH_MONEY_BANK;
    }

    private double getDouble(String string) {
        if(string != null && !string.isEmpty())
            return Double.parseDouble(string);

        return 0;
    }

    private String changePassword() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String username = server.getUsernameByAuth(command.getAuthToken()),
                oldPassword = command.getData().get(0),
                newPassword = command.getData().get(1);
        //Todo ChangePassword
        Response response = new Response(accountControl.changePassword(username, oldPassword, newPassword));
        return gson.toJson(response);
    }

    private String editAccountInfo() {
        Account account = commandParser.parseDatum(Command.class, (Class<Account>)Account.class);
        Response response = new Response(accountControl.editAccount(account));
        return gson.toJson(response);
    }

    private String deleteUserImage() {
        String username = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        accountControl.setAccountPicture(username, null);
        Response response = new Response(Notification.PACKET_NOTIFICATION);
        return gson.toJson(response);
    }

   /*private String getUserImage() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Integer[] userImageIntegerArray = accountControl.getProfileImageArrayByUsername(command.getData().get(0));
        Response<Integer> response = new Response<>(Notification.PACKET_NOTIFICATION, userImageIntegerArray);
        outStream.write
        return gson.toJson(response);
    }*/

    private String doesUserHaveImage() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Boolean doesHaveImage = accountControl.doesUserHaveImage(command.getData().get(0));
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, doesHaveImage);
        return gson.toJson(response);
    }

    private String getUsername() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Account account = accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken()));
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, account.getUsername());
        return gson.toJson(response);
    }

    private String isThereAdmin() {
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, ioControl.isThereAdmin());
        return gson.toJson(response);
    }

    private String getLoggedInAccount() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Account account = accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken()));
        Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION, account);
        return gson.toJson(response);
    }

    private String getType() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Account account = accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken()));
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, account.getType());
        return gson.toJson(response);
    }

    private String login() {
        Account account = commandParser.parseDatum(Command.class, (Class<Account>)Account.class);
        Notification result = ioControl.login(account);
        Response<String> response;
        if(result == Notification.LOGIN_SUCCESSFUL) {
            String auth = server.makeAuth();
            response = new Response<>(result, auth);
            server.addAuth(auth, account.getUsername());
        } else {
            response = new Response<>(result, "EMPTY");
        }
        return gson.toJson(response);
    }

    private String register() {
        Account account = commandParser.parseDatum(Command.class, (Class<Account>)Account.class);
        Response response = new Response(ioControl.register(account));
        return gson.toJson(response);
    }

}
