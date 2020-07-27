package client.view;

import client.api.Client;
import client.api.Command;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import notification.Notification;
import server.model.existence.Account;
import server.model.existence.Category;
import server.model.existence.Off;
import server.model.existence.Product;
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

import static client.api.Command.HandleType.*;

public abstract class Processor {
    protected static final String IMAGE_FOLDER_URL = "client\\Images\\";
    protected static final String DOWNLOAD_FOLDER_URL = "Downloads\\";
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
                for(ProductProcessor productProcessor : ((ProductProcessor) this).subProcessors) {
                    productProcessor.stopTimer();
                }
                ((ProductProcessor) this).imageSubProcessor.stopTimer();
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
                    for(ProductProcessor productProcessor : ((ProductProcessor) this).subProcessors) {
                        productProcessor.stopTimer();
                    }
                    ((ProductProcessor) this).imageSubProcessor.stopTimer();
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
                } else if (newValue.matches("\\d+") && (newValueLength > maxValueLength ||
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

    protected void logOut() {
        Command command = new Command("log out", Command.HandleType.ACCOUNT);
        Response response = client.postAndGet(command, Response.class, (Class<Object>)Object.class);

        if(response.getMessage() == Notification.UNKNOWN_ERROR) {
            System.err.println("Shit. Error IN Logging Out");
        } else {
            client.setAuthToken(null);
        }
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

    protected Account getAccountByUsername(String username) {
        Command<String> command = new Command("get account by username", Command.HandleType.ACCOUNT, username);
        Response<Account> response = client.postAndGet(command, Response.class, (Class<Account>)Account.class);
        return response.getDatum();
    }

    protected double getCredit(String username) {
        Command<String> command = new Command<>("get account credit", Command.HandleType.ACCOUNT, username);
        Response<String> response = client.postAndGet(command, Response.class, (Class<String>)String.class);
        return Double.parseDouble(response.getDatum());
    }

    protected Image getProfileImage(String username) {
        Command<String> command = new Command<>("get user image", Command.HandleType.PICTURE_GET, username);
        return client.getImage(command);
    }

    protected void addSeenToProduct(String productID) {
        Command<String> command = new Command<>("add seen", Command.HandleType.PRODUCT, productID);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    protected Product getProductByID(String ID, String productType) {
        Command<String> command = new Command<>("get " + productType, Command.HandleType.PRODUCT, ID);
        Response<Product> response = client.postAndGet(command, Response.class, (Class<Product>)Product.class);
        return response.getDatum();
    }

    protected Image getProductImageByID(String productID, int imageNumber, String productType) {
        Command<String> productImageCommand = new Command<>("get " + productType + " image-1", Command.HandleType.PICTURE_GET, productID);
        return client.getImage(productImageCommand);
    }

    protected Off getOffByID(String ID, String offType) {
        Command<String> command = new Command<>("get " + offType, Command.HandleType.SALE, ID);
        Response<Off> response = client.postAndGet(command, Response.class, (Class<Off>)Off.class);
        return response.getDatum();
    }

    protected Response removeProductByID(String productID, String productType) {
        Command<String> command = new Command<>("remove " + productType, Command.HandleType.PRODUCT, productID);
        return client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    protected boolean isProductPurchasedByCustomer(String productID, String customerUsername) {
        Command<String> command = new Command<>("is product purchased by customer", Command.HandleType.PRODUCT, productID, customerUsername);
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        return response.getDatum();
    }

    protected TreeItem<Category> getCategoryTableRoot() {
        ArrayList<Category> allCategories = getAllCategoriesAsArray();
        TreeItem<Category> rootCategory = new TreeItem<>(allCategories.get(0));
        setSubCategories(rootCategory, allCategories.get(0).getName(), allCategories);
        return rootCategory;
    }

    private void setSubCategories(TreeItem<Category> parentCategoryTreeItem, String parentName, ArrayList<Category> allCategories) {
        for (Category category : allCategories) {
            if(category.getParentCategory() != null && category.getParentCategory().equals(parentName)) {
                TreeItem<Category> subCategoryTreeItem = new TreeItem<>(category);
                parentCategoryTreeItem.getChildren().addAll(subCategoryTreeItem);
                setSubCategories(subCategoryTreeItem, category.getName(), allCategories);
            }
        }
    }

    private ArrayList<Category> getAllCategoriesAsArray() {
        Command command = new Command("get all categories", Command.HandleType.SALE);
        Response<Category> categoryResponse = client.postAndGet(command, Response.class, (Class<Category>)Category.class);
        return new ArrayList<>(categoryResponse.getData());
    }

    protected boolean isOffEditing(String offID) {
        Command<String> command = new Command<>("is off edit", Command.HandleType.SALE, (offID == null ? "" : offID));
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        return response.getDatum();
    }

    protected Command<String> getOffImageCommand(String offID, boolean isEditing) {
        Command<String> command = new Command<>((isEditing ? "get editing off image" : "get off image"), Command.HandleType.PICTURE_GET, (offID == null ? "" : offID));
        return command;
    }

    protected boolean doesOffHaveImage(String offID, boolean isEditing) {
        Command<String> command = new Command<>((isEditing ? "does editing off have image" : "does off have image"), Command.HandleType.SALE, (offID == null ? "" : offID));
        Response<Boolean> response = client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class);
        return response.getDatum();
    }

    protected void setOffListic(boolean isOffListic) {
        Command<Boolean> command = new Command<>("is off listic", Command.HandleType.SALE, isOffListic);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    protected void setComparingProduct(String productID, int i) {
        Command<String> command = new Command<>("set " + (i == 1 ? "first" : "second") + " comparing product", Command.HandleType.GENERAL, productID);
        client.postAndGet(command, Response.class, (Class<Object>)Object.class);
    }

    protected List<Product> getAllComparingProducts() {
        Command command = new Command("get all comparing products", GENERAL);
        Response<Product> response = client.postAndGet(command, Response.class, (Class<Product>)Product.class);
        return response.getData();
    }

    protected String getWage() {
        Command command = new Command("get wage", Command.HandleType.ACCOUNT);
        Response<Double> response = client.postAndGet(command, Response.class, (Class<Double>)Double.class);
        return getSmoothDoubleFormat(response.getDatum());
    }

    protected String getMinimumWallet() {
        Command command = new Command("get minimum wallet", Command.HandleType.ACCOUNT);
        Response<Double> response = client.postAndGet(command, Response.class, (Class<Double>)Double.class);
        return getSmoothDoubleFormat(response.getDatum());
    }

    protected boolean doesProductHaveFile(String productID) {
        Command<String> command = new Command<>("does product have file", PRODUCT, productID);
        return client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class).getDatum();
    }

    protected boolean doesEditingProductHaveFile(String productID) {
        Command<String> command = new Command<>("does edit product have file", PRODUCT, productID);
        return client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class).getDatum();
    }

    protected boolean isSupporterAvailable(String supporterUsername) {
        Command<String> command = new Command<>("is supporter available", ACCOUNT, supporterUsername);
        return client.postAndGet(command, Response.class, (Class<Boolean>)Boolean.class).getDatum();
    }

}
