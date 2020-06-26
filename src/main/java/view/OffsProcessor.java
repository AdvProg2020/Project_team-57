package view;

import com.jfoenix.controls.JFXButton;
import controller.account.CustomerControl;
import controller.product.ProductControl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.existence.Off;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OffsProcessor implements Initializable {
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
    }

    private void initGridPaneGraphic() {
        gridPane.getChildren().clear();
        int number = 0;
        int offIterator = (currentPage - 1) * PRODUCTS_NUMBER_PER_PAGE;
        gridPane.setAlignment(Pos.CENTER);
        while (number < PRODUCTS_NUMBER_PER_PAGE && offIterator < productsNumber) {
            gridPane.add(getOffPane(offs.get(offIterator)), number % (PRODUCTS_NUMBER_PER_PAGE / 2), number / (PRODUCTS_NUMBER_PER_PAGE / 2));
            number++;
            offIterator++;
        }
    }

    private Pane getOffPane(final Off off/*,/* Long timeSecond */) {
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
        timeline.playFromStart();*/
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffPane.fxml"));
            Pane offPane = loader.load();
            OffsProcessor processor = loader.getController();
            processor.offImage.setImage(productControl.getOffImageByID(off.getOffID()));
            processor.offPercent.setText(off.getOffPercent() + "%");
            processor.offName.setStyle("-fx-font-family: Calibri; -fx-text-fill: #330939; -fx-font-size: 18px;");
            processor.offPercent.setStyle("-fx-font-family: Calibri; -fx-text-fill: #330939; -fx-font-size: 18px;");
            processor.offName.setText(off.getOffName());
            offPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
            offPane.setOnMouseEntered(event -> {
                processor.offImage.setOpacity(0.8);
                offPane.setStyle("-fx-cursor: hand; -fx-background-color: white; -fx-background-radius: 10px");
            });
            offPane.setOnMouseExited(event -> {
                processor.offImage.setOpacity(1);
                offPane.setStyle("-fx-cursor: inherit; -fx-background-color: white; -fx-background-radius: 10px");
            });
            offPane.setOnMouseClicked(event -> {
                productControl.setOffListic(true);
                productControl.setListicOffID(off.getOffID());
                try {
                    Parent root;
                    Main.getStage().getIcons().remove(0);
                    Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Market Logo.png")));
                    FXMLLoader newLoader = new FXMLLoader(Main.class.getResource("ProductsMenu.fxml"));
                    root = newLoader.load();
                    ProductsProcessor productsProcessor = newLoader.getController();
                    productsProcessor.initProcessor(ProductsProcessor.ProductsMenuType.MAIN_PRODUCTS);
                    Main.setScene( "Products Menu", root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return offPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
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

    public void showPreviousPage() {
        currentPage--;
        initPageNumberGraphic();
        initGridPaneGraphic();
    }

    public void showNextPage() {
        currentPage++;
        initGridPaneGraphic();
        initPageNumberGraphic();
    }

    public void openAccountMenu() {
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
