package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.CustomerControl;
import server.controller.account.VendorControl;
import server.controller.product.ProductControl;
import server.model.existence.Comment;
import server.model.existence.Product;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class ProductHandler extends Handler {
    private final ProductControl productControl = ProductControl.getController();
    private final VendorControl vendorControl = VendorControl.getController();
    private final CustomerControl customerControl = CustomerControl.getController();

    public ProductHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        switch (message) {
            case "add product":
            case "edit product":
                return sendProduct(message.substring(0, message.length() - 8));
            case "get product":
            case "get editing product":
            case "get cart product":
                return getProduct(message.substring(4));
            case "get product comments":
                return getAllProductComments();
            case "get average score":
                return getAverageScore();
            case "add seen":
                return addSeenToProduct();
            case "get product image count":
                return getProductImageCount();
            case "get edit product image count":
                return getEditProductImageCount();
            default:
                return null;
        }
    }

    private String getEditProductImageCount() {
        return
                gson.toJson(new Response<>
                        (Notification.PACKET_NOTIFICATION,
                                new Integer
                                        (productControl.getEditingProductImagesNumberByID(commandParser.parseDatum(Command.class, (Class<String>)String.class)))));
    }

    private String getProductImageCount() {
        return
                gson.toJson(new Response<>
                        (Notification.PACKET_NOTIFICATION,
                                new Integer
                                        (productControl.getProductImagesNumberByID(commandParser.parseDatum(Command.class, (Class<String>)String.class)))));
    }

    private String addSeenToProduct() {
        productControl.addSeenToProduct(commandParser.parseDatum(Command.class, (Class<String>)String.class));
        return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
    }

    private String sendProduct(String sendType) {
        Command<Product> command = commandParser.parseToCommand(Command.class, (Class<Product>)Product.class);

        ArrayList<Notification> notifications = new ArrayList<>();
        switch (sendType) {
            case "add":
                Product product = command.getDatum();
                notifications = vendorControl.addProduct(product);
                break;
            case "edit":
                Product currentProduct = command.getData(0), editingProduct = command.getData(1);
                notifications.add(vendorControl.editProduct(currentProduct, editingProduct));
                break;
            default:
                System.out.println("Shit. Error In #sendProduct");
                return null;
        }

        Notification[] notificationsArray = notifications.toArray(new Notification[0]);
        Response<Notification> response = new Response<>(Notification.PACKET_NOTIFICATION, notificationsArray);
        return gson.toJson(response);
    }

    private String getAllProductComments() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        ArrayList<Comment> commentsArrayList = productControl.getAllProductComments(productID);
        Response<Comment> response = new Response<>(Notification.PACKET_NOTIFICATION,
                getCommentArrayFromArrayList(commentsArrayList));
        return gson.toJson(response);
    }

    private Comment[] getCommentArrayFromArrayList(ArrayList<Comment> commentArrayList) {
        Comment[] commentsArray = new Comment[commentArrayList.size()];
        for (int i = 0; i < commentArrayList.size(); i++) {
            commentsArray[i] = commentArrayList.get(i);
        }
        return commentsArray;
    }

    private String getAverageScore() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        String averageScoreString = Double.toString(productControl.getAverageScore(productID));
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, averageScoreString);
        return gson.toJson(response);
    }

    private String getProduct(String productType) {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);

        Product product = null;
        switch (productType) {
            case "product":
                product = productControl.getProductById(productID);
                break;
            case "editing product":
                product = productControl.getEditedProductByID(productID);
                break;
            case "cart product":
                product = customerControl.getCartProductByID(productID);
                break;
            default:
                System.out.println("Shit. Error In Getting Product");
                return null;
        }

        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, product);
        return gson.toJson(response);
    }
}
