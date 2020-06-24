package view;

import com.jfoenix.controls.JFXButton;
import controller.account.CustomerControl;
import controller.product.ProductControl;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.existence.Off;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OffListProcessor implements Initializable {
    private static final int PRODUCT_PANE_HEIGHT = 250;
    private static final int PRODUCT_PANE_WIDTH = 200;
    private static final int PRODUCTS_NUMBER_PER_PAGE = 8;

    private final CustomerControl customerControl = CustomerControl.getController();
    private final ProductControl productControl = ProductControl.getController();
    private final ArrayList<Off> offs = new ArrayList<>(customerControl.getAllShowingOffs());
    private ArrayList<Long> timeSeconds = new ArrayList<>();
    private int currentPage = 1;
    private final int productsNumber = offs.size();
    private final int numberOfPages = (int) Math.floor(productsNumber / PRODUCTS_NUMBER_PER_PAGE) + 1;

    public JFXButton backButton;
    public JFXButton accountMenuButton;
    public GridPane gridPane;
    public ImageView previousPage;
    public Label pageNumberLabel;
    public ImageView nextPage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPageNumberGraphic();
        initGridPaneGraphic();
    }

    private void initGridPaneGraphic() {
        gridPane.getChildren().clear();
        int number = 0;
        int offIterator = (currentPage - 1) * 8;
        while (number < PRODUCTS_NUMBER_PER_PAGE && offIterator < productsNumber) {
            gridPane.add(getOffPane(offs.get(offIterator)), number % 4, number / 4);
            number++;
            offIterator++;
        }
    }

    private Pane getOffPane(final Off off/*,/* Long timeSecond */) {
        Pane pane = new Pane();
        pane.setPrefHeight(PRODUCT_PANE_HEIGHT);
        pane.setPrefWidth(PRODUCT_PANE_WIDTH);
        ImageView imageView = new ImageView(productControl.getOffImageByID(off.getOffID()));
        Text name = new Text(off.getOffName());
        name.setStyle("-fx-font-family: Calibri; -fx-font-size: 18;-fx-fill: #2F999F;");
        HBox hBox = new HBox(imageView, name);
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
        VBox vBox = new VBox(hBox, percent);
        pane.getChildren().add(vBox);
        return pane;
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
