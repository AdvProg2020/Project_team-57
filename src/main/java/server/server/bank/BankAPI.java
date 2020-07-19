package server.server.bank;

import java.io.*;
import java.net.Socket;

public class BankAPI {
    public static BankAPI getInstance() {
        return new BankAPI();
    }

    private static int PORT = 6666;
    private final static String IP = "127.0.0.1";
    private Socket mySocket;
    private DataOutputStream outStream;
    private DataInputStream inStream;

    public String postAndGet(String command) {
        try {
            makeConnection();
            outStream.writeUTF(command);
            outStream.flush();
            String result = inStream.readUTF();
            closeConnection();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void closeConnection() throws IOException {
        outStream.writeUTF("exit");
        outStream.flush();
        inStream.close();
        outStream.close();
        mySocket.close();
    }

    private void makeConnection() throws IOException {
        mySocket = new Socket(IP, PORT);
        inStream = new DataInputStream(new BufferedInputStream(mySocket.getInputStream()));
        outStream = new DataOutputStream(new BufferedOutputStream(mySocket.getOutputStream()));
    }
}
