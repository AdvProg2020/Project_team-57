package client.view;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.model.existence.Account;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

public class AccountProcessor extends Processor{
    protected Account loggedInAccount;

    public ImageView startMediaButton;
    public ImageView nextMediaButton;
    public ImageView previousMediaButton;
    public Label mediaNameLabel;
    public Label mediaArtistLabel;
    public JFXProgressBar mediaProgressBar;

    public void logOutButton() {
        Optional<ButtonType> buttonType =
                new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure About Logging Out?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES) {
            stopMusics();
            subStages.forEach(stage -> {
                stage.close();
            });
            logOut();
            backToMainMenu();
        }

    }

    public void backToMainMenu() {
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource("WelcomeMenu.fxml"));
        } catch (IOException e) {
            //:)
        }
        stopMusics();
        Main.getStage().getIcons().remove(0);
        Main.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("Main Icon.png")));
        Main.setScene("Boos Market", root);
    }

    public void showProfileMenu() {
        try {
            loggedInAccount = getLoggedInAccount();

            if(loggedInAccount.getType().equals("Admin")) {
                if (!canOpenSubStage(loggedInAccount.getUsername() + " Profile", parentProcessor))
                    return;
            }
            else
                if(!canOpenSubStage(loggedInAccount.getUsername() + " Profile", this))
                return;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileMenu.fxml"));
            Parent root = loader.load();
            ProfileProcessor profileProcessor = loader.getController();
            profileProcessor.init(loggedInAccount, "ProfileMenu");
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.getIcons().add(new Image(getClass().getResourceAsStream("Profile Icon.png")));
            newStage.setResizable(false);
            newStage.setTitle(loggedInAccount.getUsername() + " Profile");
            if(loggedInAccount.getType().equals("Admin"))
                parentProcessor.addSubStage(newStage);
            else
                addSubStage(newStage);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isMusicPlaying = false;
    private ArrayList<Audio> audios;
    private ChangeMusicThread changeMusicThread = new ChangeMusicThread();
    private long musicCounter = 0;
    private int nextMusicK = 0;


    public static class Audio {
        private static ArrayList<Audio> adminAudios;
        private static ArrayList<Audio> vendorAudios;
        private static ArrayList<Audio> customerAudios;
        private String artist;
        private String name;
        private MediaPlayer music;

        private Audio(String artist, String name, MediaPlayer music) {
            this.artist = artist;
            this.name = name;
            this.music = music;
        }

        private static ArrayList<Audio> getAdminMusics() {
            if(adminAudios != null)
                return adminAudios;
            try {
                adminAudios = new ArrayList<>();
                adminAudios.add(makeAudio("Mohsen Chavoshi", "Dele Man", "Admin","Mohsen Chavoshi Dele Man.mp3"));
                adminAudios.add(makeAudio("Mohsen Chavoshi", "Amire Bi Gazand", "Admin","Mohsen Chavoshi Amire Bi Gazand.mp3"));
                return adminAudios;
            } catch (URISyntaxException e) {
                //:)
            }
            return new ArrayList<>();
        }
        public static ArrayList<Audio> getVendorMusics() {
            if(vendorAudios != null)
                return vendorAudios;
            try {
                vendorAudios = new ArrayList<>();
                vendorAudios.add(makeAudio("Benyamin Bahadori", "Bi Etena", "Vendor","Benyamin Bahadori - Bi Etena.mp3"));
                vendorAudios.add(makeAudio("Shadmehr Aghili", "Alamate Soal", "Vendor","Shadmehr-Aghili-Alamate-Soal.mp3"));
                return vendorAudios;
            } catch (URISyntaxException e) {
                //:)
            }
            return new ArrayList<>();
        }
        public static ArrayList<Audio> getCustomerMusics() {
            if(customerAudios != null)
                return customerAudios;
            try {
                customerAudios = new ArrayList<>();
                customerAudios.add(makeAudio("Shayea & Hidden", "Mosser", "Customer","Shayea-Mosser_FT_Mehrad_Hidden.mp3"));
                customerAudios.add(makeAudio("Imagine Dragons", "Believer", "Customer","Imagine Dragons - Believer.mp3"));
                customerAudios.add(makeAudio("Nico Vega", "Beast", "Customer","Beast (Extended Version).mp3"));
                return customerAudios;
            } catch (URISyntaxException e) {
                //:)
            }
            return new ArrayList<>();
        }
        private static Audio makeAudio(String artist, String name, String type, String fileName) throws URISyntaxException {
            return new Audio(artist, name, new MediaPlayer(
                    new Media(Main.class.getResource("Original SoundTracks\\" + type + "\\" + fileName).toURI().toString())));
        }

        public String getName() {
            return name;
        }

        public MediaPlayer getMusic() {
            return music;
        }

        public void stop() {
            music.stop();
        }

        public String getArtist() {
            return artist;
        }
    }

    public void initAudios() {
        switch (getType()) {
            case "Admin" :
                audios = Audio.getAdminMusics();
                break;
            case "Vendor" :
                audios = Audio.getVendorMusics();
                break;
            case "Customer" :
                audios = Audio.getCustomerMusics();
        }
        isMusicPlaying = false;
        musicCounter = 0;
        changeMusicThread = new ChangeMusicThread();
    }

    public void stopMusics() {
        if(audios != null) {
            for (Audio audio : audios) {
                audio.stop();
            }
        }
    }

//    public long getMusicCount() {
//        if(audios == null)
//            return 0;
//        return audios.size();
//    }

//    public void stopPlayingMusic() {
//        audios.get((int) (musicCounter % audios.size())).stop();
//    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

//    public void setMusicPlaying(boolean musicPlaying) {
//        isMusicPlaying = musicPlaying;
//    }

    public Audio getPlayingMusic() {
        return audios.get((int) (musicCounter % audios.size()));
    }

    public void setMusicCounter(int k) {
        musicCounter = ((k + musicCounter > -1) ? k + musicCounter : audios.size() - 1);
    }

    public MediaPlayer modifyPlayingMusicControl() {
        Audio mediaPlayer = audios.get((int) (musicCounter % audios.size()));
        if(!isMusicPlaying) {
            changeMusicThread.stopThread();
            changeMusicThread = new ChangeMusicThread(null, mediaPlayer, true);
        } else {
            changeMusicThread.stopThread();
            changeMusicThread = new ChangeMusicThread(mediaPlayer, null, false);
        }
        changeMusicThread.start();
        isMusicPlaying = !isMusicPlaying;
        return mediaPlayer.getMusic();
    }

    public MediaPlayer changeMusic(int nextMusicK) {
        Audio first = audios.get((int) (musicCounter % audios.size()));
        if(nextMusicK == -1 && audios.get((int) (musicCounter % audios.size())).getMusic().getCurrentTime().compareTo(Duration.seconds(2)) >= 0)   {
            setMusicCounter(0);
        } else
            setMusicCounter(nextMusicK);
        Audio second = audios.get((int) (musicCounter % audios.size()));
        if(isMusicPlaying) {
            changeMusicThread.stopThread();
            changeMusicThread = new ChangeMusicThread(first, second, true);
            changeMusicThread.start();
            return second.getMusic();
        }
        return null;
    }

    private static class ChangeMusicThread extends Thread {
        private Audio firstMediaPlayer;
        private Audio secondMediaPlayer;
        private boolean isStop = true;
        public ChangeMusicThread() { }

        public ChangeMusicThread(Audio firstMediaPlayer, Audio secondMediaPlayer, boolean isStop) {
            this.firstMediaPlayer = firstMediaPlayer;
            this.secondMediaPlayer = secondMediaPlayer;
            this.isStop = isStop;
        }

        public void stopThread() {
            if (firstMediaPlayer != null)
                firstMediaPlayer.getMusic().stop();
            super.stop();
        }

        @Override
        public void run() {
            try {
                if(firstMediaPlayer != null) {
                    while (firstMediaPlayer.getMusic().getVolume() > 0) {
                        firstMediaPlayer.getMusic().setVolume(firstMediaPlayer.getMusic().getVolume() - 0.01);
                        Thread.sleep(20);
                    }
                    if(isStop)
                        firstMediaPlayer.getMusic().stop();
                    else
                        firstMediaPlayer.getMusic().pause();
                }
                if(secondMediaPlayer != null) {
                    secondMediaPlayer.getMusic().play();
                    secondMediaPlayer.getMusic().setVolume(0);
                    while (secondMediaPlayer.getMusic().getVolume() < 1) {
                        secondMediaPlayer.getMusic().setVolume(secondMediaPlayer.getMusic().getVolume() + 0.01);
                        Thread.sleep(50);
                    }
                }
            } catch (Exception e) {
                //:)
            }

        }
    }


    //TODO(FOR MEDIA)
    public void modifyPlayingMusic() {
        modifyPlayingMusicControl().setOnEndOfMedia(() -> this.changeMusic(null));
        initMusicPlayer();
    }


    public void changeMusic(MouseEvent mouseEvent) {
        int k;
        if(mouseEvent != null) {
           k = (((ImageView)mouseEvent.getSource()).getId().equalsIgnoreCase("nextMediaButton") ? 1 : -1);
        } else {
            k = 1;
        }
        MediaPlayer mediaPlayer = changeMusic(k);
        if(mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(() -> this.changeMusic(null));
        }
        initMusicPlayer();
    }

    protected void initMusicPlayer() {
        setStartMediaButton();
        mediaArtistLabel.setText(getPlayingMusic().getArtist());
        mediaNameLabel.setText(getPlayingMusic().getName());
        if(isMusicPlaying())
            mediaProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        else
            mediaProgressBar.setProgress(0);
    }

    private void setStartMediaButton() {
        String url = getType() + " " + (isMusicPlaying() ? "Pause" : "Start") + ".png";
        startMediaButton.setImage(new Image(IMAGE_FOLDER_URL + "Icons/MediaIcons/" + url));
    }

}
