package server.server.handler;

import client.api.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import notification.Notification;
import server.server.Property;
import server.server.Response;
import server.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class GeneralHandler extends Handler {

    public GeneralHandler(DataOutputStream outStream, DataInputStream inStream, Server server, String input) throws JsonProcessingException {
        super(outStream, inStream, server, input);
    }

    @Override
    protected String handle() throws InterruptedException {
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
            default:
                System.err.println("Serious Error In General Handler");
                return null;
        }
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
