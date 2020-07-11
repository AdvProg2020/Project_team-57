package client.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import server.server.Response;
import server.server.Server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Client {
    private static int PORT = 60718;
    private static Client client = null;
    private final static String HOME = "127.0.0.1";
    private Socket mySocket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private String authToken;
    private Gson gson;

    protected static Function<BufferedImage, Image> bufferedImage2Image;
    protected static Function<List<Integer>, Image> integerArray2Image;
    static {
        bufferedImage2Image = new Function<BufferedImage, Image>() {
            @Override
            public Image apply(BufferedImage bufferedImage) {
                WritableImage wr = null;
                if (bufferedImage != null) {
                    wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
                    PixelWriter pw = wr.getPixelWriter();
                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            pw.setArgb(x, y, bufferedImage.getRGB(x, y));
                        }
                    }
                }

                return new ImageView(wr).getImage();
            }
        };
        integerArray2Image = new Function<List<Integer>, Image>() {
            @Override
            public Image apply(List<Integer> integers) {
                try {
                    byte[] bytes = new byte[integers.size()];

                    for (int i = 0; i < integers.size(); i++) {
                        bytes[i] = integers.get(i).byteValue();
                    }

                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    return bufferedImage2Image.apply(ImageIO.read(inputStream));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


    public static Client getClient() {
        try {
            if(client == null)
                client = new Client();
            return client;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Client() throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public  <T, E, C extends Response> Response<T> postAndGet(Command<E> command, Class<C> responseType, Class<T> responseDataType){
        try {
            makeConnection();
            command.setAuthToken(authToken);
            String commandStr = gson.toJson(command);
            outStream.writeUTF(commandStr);
            outStream.flush();
            String responseStr = inStream.readUTF();
            Response<T> response = gson.fromJson(responseStr,  TypeToken.getParameterized(responseType, responseDataType).getType());
            closeConnection();
            return response;
        } catch (IOException e) {
            System.err.println("SHIT ERROR IN POST AND GET");
            e.printStackTrace();
        }
        return null;
    }

    public <E> Image getImage(Command<E> command) {
        try {
            makeConnection();
            command.setAuthToken(authToken);
            String commandStr = gson.toJson(command);
            outStream.writeUTF(commandStr);
            outStream.flush();

            ArrayList<Integer> integers = new ArrayList<>();
            int i;
            while ((i = inStream.read()) > -1) {
                integers.add(i);
            }

            return integerArray2Image.apply(integers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <E> void sendImage(Command<E> command, File imageFile) {
        try {
            makeConnection();
            command.setAuthToken(authToken);
            String commandStr = gson.toJson(command);
            outStream.writeUTF(commandStr);
            outStream.flush();

            FileInputStream imageFileInputStream = new FileInputStream(imageFile);
            int i;
            while ((i = imageFileInputStream.read()) > -1) {
                outStream.write(i);
                outStream.flush();
            }

            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private void closeConnection() throws IOException {
        inStream.close();
        outStream.close();
        mySocket.close();
    }

    private void makeConnection() throws IOException {
        mySocket = new Socket(HOME, PORT);
        inStream = new DataInputStream(new BufferedInputStream(mySocket.getInputStream()));
        outStream = new DataOutputStream(new BufferedOutputStream(mySocket.getOutputStream()));
    }

}
