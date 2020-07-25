package client.view;

import client.api.Command;
import client.chatapi.ChatClient;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import server.model.existence.Message;
import server.server.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatProcessor extends Processor {

    private static final String START_CHAT_MESSAGE = " Started To Talk To You";
    public HBox messageHBox;
    public ScrollPane chatScroll;
    public ImageView logoutButton;
    public JFXTextField serverMessageField;
    public Label isContactTypingLabel;
    private ChatClient chatClient;
    public JFXTextArea writingMessageArea;
    public ImageView sendImageView;
    public VBox messageBox;
    public Circle senderImageCircle;
    public JFXTextField senderNameField;
    public JFXTextArea senderMessageArea;
    private ExecutorService executor = Executors.newCachedThreadPool ( );

    private Image myImage;
    private Image frontImage;
    private long writingAreaOnKeyListenTimer = -1;
    private long writingAreaOnKeyListenClock = 500_000_000L; //0.5 sec
    private long isContactTypingListenTimer = -1;
    private long isContactTypingListenClock = 500_000_000L;//0.5 sec
    private long isTypingLabelListenTimer = -1;
    private long isTypingLabelListenClock = 500_000_000L;//0.5 sec
    private AnimationTimer writingAreaOnKeyListen;
    private AnimationTimer isContactTypingListen;
    private AnimationTimer isTypingLabelListen;
    private boolean amITyping = false;

    public void initChatPane(ChatClient chatClient) {
        this.chatClient = chatClient;
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setStringFields(writingMessageArea, 2500);
        initAnimationTimers();
    }

    private void initAnimationTimers() {
        writingAreaOnKeyListen = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(writingAreaOnKeyListenTimer == -1) {
                    writingAreaOnKeyListenTimer = now;
                } else {
                    if(now - writingAreaOnKeyListenTimer > writingAreaOnKeyListenClock) {
                        amITyping = !writingMessageArea.getText().isEmpty();
                        writingAreaOnKeyListenTimer = now;
                    }
                }
            }
        };
        isContactTypingListen = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(isContactTypingListenTimer == -1) {
                    isContactTypingListenTimer = now;
                } else {
                    if(now - isContactTypingListenTimer > isContactTypingListenClock) {
                        Command<String> command = new Command<>("is contact typing", Command.HandleType.ACCOUNT, chatClient.getContactUsername());
                        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
                        if(response.getDatum()) {
                            isTypingLabelListen.start();
                        }
                        else {
                            isTypingLabelListen.stop();
                            isTypingLabelListenTimer = -1;
                            isContactTypingLabel.setText("");
                        }
                        isContactTypingListenTimer = now;
                    }
                }
            }
        };
        isTypingLabelListen = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(isTypingLabelListenTimer == -1) {
                    isTypingLabelListenTimer = now;
                } else {
                    if(now - isTypingLabelListenTimer > isTypingLabelListenClock) {
                        if(isContactTypingLabel.getText() == null || isContactTypingLabel.getText().isEmpty()) {
                            isContactTypingLabel.setText(chatClient.getContactUsername() + " is typing.");
                        } else {
                            String[] isContactTypingLabelSplit = isContactTypingLabel.getText().split("\\s");
                            String text = " ";
                            for (int i = 1; i < isContactTypingLabelSplit.length; i++) {
                                text += isContactTypingLabelSplit[i];
                                text += " ";
                            }
                            int dotCounter = 0;
                            for (char c : text.toCharArray()) {
                                if(c == '.')
                                    dotCounter++;
                            }
                            ++dotCounter;
                            dotCounter %= 4;
                            if(dotCounter == 0)
                                ++dotCounter;
                            text = text.split("\\.")[0];
                            for (int i = 0; i < dotCounter; i++) {
                                text += ".";
                            }
                            isContactTypingLabel.setText(chatClient.getContactUsername() + text);
                        }
                        isTypingLabelListenTimer = now;
                    }
                }
            }
        };
    }

    public void writeMessage(Message message) {
        Task displayMessage = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater( () -> {
                    HBox messageHBox = getFrontMessagePane(message);
                    messageBox.getChildren().add(messageHBox);
                    chatScroll.layout();
                    chatScroll.setVvalue(1.0);
                });
                return null;
            }
        };
        executor.execute(displayMessage);
    }

    private HBox getFrontMessagePane(final Message message) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ChatMessage.fxml"));
        try {
            HBox messageHBox = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            if(frontImage == null) {
                System.err.println("Downloading Your Pic: " + message.getSenderName());
                frontImage = getProfileImage(message.getSenderName());
            }
            chatProcessor.initMessage(message, frontImage);
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
        senderNameField.setEditable(false);
        senderMessageArea.setText(message.getMessage());
        senderMessageArea.setEditable(false);
    }

    public void sendMessage(MouseEvent event) {
        if (!writingMessageArea.getText().isEmpty()) {
            Message message = new Message(writingMessageArea.getText(), chatClient.getContactUsername(),false, getUsername());
            chatClient.sendMessage(message);
            HBox messageHBox = getMyMessagePane(message);
            messageBox.getChildren().add(messageHBox);
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
            writingMessageArea.setText("");
        }
    }

    private HBox getMyMessagePane(Message myMessage) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ChatMessage.fxml"));
        try {
            HBox messageHBox = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            if(myImage == null) {
                System.err.println("Downloading My Pic: " + getUsername());
                myImage = getProfileImage(getUsername());
            }
            chatProcessor.initMessage(myMessage, ((myImage == null) ? getProfileImage(getUsername()) : myImage));
            messageHBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            return messageHBox;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startChat() {
        writingAreaOnKeyListen.start();
        isContactTypingListen.start();
        writingMessageArea.setDisable(false);
        sendImageView.setDisable(false);
        chatClient.startListening();
    }

    public void textAreaOnKey(KeyEvent keyEvent) {
        if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage(null);
        }
    }

    public void endChat(boolean isSupporterEnded, boolean isSupporter) {
        amITyping = false;
        writingAreaOnKeyListen.stop();
        writingAreaOnKeyListenTimer = -1;
        isContactTypingListen.stop();
        isContactTypingListenTimer = -1;
        isTypingLabelListen.stop();
        isTypingLabelListenTimer = -1;
        isContactTypingLabel.setText("");
        Task displayMessage = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater( () -> {
                    if(isSupporterEnded) {
                        if(isSupporter) {
                            //:))
                        } else {
                            sendFinishChatMessage();
                        }
                    } else {
                        if(isSupporter) {
                            sendFinishChatMessage();
                            chatClient.setContactUsername(null);
                            chatClient.waitForContact();
                        } else {
                            //:)
                        }
                    }
                });
                return null;
            }
        };
        executor.execute(displayMessage);
    }

    private void sendFinishChatMessage() {
        HBox messageHBox = getServerMessageHBox(chatClient.getContactUsername() + " Has Left Your Chat!");
        messageBox.getChildren().add(messageHBox);
        chatScroll.layout();
        chatScroll.setVvalue(1.0);
        sendImageView.setDisable(true);
        writingMessageArea.setDisable(true);
    }

    public void logoutMouseClicked(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure About That?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Be Caucious");

        Optional<ButtonType> optionalButtonType = alert.showAndWait();
        if(optionalButtonType.get() == ButtonType.YES) {
            if(getType() != null && getType().equals("Customer")) {
                chatClient.customerCloseChat();
                myStage.close();
                this.parentProcessor.getSubStages().remove(myStage);
            } else {
                chatClient.supporterLogOut();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("WelcomeMenu.fxml"));
                try {
                    Parent root = fxmlLoader.load();
                    Main.setScene("BoosMarket", root);
                    Stage stage = Main.getStage();
                    stage.getIcons().removeAll(stage.getIcons());
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("Main Icon.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void chatStartedForSupporter(String contactUsername) {
        Task displayMessage = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater( () -> {
                    HBox messageHBox = getServerMessageHBox(contactUsername + START_CHAT_MESSAGE);
                    messageBox.getChildren().add(messageHBox);
                    chatScroll.layout();
                    chatScroll.setVvalue(1.0);
                });
                return null;
            }
        };
        executor.execute(displayMessage);
    }

    private HBox getServerMessageHBox(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ChatServerMessage.fxml"));
        try {
            HBox messageHBox = fxmlLoader.load();
            ChatProcessor chatProcessor = fxmlLoader.getController();
            chatProcessor.serverMessageField.setText(message);
            return messageHBox;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean amITyping() {
        return amITyping;
    }
}