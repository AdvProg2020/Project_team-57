package client.view;

import client.api.Client;
import client.chatapi.ChatClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXTextArea;
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
import server.model.existence.Message;
import server.server.ChatServer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatProcessor extends Processor{

    private ChatClient chatClient;
    public Circle anotherImage;
    public Label anotherPerson;
    public JFXTextArea writingMessage;
    public ImageView send;
    public VBox messageBox;

    public Circle senderImage;
    public JFXTextField senderName;
    public TextArea senderMessage;

    private Image myImage;
    private Image frontImage;

    public void initChatPane(ChatClient chatClient) {
        this.chatClient = chatClient;
/*        new Thread(() -> {
            while (true) {
                writeMessage(this.chatClient.getMessage());
            }
        }).start();*/

    }

    public void writeMessage(Message message) {
        if (!message.isEndAlert()) {
            Pane messagePane = getFrontMessagePane(message);
            messageBox.getChildren().add(messagePane);
        } else {
            closeChat(message);
        }
    }

    private void closeChat(Message message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chat Closed!", ButtonType.OK);
        alert.setTitle("Ask Me");
        alert.show();
        writingMessage.setDisable(true);
        send.setDisable(true);
    }

    private Pane getFrontMessagePane(final Message message) {
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
            Message message = new Message(writingMessage.getText(), chatClient.getContactUsername(),false, getUsername());
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

    public void startChat() {
        writingMessage.setDisable(false);
        send.setDisable(false);
        chatClient.startListening();
    }
}