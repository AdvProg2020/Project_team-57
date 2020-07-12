package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.account.CustomerControl;
import server.controller.account.VendorControl;
import server.controller.product.ProductControl;
import server.model.existence.Account;
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
    private final AccountControl accountControl = AccountControl.getController();
    private final AdminControl adminControl = AdminControl.getController();

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
            case "remove product":
            case "remove edit product":
            case "remove cart product":
                return removeProduct(message.substring(7));
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
            case "get vendor products":
                return getVendorProducts();
            case "delete editing product pictures":
                return deleteEditingProductPictures();
            case "get not approved products":
                return getNotApprovedProducts();
            case "get product status":
                return getProductStatus();
            case "modify editing product approve":
                return modifyEditingProductApprove();
            case "modify product approve":
                return modifyProductApprove();
            case "get all showing products":
                return getAllShowingProducts();
            default:
                System.err.println("Serious Error In Product Handler");
                return null;
        }
    }

    private String getAllShowingProducts() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
        response.setData(productControl.getAllShowingProducts(server.getPropertyByRelic(command.getRelic())));
        return gson.toJson(response);
    }

    private String removeProduct(String productType) {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String productID = command.getDatum();

        Notification notification = null;
        switch (productType) {
            case "product":
                notification = productControl.removeProductById(productID);
                break;
            case "edit product":
                notification = productControl.removeEditingProductById(productID);
                break;
            case "cart product":
                String username = server.getUsernameByAuth(command.getAuthToken());
                //Todo Cart
//                notification = customerControl.removeProductFromCartByID(username, productID);
                break;
            default:
                System.err.println("Error In #removeProduct");
                return null;
        }

        Response response = new Response(notification);
        return gson.toJson(response);
    }

    private String modifyProductApprove() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response response = new Response(adminControl.modifyProductApprove(command.getData(0), command.getData(1).equals("true")));
        return gson.toJson(response);
    }

    private String modifyEditingProductApprove() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response response = new Response(adminControl.modifyEditingProductApprove(command.getData(0), command.getData(1).equals("true")));
        return gson.toJson(response);
    }

    private String getProductStatus() {
        return
                gson.toJson(new Response<>
                        (Notification.PACKET_NOTIFICATION,
                                new Integer(productControl.getProductById(commandParser.parseDatum(Command.class, (Class<String>)String.class)).getStatus())));
    }

    private String getNotApprovedProducts() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if(accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
            response.setData(adminControl.getAllNotApprovedProducts());
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String deleteEditingProductPictures() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        productControl.deleteEditingProductPictures(productID);
        Response response = new Response(Notification.PACKET_NOTIFICATION);
        return gson.toJson(response);
    }

    private String getVendorProducts() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if(server.getAuthTokens().containsKey(command.getAuthToken())) {
            String username = server.getUsernameByAuth(command.getAuthToken());
            if(accountControl.getAccountByUsername(username).getType().equalsIgnoreCase("Vendor")) {
                Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
                response.setData(vendorControl.getAllProducts(username));
                return gson.toJson(response);
            }
        }
        return gson.toJson(HACK_RESPONSE);
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

        String productID = null;
        ArrayList<Notification> notifications = new ArrayList<>();
        switch (sendType) {
            case "add":
                Product product = command.getDatum();
                productID = vendorControl.addProduct(product, notifications);
                break;
            case "edit":
                Product currentProduct = command.getData(0), editingProduct = command.getData(1);
                notifications.add(vendorControl.editProduct(currentProduct, editingProduct));
                break;
            default:
                System.err.println("Shit. Error In #sendProduct");
                return null;
        }

        Notification[] notificationsArray = notifications.toArray(new Notification[0]);
        Response<Notification> response = new Response<>(Notification.PACKET_NOTIFICATION, notificationsArray);
        response.setAdditionalString(productID);
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
                System.err.println("Shit. Error In Getting Product");
                return null;
        }

        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, product);
        return gson.toJson(response);
    }
}
