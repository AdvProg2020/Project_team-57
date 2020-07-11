package server.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ProductHandler extends Handler {

    public ProductHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        return null;
    }
}
