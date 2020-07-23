package client.view;

import client.api.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import server.server.handler.ChatHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatProcessor extends Processor implements Initializable {

    /**
     * chatPane.fxml elements
     */
    public Circle anotherImage;
    public Label anotherPerson;
    public JFXTextField writingMessage;
    public ImageView send;
    public VBox messageBox;

    /**
     * Message.fxml elements
     */
    public Circle senderImage;
    public JFXTextField senderName;
    public TextArea senderMessage;


    private Image myImage;
    private Image frontImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.toString().contains("chatPane")) {
            final Client client = this.client;
            Thread getter = new Thread(() -> {
                while (true) {
                    writeMessage(client.getMessage());
                }
            });
            getter.start();
        }
        writingMessage.setDisable(false);
        send.setDisable(false);
    }

    private void writeMessage(final String messageJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ChatHandler.Message message = gson.fromJson(messageJson, ChatHandler.Message.class);
        if (message.getAlert().equals("send message")) {
            Pane messagePane = getFrontMessagePane(message);
            messageBox.getChildren().add(messagePane);
        } else {
            closeChat(message);
        }
    }

    private void closeChat(final ChatHandler.Message message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chat Closed!", ButtonType.OK);
        alert.setTitle("Ask Me");
        alert.show();
        writingMessage.setDisable(true);
        send.setDisable(true);
    }

    private Pane getFrontMessagePane(final ChatHandler.Message message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Message.fxml"));
        try {
            Pane messagePane = fxmlLoader.load();
            senderImage.setFill(new ImagePattern((frontImage == null) ? getProfileImage(message.getSenderName()) : frontImage));
            senderName.setText(message.getSenderName());
            senderMessage.setText(message.getMessage());
            return messagePane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
    }

    public void sendMessage(MouseEvent event) {
        if (!writingMessage.toString().isEmpty()) {
            Pane myMessage = getMyMessagePane();
            messageBox.getChildren().add(myMessage);
            ChatHandler.Message message = new ChatHandler.Message(writingMessage.getText(), "send Message", getUsername());
            client.sendMessage(new Gson().toJson(message));
        }
    }

    private Pane getMyMessagePane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Message.fxml"));
        try {
            Pane message = fxmlLoader.load();
            senderImage.setFill(new ImagePattern((myImage == null) ? getProfileImage(getUsername()) : myImage));
            senderName.setText(getUsername());
            senderMessage.setText(writingMessage.getText());
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
    }
}