package server.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
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
            default:
                System.err.println("Serious Error In General Handler");
                return null;
        }
    }

    private String getRelic() {
        String relic = server.makeRelic(); server.addRelic(relic);
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, relic);
        System.err.println(response.getDatum());
        return gson.toJson(response);
    }
}
