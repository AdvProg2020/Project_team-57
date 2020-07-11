package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import server.controller.account.AccountControl;
import server.server.Server;

import java.io.*;
import java.util.Date;
import java.util.List;

public class PictureHandler extends Handler {
    private final Command.HandleType handleType;
    private AccountControl accountControl = AccountControl.getController();

    public PictureHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input, Command.HandleType handleType) throws JsonProcessingException {
        super(outStream, inStream, server, input);
        this.handleType = handleType;
    }

    @Override
    public void run() {
        try {
            switch (handleType) {
                case PICTURE_GET:
                    getPicture();
                    break;
                case PICTURE_SEND:
                    sendPicture();
                    break;
                default:
                    System.out.println("Serious Error In Picture Handler");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPicture() {
        try {
            List<String> data = commandParser.parseData(Command.class, (Class<String>)String.class);
            String username = data.get(0), pictureExtension = data.get(1);
            FileOutputStream pictureOutputStream = null;

            switch (message) {
                case "send user image":
                    pictureOutputStream = accountControl.getAccountPictureOutputStream(username, pictureExtension);
                    break;
                    //Todo
                default:
                    System.out.println("Error In #sendPicture");
                    System.out.println("Message : " + message);
            }

            int i;
            while ( (i = inStream.read()) > -1) {
                pictureOutputStream.write(i);
            }

            pictureOutputStream.flush();
            pictureOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getPicture() throws IOException {
        FileInputStream imageInputStream = null;

        switch (message) {
            case "get user image":
                imageInputStream = getUserImageInputStream();
                break;
            default:
                System.out.println("Serious Error In Sending ");
        }

        int i;
        while ((i = imageInputStream.read()) > -1) {
//            System.out.println("i : " + i);
            outStream.write(i);
            outStream.flush();
        }

        imageInputStream.close();
        outStream.close();
        System.out.println(new Date());
    }

    private FileInputStream getUserImageInputStream() {
        String userName = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        return accountControl.getUserImageInputStream(userName);
    }

    @Override
    protected String handle() throws InterruptedException {
        return null;
    }
}
