package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import server.controller.account.AccountControl;
import server.controller.product.ProductControl;
import server.server.Server;

import java.io.*;
import java.util.Date;
import java.util.List;

public class PictureHandler extends Handler {
    private final Command.HandleType handleType;
    private AccountControl accountControl = AccountControl.getController();
    private ProductControl productControl = ProductControl.getController();

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
                    savePicture();
                    break;
                default:
                    System.out.println("Serious Error In Picture Handler");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePicture() {
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
            case "get product image-1":
            case "get product image-2":
            case "get product image-3":
            case "get product image-4":
            case "get product image-5":
            case "get product image-6":
                imageInputStream = getProductImageInputStream();
            case "get edit product image-1":
            case "get edit product image-2":
            case "get edit product image-3":
            case "get edit product image-4":
            case "get edit product image-5":
            case "get edit product image-6":
                imageInputStream = getEditProductImageInputStream();
            default:
                System.out.println("Serious Error In Sending ");
        }

        int i;
        while ((i = imageInputStream.read()) > -1) {
            outStream.write(i);
            outStream.flush();
        }

        imageInputStream.close();
        outStream.close();
        System.out.println(new Date());
    }

    private FileInputStream getEditProductImageInputStream() {
        int imageNumber = Integer.parseInt(message.split("-")[1]);
        return productControl.
                getEditingProductImageFileInputStreamByID
                        (commandParser.parseDatum(Command.class, (Class<String>)String.class), imageNumber);
    }

    private FileInputStream getProductImageInputStream() {
        int imageNumber = Integer.parseInt(message.split("-")[1]);
        return productControl.
                getProductImageFileInputStreamByID
                        (commandParser.parseDatum(Command.class, (Class<String>)String.class), imageNumber);
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
