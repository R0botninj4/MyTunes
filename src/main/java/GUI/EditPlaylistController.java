package GUI;

import BE.PlayList;
import BLL.PlayListManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditPlaylistController {

    @FXML
    private TextField txtPlaylistName;

    private PlayListManager playlistManager;
    private Stage stage;
    private PlayList playlist; // den der skal redigeres

    public void setPlaylistManager(PlayListManager manager) {
        this.playlistManager = manager;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPlaylist(PlayList playlist) {
        this.playlist = playlist;
        txtPlaylistName.setText(playlist.getName());
    }

    @FXML
    private void btnSavePlaylist() {
        if (playlistManager == null || playlist == null) return;

        String newName = txtPlaylistName.getText().trim();
        if (newName.isEmpty()) return;

        try {
            playlistManager.renamePlaylist(playlist.getId(), newName);
            playlist.setName(newName); // opdater objektet

            if (stage != null) stage.close();

        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void btnCancel() {
        if (stage != null) stage.close();
    }
}
