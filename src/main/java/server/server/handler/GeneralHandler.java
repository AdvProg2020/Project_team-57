package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.server.Property;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class GeneralHandler extends Handler {

    public GeneralHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        switch (message) {
            case "get relic":
                return getRelic();
            case "init sort and filter":
                return initSortAndFilter();
            case "set sort":
                return setSort();
            default:
                System.err.println("Serious Error In General Handler");
                return null;
        }
    }

    private String setSort() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        property.setSort(command.getData(0), command.getData(1).equals("true"));
        return gson.toJson(new Response<>(Notification.PACKET_NOTIFICATION));
    }

    private String initSortAndFilter() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        property.initFilter(); property.initSort();
        return gson.toJson(new Response<>(Notification.PACKET_NOTIFICATION));
    }

    private String getRelic() {
        String relic = server.makeRelic(); server.addRelic(relic);
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, relic);
        System.err.println(response.getDatum());
        return gson.toJson(response);
    }
}
