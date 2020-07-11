package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
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

    public ProductHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        switch (message) {
            case "get product":
                return getProduct();
            case "get editing product":
                return getEditingProduct();
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

    private String getEditingProduct() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        Product editingProduct = productControl.getEditedProductByID(productID);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, editingProduct);
        return gson.toJson(response);
    }

    private String getProduct() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        Product editingProduct = productControl.getProductById(productID);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, editingProduct);
        return gson.toJson(response);
    }


}
