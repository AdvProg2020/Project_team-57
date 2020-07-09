package server.server.handler;
import client.api.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CommandParser {
    private String json;
    private Gson gson;
    private ObjectMapper mapper;

    public CommandParser(Gson gson) {
        this.gson = gson;
        mapper = new ObjectMapper();
    }

    public<E, C extends Command> Command<E> parseToCommand(Class<C> rawType, Class<E> type) {
        return gson.fromJson(json, TypeToken.getParameterized(rawType, type).getType());
    }

    public<E, C extends Command> List<E> parseData(Class<C> rawType, Class<E> type) {
        return parseToCommand(rawType, type).getData();
    }

    public<E, C extends Command> E parseDatum(Class<C> rawType, Class<E> type) {
        return parseData(rawType, type).get(0);
    }

    public void setJson(String json) {
        this.json = json;
    }

}
