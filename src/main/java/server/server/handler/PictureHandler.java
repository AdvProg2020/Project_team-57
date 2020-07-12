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
                    System.err.println("Serious Error In Picture Handler");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePicture() {
        try {
            FileOutputStream pictureOutputStream = null;

            switch (message) {
                case "send user image":
                    List<String> data = commandParser.parseData(Command.class, (Class<String>)String.class);
                    String username = data.get(0), pictureExtension = data.get(1);
                    pictureOutputStream = accountControl.getAccountPictureOutputStream(username, pictureExtension);
                    break;
                    //Todo
                case "add product image":
                    pictureOutputStream = getProductImageOutputStream("add");
                    break;
                case "edit product image":
                    pictureOutputStream = getProductImageOutputStream("edit");
                    break;
                default:
                    System.err.println("Error In #sendPicture");
                    System.err.println("Message : " + message);
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

    private FileOutputStream getProductImageOutputStream(String edit) {
        List<String> data = commandParser.parseData(Command.class, (Class<String>)String.class);
        String productID = data.get(0), fileExtension = data.get(1);

        switch (edit) {
            case "add":
                return productControl.getProductPictureOutputStream(productID, fileExtension);
            case "edit":
                return productControl.getEditingProductPictureOutputStream(productID, fileExtension);
            default:
                System.err.println("Error In #getProductImageOutputStream");
                return null;
        }
    }

    private void getPicture() throws IOException {
        FileInputStream imageInputStream = null;

        switch (message) {
            case "get user image":
                outStream.writeUTF("Yeah, Bitch");
                outStream.flush();
                imageInputStream = getUserImageInputStream();
                break;
            case "get product image-1":
            case "get product image-2":
            case "get product image-3":
            case "get product image-4":
            case "get product image-5":
            case "get product image-6":
                sendExtension("product");
                imageInputStream = getProductImageInputStream();
                break;
            case "get edit product image-1":
            case "get edit product image-2":
            case "get edit product image-3":
            case "get edit product image-4":
            case "get edit product image-5":
            case "get edit product image-6":
                sendExtension("editing product");
                imageInputStream = getEditProductImageInputStream();
                break;
            default:
                System.err.println("Serious Error In Sending ");
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

    private void sendExtension(String sendType) throws IOException {
        int imageNumber = Integer.parseInt(message.split("-")[1]);
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);

        String productExtension = null;
        switch (sendType) {
            case "product":
                productExtension = productControl.getProductImageExtensionByNumber(productID, imageNumber);
                break;
            case "editing product":
                productExtension = productControl.getEditingProductImageExtensionByNumber(productID, imageNumber);
                break;
            default:
                System.err.println("Error In #sendExtension");
                return;
        }

        outStream.writeUTF(productExtension);
        outStream.flush();
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
