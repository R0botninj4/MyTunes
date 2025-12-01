package GUI;

import BE.Song;
import DAL.SongDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditSongController implements Initializable {

    @FXML private TextField txtTitle;
    @FXML private TextField txtArtist;
    @FXML private TextField txtCategory;
    @FXML private TextField txtAudioPath;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private Song songToEdit;
    private SongDAO songDAO;
    private File selectedAudioFile;

    private boolean isDataSet = false; // sikrer at setSong først fylder data når FXML er loadet

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (songToEdit != null && !isDataSet) {
            fillFields();
        }
    }

    public void setSong(Song song, SongDAO dao) {
        this.songToEdit = song;
        this.songDAO = dao;

        // Hvis FXML allerede er loadet, fyld felterne med det samme
        if (txtTitle != null) {
            fillFields();
        }
    }

    private void fillFields() {
        txtTitle.setText(songToEdit.getTitle());
        txtArtist.setText(songToEdit.getArtist());
        txtCategory.setText(songToEdit.getCategory());
        txtAudioPath.setText("Existing file kept");
        isDataSet = true;
    }

    @FXML
    private void btnBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        selectedAudioFile = fileChooser.showOpenDialog(txtTitle.getScene().getWindow());

        if (selectedAudioFile != null) {
            txtAudioPath.setText(selectedAudioFile.getAbsolutePath());
        }
    }

    @FXML
    private void btnSave() {
        try {
            songToEdit.setTitle(txtTitle.getText());
            songToEdit.setArtist(txtArtist.getText());
            songToEdit.setCategory(txtCategory.getText());

            if (selectedAudioFile != null) {
                FileInputStream fis = new FileInputStream(selectedAudioFile);
                byte[] audioBytes = fis.readAllBytes();
                fis.close();
                songToEdit.setAudioData(audioBytes);

                // gem ALT (inkl. ny fil)
                songDAO.updateSong(songToEdit);

            } else {
                // gem KUN title, artist, category (audio unchanged)
                songDAO.updateSongMetadata(songToEdit);
            }

            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
