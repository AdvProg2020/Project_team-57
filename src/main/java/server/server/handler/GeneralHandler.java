package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.controller.account.AccountControl;
import server.controller.account.AdminControl;
import server.controller.account.CustomerControl;
import server.controller.product.ProductControl;
import server.model.existence.Account;
import server.model.existence.Discount;
import server.model.existence.Product;
import server.server.Property;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GeneralHandler extends Handler {
    private final AccountControl accountControl = AccountControl.getController();
    private final AdminControl adminControl = AdminControl.getController();
    private final ProductControl productControl = ProductControl.getController();
    private final CustomerControl customerControl = CustomerControl.getController();

    public GeneralHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input, Socket clientSocket) throws JsonProcessingException {
        super(outStream, inStream, server, input, clientSocket);
    }

    @Override
    protected String handle() throws Exception {
        switch (message) {
            case "get relic":
                return getRelic();
            case "init sort and filter":
                return initSortAndFilter();
            case "set sort":
                return setSort();
            case "is category in filter":
                return isCategoryInFilter();
            case "add category to filter":
                return addCategoryToFilter();
            case "remove category from filter":
                return removeCategoryFromFilter();
            case "is name in filter":
                return isNameInFilter();
            case "add name to filter":
                return addNameToFilter();
            case "remove name from filter":
                return removeNameFromFilter();
            case "set price filters":
                return setPriceFilters();
            case "create discount added users":
                return initDiscountAddedUsers();
            case "get discount added users":
                return discountAddedUsers();
            case "add discount to property":
                return addDiscountToProperty();
            case "remove discount from property":
                return removeDiscountFromProperty();
            case "add customer to discount":
                return addCustomerToDiscount();
            case "delete customer from discount":
                return deleteCustomerFromDiscount();
            case "add discount":
                return addDiscount();
            case "set first comparing product":
                return setComparingProducts(1);
            case "set second comparing product":
                return setComparingProducts(2);
            case "get all comparing products":
                return getAllComparingProducts();
            case "set has discount":
                return setHasDiscount();
            case "set discount for purchase":
                return setDiscountForPurchase();
            case "set product for buyers":
                return setProductForBuyers();
            default:
                System.err.println("Serious Error In General Handler");
                return null;
        }
    }

    private String setProductForBuyers() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>) String.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            Property property = server.getPropertyByRelic(command.getRelic());
            property.setProductIDForBuyers(command.getDatum());
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String setDiscountForPurchase() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            Property property = server.getPropertyByRelic(command.getRelic());
            property.setDiscount(customerControl.getCustomerDiscountByID(command.getDatum()));
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String setHasDiscount() {
        Command<Boolean> command = commandParser.parseToCommand(Command.class, (Class<Boolean>)Boolean.class);
        if (server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Customer")) {
            Property property = server.getPropertyByRelic(command.getRelic());
            property.setHasDiscount(command.getDatum());
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String getAllComparingProducts() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        Response<Product> response = new Response<>(Notification.PACKET_NOTIFICATION);
        response.setData(productControl.getAllComparingProducts(property));
        return gson.toJson(response);
    }

    private String setComparingProducts(int i) {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        String productID = command.getDatum();

        switch (i) {
            case 1:
                property.setFirstComparingProduct(productID);
                break;
            case 2:
                property.setSecondComparingProduct(productID);
                break;
        }

        return gson.toJson(HACK_RESPONSE);
    }

    private String addDiscountToProperty() {
        Command<Discount> command = commandParser.parseToCommand(Command.class, (Class<Discount>)Discount.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            property.addDiscountToHashMap(command.getDatum());
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String removeDiscountFromProperty() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            property.removeDiscountFromHashMapByDiscountID(command.getDatum());
            Response response = new Response(Notification.PACKET_NOTIFICATION);
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String addDiscount() {
        Command<Discount> command = commandParser.parseToCommand(Command.class, (Class<Discount>)Discount.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        Discount discount = command.getDatum();
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            Response response = new Response(adminControl.addAddedDiscount(discount, getDiscountAddedUsers(discount.getID(), property)));
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String deleteCustomerFromDiscount() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            ArrayList<String> accounts = getDiscountAddedUsers(command.getData(0), property);
            if (accounts.contains(command.getData(1))) {
                accounts.remove(command.getData(1));
            }
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String addCustomerToDiscount() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            ArrayList<String> accounts = getDiscountAddedUsers(command.getData(0), property);
            if (!accounts.contains(command.getData(1))) {
                accounts.add(command.getData(1));
            }
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String discountAddedUsers() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            ArrayList<Account> accounts = accountControl.convertUsernameToAccounts(getDiscountAddedUsers(command.getDatum(), property));
            Response<Account> response = new Response<>(Notification.PACKET_NOTIFICATION);
            response.setData(accounts);
            return gson.toJson(response);
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private ArrayList<String> getDiscountAddedUsers(String discountID, Property property) {
        if(discountID == null || discountID.equals("")){
            for (Discount discount : property.getDiscountsAddedUsers().keySet()) {
                if(discount.getID() == null || discount.getID().equals("")) {
                    return property.getDiscountsAddedUsers().get(discount);
                }
            }
        } else {
            for (Discount discount : property.getDiscountsAddedUsers().keySet()) {
                if(discount.getID() != null && discount.getID().equals(discountID)) {
                    return property.getDiscountsAddedUsers().get(discount);
                }
            }
        }
        return new ArrayList<>();
    }

    private String initDiscountAddedUsers() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        if(server.getAuthTokens().containsKey(command.getAuthToken()) && accountControl.getAccountByUsername(server.getUsernameByAuth(command.getAuthToken())).getType().equals("Admin")){
            property.createDiscountAddedUsers();
            return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
        }
        return gson.toJson(HACK_RESPONSE);
    }

    private String setPriceFilters() {
        Command<Double> command = commandParser.parseToCommand(Command.class, (Class<Double>)Double.class);
        double minPrice = command.getData(0), maxPrice = command.getData(1);
        String relic = command.getRelic();
        Property property = server.getPropertyByRelic(relic);
        property.setPriceFilters(minPrice, maxPrice);
        return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
    }

    private String isNameInFilter() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String relic = command.getRelic(), filterName = command.getDatum();
        Property property = server.getPropertyByRelic(relic);
        Boolean bool = property.isThereFilteringNameWithName(filterName);
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, bool);
        return gson.toJson(response);
    }

    private String addNameToFilter() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String relic = command.getRelic(), filterName = command.getDatum();
        Property property = server.getPropertyByRelic(relic);
        property.addToFilterNameList(filterName);
        return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
    }

    private String removeNameFromFilter() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String relic = command.getRelic(), filterName = command.getDatum();
        Property property = server.getPropertyByRelic(relic);
        property.removeFromFilterNameList(filterName);
        return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
    }

    private String addCategoryToFilter() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String relic = command.getRelic(), categoryName = command.getDatum();
        Property property = server.getPropertyByRelic(relic);
        property.addToFilterCategoryList(categoryName);
        return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
    }

    private String removeCategoryFromFilter() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String relic = command.getRelic(), categoryName = command.getDatum();
        Property property = server.getPropertyByRelic(relic);
        property.removeFromFilterCategoryList(categoryName);
        return gson.toJson(new Response(Notification.PACKET_NOTIFICATION));
    }

    private String isCategoryInFilter() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        String relic = command.getRelic(), categoryName = command.getDatum();
        Property property = server.getPropertyByRelic(relic);
        Boolean bool = property.isThereFilteringCategoryWithName(categoryName);
        Response<Boolean> response = new Response<>(Notification.PACKET_NOTIFICATION, bool);
        return gson.toJson(response);
    }

    private String setSort() {
        Command<String> command = commandParser.parseToCommand(Command.class, (Class<String>)String.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        property.setSort(command.getData(0), command.getData(1).equals("true"));
        return gson.toJson(new Response<>(Notification.PACKET_NOTIFICATION));
    }

    private String initSortAndFilter() {
        Command command = commandParser.parseToCommand(Command.class, (Class<Object>)Object.class);
        Property property = server.getPropertyByRelic(command.getRelic());
        property.initFilter(); property.initSort();
        return gson.toJson(new Response<>(Notification.PACKET_NOTIFICATION));
    }

    private String getRelic() {
        String relic = server.makeRelic(); server.addRelic(relic);
        Response<String> response = new Response<>(Notification.PACKET_NOTIFICATION, relic);
        return gson.toJson(response);
    }
}
