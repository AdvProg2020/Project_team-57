package client.chatapi;

import client.api.Client;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ChatClient {
    private final static String IP = "127.0.0.1";
    private static int PORT = 52290;

    private Socket mySocket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private Gson gson;



}
