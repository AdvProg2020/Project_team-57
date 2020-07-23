package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import server.controller.account.AccountControl;
import server.controller.product.ProductControl;
import server.server.Server;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class PictureHandler extends Handler {
    private final Command.HandleType handleType;
    private AccountControl accountControl = AccountControl.getController();
    private ProductControl productControl = ProductControl.getController();

    public PictureHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input, Command.HandleType handleType, Socket clientSocket) throws JsonProcessingException {
        super(outStream, inStream, server, input, clientSocket);
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
            System.out.println(new Date() + ", Duration: " + formatter.format(new Date(new Date().getTime() - startOperationDate.getTime())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePicture() {
        try {
            FileOutputStream outputStream = null;

            switch (message) {
                case "send user image":
                    List<String> data = commandParser.parseData(Command.class, (Class<String>)String.class);
                    String username = data.get(0), pictureExtension = data.get(1);
                    outputStream = accountControl.getAccountPictureOutputStream(username, pictureExtension);
                    break;
                    //Todo
                case "add product image":
                    outputStream = getProductImageOutputStream("add");
                    break;
                case "edit product image":
                    outputStream = getProductImageOutputStream("edit");
                    break;
                case "send off image":
                    outputStream = getOffImageOutputStream("add");
                    break;
                case "send editing off image":
                    outputStream = getOffImageOutputStream("edit");
                    break;
                case "add product file":
                    outputStream = getProductFileOutPutStream("add");
                    break;
                case "edit product file":
                    outputStream = getProductFileOutPutStream("edit");
                    break;
                default:
                    System.err.println("Error In #sendPicture");
                    System.err.println("Message : " + message);
            }

            int i;
            while ( (i = inStream.read()) > -1) {
                outputStream.write(i);
            }

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private FileOutputStream getProductFileOutPutStream(String type) {
        List<String> data = commandParser.parseData(Command.class, (Class<String>)String.class);
        String productID = data.get(0), fileExtension = data.get(1);

        switch (type) {
            case "add":
                return productControl.getProductFileOutputStream(productID, fileExtension);
            case "edit":
                return productControl.getEditingProductFileOutputStream(productID, fileExtension);
                default:
                System.err.println("Error In #getProductImageOutputStream");
                return null;
        }
    }

    private FileOutputStream getOffImageOutputStream(String type) {
        List<String> data = commandParser.parseData(Command.class, (Class<String>)String.class);
        String offID = data.get(0), fileExtension = data.get(1);

        switch (type) {
            case "add":
                return productControl.getOffPictureOutputStream(offID, fileExtension);
            case "edit":
                return productControl.getEditingOffPictureOutputStream(offID, fileExtension);
            default:
                System.err.println("Error In #getProductImageOutputStream");
                return null;
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
        FileInputStream inputStream = null;

        switch (message) {
            case "get user image":
                outStream.writeUTF("Yeah, Bitch");
                outStream.flush();
                inputStream = getUserImageInputStream();
                break;
            case "get product image-1":
            case "get product image-2":
            case "get product image-3":
            case "get product image-4":
            case "get product image-5":
            case "get product image-6":
                sendExtension("product");
                inputStream = getProductImageInputStream();
                break;
            case "get edit product image-1":
            case "get edit product image-2":
            case "get edit product image-3":
            case "get edit product image-4":
            case "get edit product image-5":
            case "get edit product image-6":

                sendExtension("editing product");
                inputStream = getEditProductImageInputStream();
                break;
            case "get off image":
                sendExtension("off");
                inputStream = getOffImageInputStream();
                break;
            case "get editing off image":
                sendExtension("editing off");
                inputStream = getEditingOffImageInputStream();
                break;
            case "get product file":
                sendExtension("product file");
                inputStream = getProductFileInputStream();
                break;
            case "get edit product file":
                sendExtension("edit product file");
                inputStream = getEditingProductFileInputStream();
                break;
            default:
                System.err.println("Serious Error In Sending ");
        }

        int i;
        while ((i = inputStream.read()) > -1) {
            outStream.write(i);
            outStream.flush();
        }

        inputStream.close();
        outStream.close();
    }

    private FileInputStream getEditingProductFileInputStream() {
        return productControl.getEditingProductFileInputStreamByID(commandParser.parseDatum(Command.class, (Class<String>)String.class));
    }

    private FileInputStream getProductFileInputStream() {
        return productControl.getProductFileInputStreamByID(commandParser.parseDatum(Command.class, (Class<String>)String.class));
    }

    private FileInputStream getEditingOffImageInputStream() {
        return productControl.
                getEditingOffImageFileInputStreamByID
                        (commandParser.parseDatum(Command.class, (Class<String>)String.class));    }

    private FileInputStream getOffImageInputStream() {
        return productControl.
                getOffImageFileInputStreamByID
                        (commandParser.parseDatum(Command.class, (Class<String>)String.class));
    }

    private void sendExtension(String sendType) throws IOException {
        int imageNumber;
        String ID;
        String extension = null;
        switch (sendType) {
            case "product":
                imageNumber = Integer.parseInt(message.split("-")[1]);
                ID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
                extension = productControl.getProductImageExtensionByNumber(ID, imageNumber);
                break;
            case "editing product":
                imageNumber = Integer.parseInt(message.split("-")[1]);
                ID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
                extension = productControl.getEditingProductImageExtensionByNumber(ID, imageNumber);
                break;
            case "off" :
                ID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
                extension = productControl.getOffImageExtensionByID(ID);
                break;
            case "editing off":
                ID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
                extension = productControl.getEditingOffImageExtensionByID(ID);
                break;
            case "product file":
                ID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
                extension = productControl.getProductFileExtension(ID);
                break;
            case "edit product file":
                ID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
                extension = productControl.getEditingProductFileExtension(ID);
                break;
            default:
                System.err.println("Error In #sendExtension");
                return;
        }

        outStream.writeUTF(extension);
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
