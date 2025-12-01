package GUI;

import BE.PlayList;
import BE.Song;
import BLL.PlayListManager;
import DAL.SongDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class MainController {

    // ---------------------- MEDIA ----------------------
    @FXML private Button btnPlayPause, btnStop, btnNext, btnPrev;
    @FXML private Slider volumeSlider;
    @FXML private Label lblNowPlaying;

    // ---------------------- FILTER ----------------------
    @FXML private TextField txtFilter;

    // ---------------------- TABLES ----------------------
    @FXML private TableView<Song> tableSongs;
    @FXML private TableColumn<Song, String> colTitle, colArtist, colCategory, colTime;

    @FXML private TableView<PlayList> tblPlaylists;
    @FXML private TableColumn<PlayList, String> colPlaylistName;
    @FXML private TableColumn<PlayList, Integer> colPlaylistSongs;
    @FXML private TableColumn<PlayList, String> colPlaylistTime;

    @FXML private TableView<Song> tableSongsOnPlaylist;
    @FXML private TableColumn<Song, String> colPlaylistSongTitle, colPlaylistSongArtist, colPlaylistSongTime;

    // ---------------------- DAO / MANAGER ----------------------
    private SongDAO songDAO;
    private PlayListManager playlistManager;

    // ---------------------- OBSERVABLE LISTS ----------------------
    private ObservableList<Song> songList;
    private ObservableList<PlayList> playlistList;
    private ObservableList<Song> playlistSongList;

    // ---------------------- PLAYBACK ----------------------
    private PlayList currentPlaylist;
    private int currentSongIndex = -1;
    private MediaPlayer mediaPlayer;
    private Song currentSong;

    @FXML
    public void initialize() {
        try {
            songDAO = new SongDAO();
            playlistManager = new PlayListManager();
        } catch (Exception e) { e.printStackTrace(); }
        // ---------------------- FILTER LISTENER ----------------------
        txtFilter.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));


        // ---------------------- SETUP SONGS TABLE ----------------------
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("duration"));
        songList = FXCollections.observableArrayList();
        tableSongs.setItems(songList);

        // ---------------------- SETUP PLAYLIST TABLE ----------------------
        colPlaylistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlaylistSongs.setCellValueFactory(new PropertyValueFactory<>("numberOfSongs"));
        colPlaylistTime.setCellValueFactory(new PropertyValueFactory<>("duration"));
        playlistList = FXCollections.observableArrayList();
        tblPlaylists.setItems(playlistList);

        // ---------------------- SETUP SONGS ON PLAYLIST TABLE ----------------------
        colPlaylistSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPlaylistSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colPlaylistSongTime.setCellValueFactory(new PropertyValueFactory<>("duration"));
        playlistSongList = FXCollections.observableArrayList();
        tableSongsOnPlaylist.setItems(playlistSongList);

        // ---------------------- LOAD DATA ----------------------
        loadSongs();
        loadPlaylists();

        // ---------------------- VOLUME ----------------------
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(newVal.doubleValue());
        });

        // ---------------------- PLAYLIST SELECTION ----------------------
        tblPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadSongsFromPlaylist(newVal);
        });

        // ---------------------- DOUBLE-CLICK EVENTS ----------------------
        tblPlaylists.setRowFactory(tv -> createPlaylistRow());
        tableSongsOnPlaylist.setRowFactory(tv -> createSongRow(tableSongsOnPlaylist));
        tableSongs.setRowFactory(tv -> createSongRow(tableSongs));

        // ---------------------- SELECTION LISTENERS ----------------------
        tableSongsOnPlaylist.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) currentSong = newVal;
        });

        tableSongs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) currentSong = newVal;
        });
    }
    // --- Filter-------
    private void applyFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            tableSongs.setItems(songList);
            tableSongsOnPlaylist.setItems(playlistSongList);
            return;
        }

        String lower = filter.toLowerCase();

        // --- Filter ALLE sange ---
        ObservableList<Song> filteredSongs = FXCollections.observableArrayList();
        for (Song s : songList) {
            if (s.getTitle().toLowerCase().contains(lower) ||
                    s.getArtist().toLowerCase().contains(lower) ||
                    (s.getCategory() != null && s.getCategory().toLowerCase().contains(lower))) {
                filteredSongs.add(s);
            }
        }

        // --- Filter SANGE PÅ PLAYLIST ---
        ObservableList<Song> filteredPlaylistSongs = FXCollections.observableArrayList();
        for (Song s : playlistSongList) {
            if (s.getTitle().toLowerCase().contains(lower) ||
                    s.getArtist().toLowerCase().contains(lower)) {
                filteredPlaylistSongs.add(s);
            }
        }

        tableSongs.setItems(filteredSongs);
        tableSongsOnPlaylist.setItems(filteredPlaylistSongs);
    }

    // ---------------------- ROW FACTORIES ----------------------
    private TableRow<PlayList> createPlaylistRow() {
        TableRow<PlayList> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !row.isEmpty()) {
                playFirstSongInPlaylist(row.getItem());
            }
        });
        return row;
    }

    private TableRow<Song> createSongRow(TableView<Song> table) {
        TableRow<Song> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !row.isEmpty()) {
                playSong(row.getItem());
            }
        });
        return row;
    }

    // ---------------------- LOAD DATA ----------------------
    private void loadSongs() {
        try {
            songList.clear();
            List<Song> songs = songDAO.getAllSongs();
            songs.forEach(s -> {
                if (s.getDurationSeconds() == null) {
                    s.setDurationSeconds(0); // default 0 sekunder
                }
                // fjern setDuration(String), getDuration() tager sig af visningen
            });
            songList.addAll(songs);
            tableSongs.refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadPlaylists() {
        try {
            playlistList.clear();
            List<PlayList> playlists = playlistManager.getAllPlaylists();
            for (PlayList p : playlists) {
                List<Song> songs = songDAO.getSongsByPlaylist(p.getId());
                p.setNumberOfSongs(songs.size());
                int totalSec = songs.stream().mapToInt(s -> s.getDurationSeconds() != null ? s.getDurationSeconds() : 0).sum();
                p.setDuration(String.format("%02d:%02d", totalSec / 60, totalSec % 60));
            }
            playlistList.addAll(playlists);
            tblPlaylists.refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }


    private void loadSongsFromPlaylist(PlayList playlist) {
        try {
            List<Song> songs = songDAO.getSongsByPlaylist(playlist.getId());
            songs.forEach(s -> {
                if (s.getDurationSeconds() == null) s.setDurationSeconds(0);
            });
            playlistSongList.setAll(songs);
            tableSongsOnPlaylist.refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ---------------------- PLAYBACK ----------------------
    @FXML private void togglePlayPause() {
        if (currentSong == null) {
            PlayList selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) playFirstSongInPlaylist(selectedPlaylist);
            return;
        }
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            btnPlayPause.setText("▶");
        } else {
            playSong(currentSong);
        }
    }

    @FXML private void stopSong() {
        if (mediaPlayer != null) mediaPlayer.stop();
        btnPlayPause.setText("▶");
    }

    private void playSong(Song song) {
        try {
            if (mediaPlayer != null) mediaPlayer.stop();
            File tempFile = File.createTempFile("song-", ".mp3");
            tempFile.deleteOnExit();
            Files.write(tempFile.toPath(), song.getAudioData());
            Media media = new Media(tempFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volumeSlider.getValue());
            mediaPlayer.setOnReady(() -> mediaPlayer.play());
            mediaPlayer.setOnEndOfMedia(this::playNextSong);
            lblNowPlaying.setText("Now Playing: " + song.getTitle());
            btnPlayPause.setText("⏸");
            currentSong = song;
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void playFirstSongInPlaylist(PlayList playlist) {
        try {
            List<Song> songs = songDAO.getSongsByPlaylist(playlist.getId());
            if (songs != null && !songs.isEmpty()) {
                currentPlaylist = playlist;
                playlistSongList.setAll(songs);
                currentSongIndex = 0;
                playSong(songs.get(0));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void playNextSong() {
        if (currentPlaylist == null || playlistSongList.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlistSongList.size();
        playSong(playlistSongList.get(currentSongIndex));
    }

    @FXML private void playPrevSong() {
        if (currentPlaylist == null || playlistSongList.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + playlistSongList.size()) % playlistSongList.size();
        playSong(playlistSongList.get(currentSongIndex));
    }

    // ---------------------- CRUD SONGS ----------------------
    @FXML
    private void btnNewSong() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mytunes/NewSong.fxml"));
            Parent root = loader.load();

            // Hent controlleren
            NewSongController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Create New Song");
            stage.setScene(new Scene(root));

            // Giv controlleren en reference til dette stage
            controller.setStage(stage);

            stage.showAndWait(); // venter til vinduet lukkes
            loadSongs(); // opdater sangene bagefter
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void btnEditSong() {
        Song selectedSong = tableSongs.getSelectionModel().getSelectedItem();
        if (selectedSong == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mytunes/EditSong.fxml"));
            Parent root = loader.load();
            EditSongController controller = loader.getController();
            controller.setSong(selectedSong, songDAO);
            Stage stage = new Stage();
            stage.setTitle("Edit Song");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadSongs();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void btnDeleteSong() {
        Song selected = tableSongs.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Vil du slette sangen: " + selected.getTitle() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Slet sang");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            songDAO.deleteSong(selected.getSongId());
            loadSongs();
        }
    }

    // ---------------------- CRUD PLAYLISTS ----------------------
    @FXML private void btnNewPlaylist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mytunes/NewPlaylist.fxml"));
            Parent root = loader.load();
            NewPlaylistController controller = loader.getController();
            controller.setPlaylistManager(playlistManager);
            Stage stage = new Stage();
            stage.setTitle("Create New Playlist");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();
            loadPlaylists();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void btnEditPlaylist() {
        PlayList selected = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mytunes/EditPlaylist.fxml"));
            Parent root = loader.load();
            EditPlaylistController controller = loader.getController();
            controller.setPlaylistManager(playlistManager);
            controller.setPlaylist(selected);
            Stage stage = new Stage();
            stage.setTitle("Edit Playlist");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();
            loadPlaylists();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void btnDeletePlaylist() {
        PlayList selected = tblPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Vil du slette playlisten: " + selected.getName() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Slet playlist");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playlistManager.deletePlaylist(selected.getId());
                loadPlaylists();
                playlistSongList.clear();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // ---------------------- PLAYLIST SONGS ----------------------
    @FXML private void btnMoveSong() {
        PlayList selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedSong = tableSongs.getSelectionModel().getSelectedItem();
        if (selectedPlaylist == null || selectedSong == null) return;
        try {
            playlistManager.addSongToPlaylist(selectedPlaylist.getId(), selectedSong.getSongId());
            playlistSongList.add(selectedSong);
            selectedPlaylist.setNumberOfSongs(selectedPlaylist.getNumberOfSongs() + 1);
            tblPlaylists.refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void btnDeleteFromPlaylist() {
        PlayList selectedPlaylist = tblPlaylists.getSelectionModel().getSelectedItem();
        Song selectedSong = tableSongsOnPlaylist.getSelectionModel().getSelectedItem();
        if (selectedPlaylist == null || selectedSong == null) return;
        try {
            playlistManager.removeSongFromPlaylist(selectedPlaylist.getId(), selectedSong.getSongId());
            playlistSongList.remove(selectedSong);
            selectedPlaylist.setNumberOfSongs(selectedPlaylist.getNumberOfSongs() - 1);
            tblPlaylists.refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ---------------------- MISC ----------------------
    @FXML private void btnClose() { System.exit(0); }
    @FXML private void tbnRefresh() { loadSongs(); loadPlaylists(); }
}
