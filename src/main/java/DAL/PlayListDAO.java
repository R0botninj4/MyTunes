package DAL;

import BE.PlayList;
import DAL.DB.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayListDAO {

    private final DBConnector dbConnector;

    public PlayListDAO() throws Exception {
        dbConnector = new DBConnector();
    }

    // -----------------------------
    // GET ALL PLAYLISTS
    // -----------------------------
    public List<PlayList> getAllPlaylists() throws Exception {
        List<PlayList> playlists = new ArrayList<>();

        String sql = "SELECT ID, Name FROM PlayList";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");

                playlists.add(new PlayList(id, name));
            }
        }
        return playlists;
    }

    // -----------------------------
    // CREATE PLAYLIST
    // -----------------------------
    public PlayList createPlaylist(String name) throws Exception {
        String sql = "INSERT INTO PlayList (Name) VALUES (?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                return new PlayList(id, name);
            }
        }
        return null;
    }

    // -----------------------------
    // DELETE PLAYLIST
    // -----------------------------
    public void deletePlaylist(int id) throws Exception {
        try (Connection conn = dbConnector.getConnection()) {
            // 1. Slet alle sange i playlisten
            String sqlDeleteSongs = "DELETE FROM Playlist_Songs WHERE playlist_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteSongs)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // 2. Slet selve playlisten
            String sqlDeletePlaylist = "DELETE FROM PlayList WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeletePlaylist)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }
    }

    // -----------------------------
    // RENAME PLAYLIST
    // -----------------------------
    public void renamePlaylist(int id, String newName) throws Exception {
        String sql = "UPDATE PlayList SET Name = ? WHERE ID = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
    // -----------------------------
    // SONG PLAYLIST
    // -----------------------------


    // TILFØJ SANG TIL PLAYLIST
    public void addSongToPlaylist(int playlistId, int songId) throws Exception {
        String sql = "INSERT INTO Playlist_Songs (playlist_id, song_id) VALUES (?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
        }
    }

    // FJERN SANG FRA PLAYLIST
    public void removeSongFromPlaylist(int playlistId, int songId) throws Exception {
        String sql = "DELETE FROM Playlist_Songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
        }
    }

}
