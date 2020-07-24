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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
        setStringFields(writingMessage, 2500);
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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Message.fxml"));
        try {
            Pane messagePane = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            chatProcessor.initMessage(message, ((frontImage == null) ? frontImage = getProfileImage(message.getSenderName()) : frontImage));
            return messagePane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
    }

    private void initMessage(Message message, Image image) {
        senderImage.setFill(new ImagePattern(image));
        senderName.setText(message.getSenderName());
        senderMessage.setText(message.getMessage());
    }

    public void sendMessage(MouseEvent event) {
        if (!writingMessage.getText().isEmpty()) {
            Message message = new Message(writingMessage.getText(), chatClient.getContactUsername(),false, getUsername());
            chatClient.sendMessage(message);
            Pane myMessage = getMyMessagePane(message);
            messageBox.getChildren().add(myMessage);
        }
    }

    private Pane getMyMessagePane(Message myMessage) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Message.fxml"));
        try {
            Pane message = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            chatProcessor.initMessage(myMessage, ((myImage == null) ? getProfileImage(getUsername()) : myImage));
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

    public void textAreaOnKey(KeyEvent keyEvent) {
        if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) {
            System.out.println("YES");
        }
    }
}