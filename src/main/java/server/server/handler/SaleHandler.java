package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.account.VendorControl;
import server.controller.product.ProductControl;
import server.model.existence.Category;
import server.model.existence.Discount;
import server.model.existence.Off;
import server.model.existence.Product;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaleHandler extends Handler {
    AccountControl accountControl = AccountControl.getController();
    ProductControl productControl = ProductControl.getController();
    VendorControl vendorControl = VendorControl.getController();
    AdminControl adminControl = AdminControl.getController();
    public SaleHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
        switch (message) {
            case "get all categories" :
                return getAllCategories();
            case "delete category":
                return deleteCategory();
            case "add category":
                return addCategory();
            case "edit category field-name":
            case "edit category field-parent name":
            case "edit category field-features":
                return editCategory();
            case "get all discounts":
                return getAllDiscounts();
            case "delete discount":
                return deleteDiscount();
            case "is off edit":
                return isOffEdit();
            case "does off have image":
                return doesOffHaveImage();
            case"add off":
                return addOff();
            case "edit off":
                return editOff();
            case "get vendor offs":
                return getOffs("vendor offs");
            case "get all unapproved offs":
                return getOffs("all unapproved offs");
            case "delete off":
                return deleteOff();
            case "get edit off":
                return getEditOff();
            case "get non off products":
                return getNonOffProducts(false);
            case "is there off":
                return isThereOff();
            case "get off":
                return getOff();
            case "get non off products with exceptions":
                return getNonOffProducts(true);
            case "get off products":
                return getOffProducts();
            default:
                System.err.println("Serious Error In Sale Handler");
                return null;
        }
    }

    private String getOffProducts() {
        return gson.toJson(
                new Response<Product>(Notification.PACKET_NOTIFICATION,
                        productControl.getAllOffProductsByOffID
                                (commandParser.parseDatum(Command.class, (Class<String>)String.class)).toArray(new Product[0])));
    }

    private String getOff() {
        return gson.toJson(new Response<Off>
                (Notification.PACKET_NOTIFICATION,
                        productControl.getOffByID(commandParser.parseDatum(Command.class, (Class<String>)String.class))));
    }

    private String isThereOff() {
        return gson.toJson(
                new Response<>(Notification.PACKET_NOTIFICATION,
                        productControl.isThereOffWithID(commandParser.parseDatum(Command.class, (Class<String>)String.class))));
    }

    private String getNonOffProducts(boolean hasException) {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
        if(hasException) {
            response.setData(vendorControl.getNonOffProducts(server.getUsernameByAuth(command.getAuthToken()), command.getDatum()));
        } else
            response.setData(vendorControl.getNonOffProducts(server.getUsernameByAuth(command.getAuthToken())));
        return gson.toJson(response);
    }

    private String getEditOff() {
        return gson.toJson(new Response<Off>
                (Notification.PACKET_NOTIFICATION,
                        productControl.getEditingOffByID(commandParser.parseDatum(Command.class, (Class<String>)String.class))));
    }

    private String deleteOff() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if(canChangeOff(command.getDatum(), command.getAuthToken())) {
            return gson.toJson(new Response(adminControl.modifyOffApprove(command.getDatum(), false)));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getOffs(String offsType) {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Response<Off> response = new Response<>(Notification.PACKET_NOTIFICATION);

        List<Off> offs = null;
        switch (offsType) {
            case "vendor offs":
                offs = vendorControl.getAllOffs(server.getUsernameByAuth(command.getAuthToken()));
                break;
            case "all unapproved offs":
                offs = adminControl.getAllUnApprovedOffs();
                break;
            default:
                System.err.println("Error IN #getOffs");
                offs = new ArrayList<>();
        }
        response.setData(offs);

        return gson.toJson(response);
    }

    private String editOff() {
        Command<Off> command = commandParser.parseToCommand(Command.class, (Class<Off>)Off.class);
        if(canChangeOff(command.getDatum().getOffID(), command.getAuthToken())) {
            Response response = new Response(vendorControl.editOff(command.getDatum()));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String addOff() {
        System.err.println("Hadeaghal ta inja oomad");
        Command<Off> command = commandParser.parseToCommand(Command.class, (Class<Off>)Off.class);
        if(canChangeOff(command.getDatum().getOffID(), command.getAuthToken())) {
            Off off = command.getDatum();
            Response<String> response = new Response<>(vendorControl.addOff(off, server.getUsernameByAuth(command.getAuthToken())), off.getOffID());
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String doesOffHaveImage() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if(canChangeOff(command.getDatum(), command.getAuthToken())) {
            Boolean bool = productControl.doesOffHaveImage(command.getDatum());
            Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, bool);
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String isOffEdit() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if(canChangeOff(command.getDatum(), command.getAuthToken())) {
            Boolean bool = productControl.isOffEditing(command.getDatum());
            Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, bool);
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String deleteDiscount() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            Response<Discount> response = new Response<>(Notification.PACKET_NOTIFICATION);
            AdminControl.getController().removeDiscountByID(command.getDatum());
            server.getPropertyByRelic(command.getRelic()).removeDiscountFromHashMapByDiscountID(command.getDatum());
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);

    }

    private String getAllDiscounts() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            Response<Discount> response = new Response<>(Notification.PACKET_NOTIFICATION);
            response.setData(adminControl.getAllDiscounts());
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String editCategory() {
        Command<Category> command = commandParser.parseToCommand(Command.class, (Class<Category>)Category.class);
        if(accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            Response response = new Response<>(adminControl.editCategory(command.getData().get(0), command.getData().get(1), command.getMessage().split("-")[1]));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }


    private String addCategory() {
        Command<Category> command = commandParser.parseToCommand(Command.class, (Class<Category>)Category.class);
        if(accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            Response response = new Response<>(adminControl.addCategory(command.getDatum()));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String deleteCategory() {
        Command<Category> command = commandParser.parseToCommand(Command.class, (Class<Category>)Category.class);
        if(accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")) {
            Response response = new Response<>(adminControl.removeCategory(command.getDatum()));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getAllCategories() {
        Response<Category> response = new Response<>(Notification.PACKET_NOTIFICATION);
        response.setData(adminControl.getAllCategories());
        return gson.toJson(response);
    }
}
