package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.CustomerControl;
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
    private final CustomerControl customerControl = CustomerControl.getController();

    public ProductHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        switch (message) {
            case "get product":
            case "get editing product":
            case "get cart product":
                return getProduct(message.substring(4));
            case "get product comments":
                return getAllProductComments();
            case "get average score":
                return getAverageScore();
            default:
                return null;
        }
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

    /*private String getEditingProduct() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        Product editingProduct = productControl.getEditedProductByID(productID);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, editingProduct);
        return gson.toJson(response);
    }

    private String getProduct() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        Product product = productControl.getProductById(productID);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, product);
        return gson.toJson(response);
    }*/

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
