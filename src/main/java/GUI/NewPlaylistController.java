package GUI;

import BLL.PlayListManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewPlaylistController {

    @FXML
    private TextField txtPlaylistName;

    private PlayListManager playlistManager; // BLL manager
    private Stage stage; // vinduet

    // Sæt PlayListManager udefra
    public void setPlaylistManager(PlayListManager manager) {
        this.playlistManager = manager;
    }

    // Sæt Stage udefra
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void btnSavePlaylist() {
        if (playlistManager == null) {
            System.out.println("ERROR: playlistManager is null!");
            return;
        }

        String name = txtPlaylistName.getText().trim();
        if (name.isEmpty()) return;

        try {
            playlistManager.createPlaylist(name); // Opret ny playlist
            if (stage != null) stage.close(); // luk vinduet
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnCancel() {
        if (stage != null) stage.close();
    }
}
