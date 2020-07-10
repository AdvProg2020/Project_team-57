package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.IOControl;
import server.model.existence.Account;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class AccountHandler extends Handler {
    IOControl ioControl = IOControl.getController();

    public AccountHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        System.out.println("Message : " + message);
        System.out.println("Is : " + message.equals("register"));
        switch (message) {
            case "register":
                return register();
            case "login":
                return login();
            default:
                return null/*server.getUnknownError()*/;
        }
    }

    private String login() {
        Account account = commandParser.parseDatum(Command.class, (Class<Account>)Account.class);
        Notification result = ioControl.login(account);
        Response<String> response;
        if(result == Notification.LOGIN_SUCCESSFUL) {
            String auth = server.makeAuth();
            response = new Response<>(result, auth);
            server.addAuth(auth);
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
