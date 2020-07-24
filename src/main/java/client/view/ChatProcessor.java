package client.view;

import client.chatapi.ChatClient;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import server.model.existence.Message;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatProcessor extends Processor{

    public HBox messageHBox;
    public ScrollPane chatScroll;
    private ChatClient chatClient;
    public Circle anotherImageCircle;
    public Label anotherPersonLabel;
    public JFXTextArea writingMessageArea;
    public ImageView sendImageView;
    public VBox messageBox;

    public Circle senderImageCircle;
    public JFXTextField senderNameField;
    public JFXTextArea senderMessageArea;
    private ExecutorService executor = Executors.newCachedThreadPool ( );

    private Image myImage;
    private Image frontImage;

    public void initChatPane(ChatClient chatClient) {
        this.chatClient = chatClient;
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setStringFields(writingMessageArea, 2500);
    }

    public void writeMessage(Message message) {
        Task displayMessage = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater( () -> {
                    if (!message.isEndAlert()) {
                        HBox messageHBox = getFrontMessagePane(message);
                        messageBox.getChildren().add(messageHBox);
                        chatScroll.layout();
                        chatScroll.setVvalue(1.0);
/*                        try {
                            Thread.sleep(2000);
                            System.out.println("After Sleep");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        chatScroll.setVvalue(1.0);*/
                    } else {
                        closeChat(message);
                    }
                });
                return null;
            }
        };
        executor.execute(displayMessage);
    }

    private void closeChat(Message message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chat Closed!", ButtonType.OK);
        alert.setTitle("Ask Me");
        alert.show();
        writingMessageArea.setDisable(true);
        sendImageView.setDisable(true);
    }

    private HBox getFrontMessagePane(final Message message) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ChatMessage.fxml"));
        try {
            HBox messageHBox = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            chatProcessor.initMessage(message, ((frontImage == null) ? frontImage = getProfileImage(message.getSenderName()) : frontImage));
            messageHBox.setNodeOrientation(NodeOrientation.INHERIT);
            return messageHBox;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initMessage(Message message, Image image) {
        senderImageCircle.setFill(new ImagePattern(image));
        senderNameField.setText(message.getSenderName());
        senderMessageArea.setText(message.getMessage());
    }

    public void sendMessage(MouseEvent event) {
        if (!writingMessageArea.getText().isEmpty()) {
            Message message = new Message(writingMessageArea.getText(), chatClient.getContactUsername(),false, getUsername());
            chatClient.sendMessage(message);
            HBox messageHBox = getMyMessagePane(message);
            messageBox.getChildren().add(messageHBox);
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
/*            try {
                Thread.sleep(2000);
                System.out.println("After Sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chatScroll.setVvalue(1.0);*/
            writingMessageArea.setText("");
        }
    }

    private HBox getMyMessagePane(Message myMessage) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ChatMessage.fxml"));
        try {
            HBox messageHBox = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            chatProcessor.initMessage(myMessage, ((myImage == null) ? getProfileImage(getUsername()) : myImage));
            messageHBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            return messageHBox;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startChat() {
        writingMessageArea.setDisable(false);
        sendImageView.setDisable(false);
        chatClient.startListening();
    }

    public void textAreaOnKey(KeyEvent keyEvent) {
        if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage(null);
        }
    }
}