package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.product.ProductControl;
import server.model.existence.Category;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SaleHandler extends Handler {
    AccountControl accountControl = AccountControl.getController();
    ProductControl productControl = ProductControl.getController();
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
            default:
                System.err.println("Serious Error In Sale Handler");
                return null;
        }
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
