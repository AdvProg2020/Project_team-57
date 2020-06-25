package view;

import com.jfoenix.controls.JFXButton;
import controller.account.CustomerControl;
import controller.product.ProductControl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.existence.Off;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OffListProcessor implements Initializable {
    private static final int PRODUCT_PANE_HEIGHT = 250;
    private static final int PRODUCT_PANE_WIDTH = 200;
    private static final int PRODUCTS_NUMBER_PER_PAGE = 6;

    private final CustomerControl customerControl = CustomerControl.getController();
    private final ProductControl productControl = ProductControl.getController();
    private final ArrayList<Off> offs = new ArrayList<>(customerControl.getAllShowingOffs());
    private final ArrayList<Long> timeSeconds = new ArrayList<>();
    private int currentPage = 1;
    private final int productsNumber = offs.size();
    private final int numberOfPages = (int) Math.floor(productsNumber / PRODUCTS_NUMBER_PER_PAGE) + 1;
    /**
     * OffPane.fxml
     */
    public ImageView offImage;
    public Label offName;
    public Label offPercent;
    /**
     * OffList.fxml
     */
    public JFXButton backMainMenu;
    public JFXButton accountMenuButton;
    public GridPane gridPane;
    public ImageView previousPage;
    public Label pageNumberLabel;
    public ImageView nextPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.toString().contains("OffList.fxml")) {
            initPageNumberGraphic();
            initGridPaneGraphic();
        }
        /*Stop[] stops = new Stop[]{
                new Stop(0, Color.valueOf("#b0bec5")),
                new Stop(0.3, Color.valueOf("#cfd8dc")),
                new Stop(0.6, Color.valueOf("#b0bec5")),
                new Stop(1, Color.valueOf("#90a4ae"))
        };
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);
        gridPane.setBackground(new Background(backgroundFill));*/
    }

    private void initGridPaneGraphic() {
        gridPane.getChildren().clear();
        int number = 0;
        int offIterator = (currentPage - 1) * PRODUCTS_NUMBER_PER_PAGE;
        gridPane.setAlignment(Pos.CENTER);
        while (number < PRODUCTS_NUMBER_PER_PAGE && offIterator < productsNumber) {
            gridPane.add(getOffPane(offs.get(offIterator)), number % 3, number / 3);
            number++;
            offIterator++;
        }
    }

    private Pane getOffPane(final Off off/*,/* Long timeSecond */) {
        /*Pane pane = new Pane();
        pane.setStyle("-fx-background-color: white");
        pane.setPrefHeight(PRODUCT_PANE_HEIGHT);
        pane.setPrefWidth(PRODUCT_PANE_WIDTH);
        pane.setPrefWidth(Region.USE_PREF_SIZE);
        ImageView imageView = new ImageView(productControl.getOffImageByID(off.getOffID()));
        imageView.setFitWidth(PRODUCT_PANE_WIDTH - 50);
        imageView.setFitHeight(PRODUCT_PANE_HEIGHT - 100);
        Text name = new Text(off.getOffName());
        name.setStyle("-fx-font-family: Calibri; -fx-font-size: 18;-fx-fill: #2F999F;");
        Text percent = new Text(off.getOffPercent() + "%");
        percent.setStyle("-fx-font-family: Calibri; -fx-font-size: 18;-fx-fill: firebrick;");
       /* Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        Label time = new Label(setTime(timeSecond));
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        event -> {
                            timeSecond--;
                            time.setText(setTime(timeSecond));
                            if (timeSecond.get() <= 0) {
                                timeline.stop();
                            }
                        }));
        timeline.playFromStart();
        VBox vBox = new VBox(hBox, percent, time);*/
        /*VBox vBox = new VBox(name, percent);
        HBox hBox = new HBox(imageView, vBox);
        pane.getChildren().add(hBox);
        return pane;*/
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffPane.fxml"));
            Pane offPane = loader.load();
            OffListProcessor processor = loader.getController();
            processor.offImage.setImage(productControl.getOffImageByID(off.getOffID()));
            processor.offImage.setOnMouseEntered(event -> processor.offImage.setStyle("-fx-cursor: hand"));
            processor.offImage.setOnMouseExited(event -> processor.offImage.setStyle("-fx-cursor: inherit"));
            processor.offPercent.setText(off.getOffPercent() + "%");
            processor.offName.setText(off.getOffName());
            offPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
            offPane.setOnMouseEntered(event -> processor.offImage.setOpacity(0.8));
            offPane.setOnMouseExited(event -> processor.offImage.setOpacity(1));
            processor.offImage.setOnMouseClicked(event -> {

            });
            return offPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String setTime(final long liftedTime) {
        StringBuilder stringBuilder = new StringBuilder("Day: " + liftedTime / 86400 + " Hour: " + (liftedTime % 86400) / 3600 + " Minute : ");
        stringBuilder.append((liftedTime % 3600) / 60).append(" Second: ").append(liftedTime % 60);
        return stringBuilder.toString();
    }

    private void initPageNumberGraphic() {
        pageNumberLabel.setText("Page " + currentPage + " of " + numberOfPages);
        if (numberOfPages == 1) {
            previousPage.setDisable(true);
            nextPage.setDisable(true);
        } else if (numberOfPages == currentPage && numberOfPages > 1) {
            nextPage.setDisable(true);
            previousPage.setDisable(false);
            previousPage.setOnMouseEntered(event -> previousPage.setOpacity(1));
            previousPage.setOnMouseExited(event -> previousPage.setOpacity(0.7));
        } else if (currentPage == 1 && numberOfPages > 1) {
            previousPage.setDisable(true);
            nextPage.setOnMouseEntered(event -> nextPage.setOpacity(1));
            nextPage.setOnMouseExited(event -> nextPage.setOpacity(0.7));
        } else if (numberOfPages > 1 && currentPage > 1 && currentPage < numberOfPages) {
            previousPage.setDisable(false);
            nextPage.setDisable(false);
            previousPage.setOnMouseEntered(event -> previousPage.setOpacity(1));
            previousPage.setOnMouseExited(event -> previousPage.setOpacity(0.7));
            nextPage.setOnMouseEntered(event -> nextPage.setOpacity(1));
            nextPage.setOnMouseExited(event -> nextPage.setOpacity(0.7));
        }
    }

    public void showPreviousPage(MouseEvent event) {
        currentPage--;
        initPageNumberGraphic();
        initGridPaneGraphic();
    }

    public void showNextPage(MouseEvent event) {
        currentPage++;
        initGridPaneGraphic();
        initPageNumberGraphic();
    }

    public void openAccountMenu(ActionEvent actionEvent) {
        new WelcomeProcessor().openAccountMenu();
    }

    public void backToMainMenu() {
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource("WelcomeMenu.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.getStage().getIcons().remove(0);
        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Main Icon.png")));
        Main.setScene("Boos Market", root);
    }
}
