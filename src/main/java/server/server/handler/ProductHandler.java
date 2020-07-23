package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.account.CustomerControl;
import server.controller.account.VendorControl;
import server.controller.product.ProductControl;
import server.model.existence.Comment;
import server.model.existence.Product;
import server.server.Property;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProductHandler extends Handler {
    private final ProductControl productControl = ProductControl.getController();
    private final VendorControl vendorControl = VendorControl.getController();
    private final CustomerControl customerControl = CustomerControl.getController();
    private final AccountControl accountControl = AccountControl.getController();
    private final AdminControl adminControl = AdminControl.getController();

    public ProductHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input, Socket clientSocket) throws JsonProcessingException {
        super(outStream, inStream, server, input, clientSocket);
    }

    @Override
    protected String handle() throws Exception {
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
            case "is product purchased by customer":
                return isProductPurchasedByCustomer();
            case "get product comments":
                return getAllProductComments();
            case "get all unapproved comments":
                return getAllUnapprovedComments();
            case "modify comment approval":
                return modifyCommentApproval();
            case "add comment":
                return addComment();
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
            case "get customer cart products":
                return getAllCartProducts(false);
            case "get temp cart products":
                return getAllCartProducts(true);
            case "add to cart countable":
                return addToCart(true);
            case "add to cart uncountable":
                return addToCart(false);
            case "remove product from cart":
                return removeProductFromCart();
            case "get total price without discount":
                return getTotalPriceWithoutDiscount();
            case "get comparing product":
                return getComparingProduct();
            case "get cart size":
                return getCartSize();
            case "purchase":
                return purchase();
            case "add product file info":
                return addProductFileInfo();
            case "get product file info":
                return getProductFileInfo();
            case "does product have file":
                return doesProductHaveFile();
            case "edit product file info":
                return editProductFileInfo();
            case "get edit product file info":
                return getEditingProductFileInfo();
            case "init product file countability":
                return initProductFileCountability();
            case "get purchased file infos":
                return getPurchasedFileInfos();
            case "does edit product have file":
                return doesEditProductHaveFile();
            default:
                System.err.println("Serious Error In Product Handler");
                return null;
        }
    }

    private String doesEditProductHaveFile() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, productControl.doesEditingProductHaveFile(command.getDatum()));
        return gson.toJson(response);
    }

    private String getPurchasedFileInfos() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>) String.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            Response<Product.ProductFileInfo> response = new Response<>(Notification.PACKET_NOTIFICATION, customerControl.getPurchasedFileInfos(server.getUsernameByAuth(command.getAuthToken())).toArray(new Product.ProductFileInfo[0]));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String initProductFileCountability() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>) String.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Vendor")) {
            productControl.initProductFileCountability(command.getDatum());
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }


    private String getEditingProductFileInfo() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response<Product.ProductFileInfo> response = new Response<>(Notification.PACKET_NOTIFICATION, productControl.getEditingProductFileInfo(command.getDatum()));
        return gson.toJson(response);
    }

    private String editProductFileInfo() {
        Command<Product.ProductFileInfo> command = commandParser.parseToCommand(Command.class, (Class<Product.ProductFileInfo>)Product.ProductFileInfo.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Vendor")) {
            productControl.deleteEditingProductFile(command.getDatum().getProductID());
            productControl.editProductFileInfo(command.getDatum().getProductID(), gson.toJson(command.getDatum()));
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String doesProductHaveFile() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, productControl.doesProductHaveFile(command.getDatum()));
        return gson.toJson(response);
    }

    private String getProductFileInfo() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response<Product.ProductFileInfo> response = new Response<>(Notification.PACKET_NOTIFICATION, productControl.getProductFileInfo(command.getDatum()));
        return gson.toJson(response);
    }

    private String addProductFileInfo() {
        Command<Product.ProductFileInfo> command = commandParser.parseToCommand(Command.class, (Class<Product.ProductFileInfo>)Product.ProductFileInfo.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Vendor")) {
            productControl.addProductFileInfo(command.getDatum().getProductID(), gson.toJson(command.getDatum()));
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String purchase() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            return gson.toJson(new Response(customerControl.purchase(server.getUsernameByAuth(command.getAuthToken()), server.getPropertyByRelic(command.getRelic()))));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getCartSize() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            Response<Integer> response = new Response<>(Notification.PACKET_NOTIFICATION, new Integer(customerControl.getAllCartProducts(server.getUsernameByAuth(command.getAuthToken())).size()));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getComparingProduct() {
        Command<Integer> command = commandParser.parseToCommand(Command.class, (Class<Integer>)Integer.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, property.getComparingProducts(command.getDatum() - 1));
        return gson.toJson(response);
    }

    private String getAllCartProducts(boolean isTemp) {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>) Object.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            List<Product> productList = null;
            if(isTemp) {
                productList = customerControl.getTempCartProducts();
            } else {
                productList = customerControl.getAllCartProducts(server.getUsernameByAuth(command.getAuthToken()));
            }
            Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
            response.setData(productList);
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String removeProductFromCart() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            String username = server.getAuthTokens().containsKey(command.getAuthToken()) ? server.getUsernameByAuth(command.getAuthToken()) : "";
            String productID = command.getDatum();
            Response<Double> response = new Response<>(customerControl.removeProductFromCartByID(username, productID));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getTotalPriceWithoutDiscount() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>) Object.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            String username = server.getAuthTokens().containsKey(command.getAuthToken()) ? server.getUsernameByAuth(command.getAuthToken()) : "";
            Response<Double> response = new Response<>(Notification.PACKET_NOTIFICATION, customerControl.getTotalPriceWithoutDiscount(username));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String addToCart(boolean isCountable) {
        //Sepanta's Code
//        drfhgiiyitrrrhuurrjjfjiio9ouyygtvghk,nhgffwwwwhfjjttvvhjjj
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if (!server.getAuthTokens().containsKey(command.getAuthToken()) || accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            String productID = command.getData(0), quantityString = command.getData(1);
            String username = server.getAuthTokens().containsKey(command.getAuthToken()) ? server.getUsernameByAuth(command.getAuthToken()) : "";

            Notification notification = null;
            if(isCountable) {
                notification = customerControl.addToCartCountable(username, productID, Integer.parseInt(quantityString));
            } else {
                notification = customerControl.addToCartUnCountable(username, productID, Double.parseDouble(quantityString));
            }
            return gson.toJson(new Response(notification));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String isProductPurchasedByCustomer() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String productID = command.getData(0), customerUsername = command.getData(1);
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION,
                customerControl.isProductPurchasedByCustomer(productID, customerUsername));
        return gson.toJson(response);
    }

    private String modifyCommentApproval() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String commentID = command.getData(0);
        boolean approve = command.getData(1).equals("true");
        Response response = new Response(adminControl.modifyCommentApproval(commentID, approve));
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
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Vendor")) {
            String username = server.getUsernameByAuth(command.getAuthToken()), productID = null;
            ArrayList<Notification> notifications = new ArrayList<>();
            switch (sendType) {
                case "add":
                    Product product = command.getDatum();
                    productID = vendorControl.addProduct(product, notifications, username);
                    break;
                case "edit":
                    Product currentProduct = command.getData(0), editingProduct = command.getData(1);
                    if (currentProduct.getSellerUserName().equals(username) && editingProduct.getSellerUserName().equals(username)) {
                        notifications.add(vendorControl.editProduct(currentProduct, editingProduct, username));
                        break;
                    } else {
                        return gson.toJson(HACK_RESPONSE);
                    }
                default:
                    System.err.println("Shit. Error In #sendProduct");
                    return null;
            }

            Notification[] notificationsArray = notifications.toArray(new Notification[0]);
            Response<Notification> response = new Response<>(Notification.PACKET_NOTIFICATION, notificationsArray);
            response.setAdditionalString(productID);
            return gson.toJson(response);
        } else {
            return gson.toJson(HACK_RESPONSE);
        }
    }

    private String getAllShowingProducts() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
        response.setData(productControl.getAllShowingProducts(server.getPropertyByRelic(command.getRelic())));
        return gson.toJson(response);
    }

    private String getAllProductComments() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String username = "", type = "", productID = command.getDatum();
        if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
            username = server.getUsernameByAuth(command.getAuthToken());
            type = accountControl.getAccountByUsername(username).getType();
        }

        ArrayList<Comment> commentsArrayList = productControl.getAllProductComments(productID, username, type);
        Response<Comment> response = new Response<>(Notification.PACKET_NOTIFICATION, commentsArrayList.toArray(new Comment[0]));
        return gson.toJson(response);
    }

    private String getAllUnapprovedComments() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);

        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            Response<Comment> response = new Response<>(Notification.PACKET_NOTIFICATION, adminControl.getAllUnApprovedComments().toArray(new Comment[0]));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String addComment() {
        Command<Comment> command = commandParser.parseToCommand(Command.class, (Class<Comment>) Comment.class);

        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            Response response = new Response(productControl.addComment(command.getDatum(), server.getUsernameByAuth(command.getAuthToken())));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getAverageScore() {
        String productID = commandParser.parseDatum(Command.class, (Class<String>)String.class);
        String averageScoreString = Double.toString(productControl.getAverageScore(productID));
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, averageScoreString);
        return gson.toJson(response);
    }

    private String getProduct(String productType) {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String productID = command.getDatum();

        Product product = null;
        switch (productType) {
            case "product":
                product = productControl.getProductById(productID);
                break;
            case "editing product":
                product = productControl.getEditedProductByID(productID);
                break;
            case "cart product":
                if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
                    product = customerControl.getCartProductByID(productID, server.getUsernameByAuth(command.getAuthToken()));
                } else {
                    return gson.toJson(HACK_RESPONSE);
                }
                break;
            default:
                System.err.println("Shit. Error In Getting Product");
                return null;
        }

        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION, product);
        return gson.toJson(response);
    }
}
