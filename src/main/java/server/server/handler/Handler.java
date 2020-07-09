package server.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public abstract class Handler extends Thread{
    protected DataOutputStream outStream;
    protected DataInputStream inStream;
    protected Gson gson;
    protected CommandParser commandParser;
    protected String message;
    protected Server server;

    public Handler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        this.outStream = outStream;
        this.inStream = inStream;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.commandParser = new CommandParser(gson);
        commandParser.setJson(input);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.readValue(input, ObjectNode.class);
        this.message = objectNode.get("message").asText();
        this.server = server;
    }

    @Override
    public void run() {
        try {
            String output = handle();
            outStream.writeUTF(output);
            outStream.flush();
            System.out.println(new Date());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    abstract protected String handle() throws InterruptedException;

}
