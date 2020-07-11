package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.IOControl;
import server.controller.account.AccountControl;
import server.model.existence.Account;
import server.server.Response;
import server.server.Server;

import java.io.*;
import java.util.ArrayList;

public class AccountHandler extends Handler {
    IOControl ioControl = IOControl.getController();
    AccountControl accountControl = AccountControl.getController();

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
            case "get login type":
                return getType();
            case "get login username" :
                return getUsername();
            case "get login account":
                return getLoggedInAccount();
            case "is there admin":
                return isThereAdmin();
            case "does user have image":
                return doesUserHaveImage();
            case "delete user image":
                return deleteUserImage();
            default:
                return null/*server.getUnknownError()*/;
        }
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
        System.out.println("Fuck");
        Account account = commandParser.parseDatum(Command.class, (Class<Account>)Account.class);
        Response response = new Response(ioControl.register(account));
        return gson.toJson(response);
    }
}
