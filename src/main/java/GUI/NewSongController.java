package GUI;

import BE.Song;
import DAL.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class NewSongController {

    @FXML
    private TextField txtTitle, txtArtist, txtCategory, txtAudioFile;
    @FXML
    private Button btnBrowse, btnAddSong;

    private Stage stage;
    private SongDAO songDAO;

    public NewSongController() {
        try {
            songDAO = new SongDAO();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
        );

        // Sæt standard folder, fx brugermappen
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            txtAudioFile.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void addSong() {
        if (txtTitle.getText().isEmpty() || txtArtist.getText().isEmpty() ||
                txtCategory.getText().isEmpty() || txtAudioFile.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill all fields and select a file.");
            alert.showAndWait();
            return;
        }

        File audioFile = new File(txtAudioFile.getText());
        if (!audioFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Audio file not found.");
            alert.showAndWait();
            return;
        }

        try {
            byte[] audioData = Files.readAllBytes(audioFile.toPath());

            // Beregn duration
            int durationInSeconds = 0;
            try {
                Media media = new Media(audioFile.toURI().toString());
                MediaPlayer player = new MediaPlayer(media);
                // Brug media.getDuration() efter player er klar
                player.setOnReady(() -> {
                    int dur = (int) media.getDuration().toSeconds();
                    Song newSong = new Song(
                            0,
                            txtTitle.getText(),
                            txtArtist.getText(),
                            txtCategory.getText(),
                            audioData,
                            dur
                    );
                    songDAO.createSong(newSong);

                    // Luk vinduet efter sangen er oprettet
                    if (stage != null) stage.close();
                });
            } catch (Exception e) {
                // Hvis MediaPlayer fejler, gem med duration = 0
                Song newSong = new Song(
                        0,
                        txtTitle.getText(),
                        txtArtist.getText(),
                        txtCategory.getText(),
                        audioData,
                        0
                );
                songDAO.createSong(newSong);
                if (stage != null) stage.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to read audio file.");
            alert.showAndWait();
        }
    }


}
