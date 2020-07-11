package client.view;

import client.api.Client;
import client.api.Command;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import server.model.existence.Account;
import server.server.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class Processor {
    protected final String IMAGE_FOLDER_URL = "client\\Images\\";
    protected Stage myStage;
    protected Processor parentProcessor;
    protected ArrayList<Stage> subStages = new ArrayList<>();
    protected Client client = Client.getClient();


    protected static final String errorTextFieldStyle = "-fx-border-color: firebrick; -fx-border-width: 0 0 2 0;";

    public Stage getMyStage() {
        return myStage;
    }

    public ArrayList<Stage> getSubStages() {
        return subStages;
    }

    public void setSubStages(ArrayList<Stage> subStages) {
        this.subStages = subStages;
    }

    //Stage Managing Section
    public void setMyStage(Stage myStage) {
        this.subStages = new ArrayList<>();
        this.myStage = myStage;
        this.myStage.setOnCloseRequest(event -> {
            if(this instanceof ProductProcessor) {
                ((ProductProcessor) this).subProcessors.get(0).stopTimer();
            }
            for (Stage subStage : this.subStages) {
                subStage.close();
            }
            if(parentProcessor != null) {
                parentProcessor.removeSubStage(myStage);
            }
        });
    }

    public void addSubStage(Stage subStage) {
        this.subStages.add(subStage);
        if(parentProcessor == null){
            subStage.setOnCloseRequest(event -> {
                if(this instanceof ProductProcessor) {
                    ((ProductProcessor) this).subProcessors.get(0).stopTimer();
                }
                this.removeSubStage(subStage);
            });
        }

    }

    protected void removeSubStage(Stage subStage) {
        this.subStages.removeIf(stage -> {
            return stage.getTitle().equals(subStage.getTitle());
        });
    }

    protected boolean canOpenSubStage(String title, Processor processor) {
        for (Stage subStage : processor.getSubStages()) {
            if(subStage.getTitle().equals(title)){
                subStage.setAlwaysOnTop(true);
                subStage.setAlwaysOnTop(false);
                return false;
            }
        }
        return true;
    }

    protected void closeSubStage(Stage stage, Processor stageProcessor) {
        stageProcessor.removeSubStage(stage);
        stage.close();
    }
    //Stage Managing Section

    //TextField Special Setting Section
    protected String removeDots(String text) {
        StringBuilder stringBuilder = new StringBuilder(text);
        boolean foundDot = false;
        int textSize = text.length();

        for (int i = 0; i < textSize; i++) {
            if(text.charAt(i) < 48 || text.charAt(i) > 57) {
                if(text.charAt(i) == '.') {
                    if(foundDot) {
                        stringBuilder.deleteCharAt(i);
                        textSize--;
                    }
                    foundDot = true;
                } else {
                    stringBuilder.deleteCharAt(i);
                    textSize--;
                }
            }
        }

        return stringBuilder.toString();
    }

    protected void setDoubleFields(TextField textField, double maxValue) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                if(newValue.equals(".")) {
                    textField.setText("0.");
                } else if (!newValue.matches("\\d+(.(\\d)+)?")) {
                    if(textField.getText().contains(".")) {
                        textField.setText(removeDots(textField.getText()));
                    } else {
                        textField.setText(newValue.replaceAll("[^\\d\\.]", ""));
                    }
                } else if(newValue.matches("\\d+(.(\\d)+)?") && Double.parseDouble(newValue) >= maxValue) {
                    //Todo checking
                    textField.setText(oldValue);
                }

            }
        });
    }

    protected void setIntegerFields(TextField textField, Integer maxValue) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                int newValueLength = newValue.length(), maxValueLength = Integer.toString(maxValue).length();

                if (!newValue.matches("\\d+")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                } else if(newValue.matches("\\d+") && (newValueLength > maxValueLength ||
                        (newValueLength == maxValueLength && newValue.compareTo(Integer.toString(maxValue)) >= 0))) {
                    textField.setText(oldValue);
                }
            }
        });
    }

    protected void setStringFields(TextInputControl textInputControl, int maxLength) {
        textInputControl.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if(newValue != null && newValue.length() > maxLength)
                    textInputControl.setText(oldValue);
            }
        });
    }
    //TextField Special Setting Section

    public Processor getParentProcessor() {
        return parentProcessor;
    }

    public void setParentProcessor(Processor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }

    protected String getSmoothDoubleFormat(double number) {
        DecimalFormat doubleFormatter = new DecimalFormat("#.####");
        doubleFormatter.setRoundingMode(RoundingMode.HALF_UP);
        return doubleFormatter.format(number);
    }


    //Login Part
    protected boolean isLoggedIn() {
        return client.getAuthToken() != null && !client.getAuthToken().isEmpty();
    }

    protected String getType() {
        Command command = new Command("get login type", Command.HandleType.ACCOUNT);
        Response<String> response = client.postAndGet(command, Response.class, (Class<String>)String.class);
        return response.getData().get(0);
    }

    protected String getUsername() {
        Command command = new Command("get login username", Command.HandleType.ACCOUNT);
        Response<String> response = client.postAndGet(command, Response.class, (Class<String>)String.class);
        return response.getData().get(0);
    }

    protected Account getLoggedInAccount() {
        Command command = new Command("get login account", Command.HandleType.ACCOUNT);
        Response<Account> response = client.postAndGet(command, Response.class, (Class<Account>)Account.class);
        return response.getData().get(0);
    }

}
