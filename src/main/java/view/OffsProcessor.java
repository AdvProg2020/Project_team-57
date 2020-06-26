package view;

import com.jfoenix.controls.JFXButton;
import controller.account.CustomerControl;
import controller.product.ProductControl;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import model.existence.Off;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OffsProcessor implements Initializable {
    private static final int PRODUCTS_NUMBER_PER_PAGE = 6;
    private static final long ONE_MONTH_MILLIS = (long) 2.628e+9;
    private static final long ONE_DAY_MILLIS = (long) 8.64e+7;
    private static final long ONE_HOUR_MILLIS = (long) 3.6e+6;
    private static final long ONE_MINUTE_MILLIS = 60000;
    private static final long ONE_SECOND_MILLIS = 1000;



    private final CustomerControl customerControl = CustomerControl.getController();
    private final ProductControl productControl = ProductControl.getController();
    private final ArrayList<Off> offs = new ArrayList<>(customerControl.getAllShowingOffs());
    private final ArrayList<Long> timeSeconds = new ArrayList<>();
    public Label secondLabel;
    public Label minuteLabel;
    public Label hourLabel;
    public Label dayLabel;
    private int currentPage = 1;
    private final int productsNumber = offs.size();
    private final int numberOfPages = (((int)Math.ceil(((double)productsNumber)/PRODUCTS_NUMBER_PER_PAGE)));
    /**
     * OffPane.fxml
     */
    public Rectangle offImage;
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
    private Timer[] timers = Timer.getTimers();

    private static class Timer {
        AnimationTimer animationTimer;
        long timer = -1;

        static Timer[] getTimers() {
            Timer[] timers = new Timer[6];
            for (int i = 0; i < timers.length; i++) {
                timers[i] = new Timer();
            }
            return timers;
        }

        public void stop() {
            if(animationTimer != null)
                animationTimer.stop();
        }

        public void addAnimationTimer(AnimationTimer animationTimer) {
            this.animationTimer = animationTimer;
            this.animationTimer.start();
        }
    }
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
            gridPane.add(getOffPane(offs.get(offIterator), number), number % (PRODUCTS_NUMBER_PER_PAGE / 2), number / (PRODUCTS_NUMBER_PER_PAGE / 2));
            number++;
            offIterator++;
        }
    }

    private Pane getOffPane(final Off off, int number) {

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("OffPane.fxml"));
            Pane offPane = loader.load();
            OffsProcessor processor = loader.getController();
            processor.offImage.setFill(new ImagePattern(productControl.getOffImageByID(off.getOffID())));
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
            int day = (int) ((off.getFinishDate().getTime() - System.currentTimeMillis())/ONE_DAY_MILLIS);
            processor.dayLabel.setText("" + day);
            int hour = (int) (((off.getFinishDate().getTime() - System.currentTimeMillis()) - (day * ONE_DAY_MILLIS))/ONE_HOUR_MILLIS);
            processor.hourLabel.setText("" + hour);
            int minute = (int) ((((off.getFinishDate().getTime() - System.currentTimeMillis()) - (day * ONE_DAY_MILLIS)) - (hour * ONE_HOUR_MILLIS))/ONE_MINUTE_MILLIS);
            processor.minuteLabel.setText("" + minute);
            int second = (int) ((((off.getFinishDate().getTime() - System.currentTimeMillis()) - (day * ONE_DAY_MILLIS)) - (hour * ONE_HOUR_MILLIS) - (minute * ONE_MINUTE_MILLIS))/ONE_SECOND_MILLIS);
            processor.secondLabel.setText("" + second);
//            System.out.println("number: " + number);
            timers[number].addAnimationTimer(new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if(timers[number].timer == -1) {
                        timers[number].timer = now;
                    }
                    if(now - timers[number].timer > 1_000_000_000L) {
                        updateTimeShow(processor, this);
                        timers[number].timer = now;
                    }
                }
            });

            return offPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pane();
    }

    private void updateTimeShow(OffsProcessor processor, AnimationTimer animationTimer) {
        int day = Integer.parseInt(processor.dayLabel.getText());
        int hour = Integer.parseInt(processor.hourLabel.getText());
        int minute = Integer.parseInt(processor.minuteLabel.getText());
        int second = Integer.parseInt(processor.secondLabel.getText());
        second--;
        if(second < 0) {
            second = 59;
            minute--;
            if(minute < 0) {
                minute = 59;
                hour--;
                if(hour < 0) {
                    hour = 23;
                    day--;
                    if(day < 0) {
                        day = 0;
                        animationTimer.stop();
                    }
                }
            }
        }
        processor.dayLabel.setText("" + day);
        processor.hourLabel.setText("" + hour);
        processor.minuteLabel.setText("" + minute);
        processor.secondLabel.setText("" + second);
    }

    private String setTime(final long liftedTime) {
        StringBuilder stringBuilder = new StringBuilder("Day: " + liftedTime / 86400 + " Hour: " + (liftedTime % 86400) / 3600 + " Minute : ");
        stringBuilder.append((liftedTime % 3600) / 60).append(" Second: ").append(liftedTime % 60);
        return stringBuilder.toString();
    }

    private void initPageNumberGraphic() {
        pageNumberLabel.setText("Page " + currentPage + " of " + numberOfPages);
        System.out.println(currentPage + ", " + numberOfPages);
        if(numberOfPages == 0) {
            previousPage.setDisable(true);
            nextPage.setDisable(true);
        }
        if(currentPage == 1) {
            nextPage.setDisable(false);
            previousPage.setDisable(true);
            nextPage.setOnMouseEntered(event -> nextPage.setOpacity(1));
            nextPage.setOnMouseExited(event -> nextPage.setOpacity(0.7));
        }
        if(currentPage == numberOfPages) {
            nextPage.setDisable(true);
            previousPage.setDisable(false);
            previousPage.setOnMouseEntered(event -> previousPage.setOpacity(1));
            previousPage.setOnMouseExited(event -> previousPage.setOpacity(0.7));
        }
    }

    public void showPreviousPage() {
        System.out.println("Hello");
        for (int i = 0; i < timers.length; i++) {
            timers[i].stop();
        }
        timers = Timer.getTimers();
        currentPage--;
        initPageNumberGraphic();
        initGridPaneGraphic();
    }

    public void showNextPage() {
        System.out.println("Hello");
        for (int i = 0; i < timers.length; i++) {
            timers[i].stop();
        }
        timers = Timer.getTimers();
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
