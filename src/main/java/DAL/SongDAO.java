package DAL;

import BE.Song;
import DAL.DB.DBConnector;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    private final DBConnector dbConnector;

    public SongDAO() throws IOException {
        dbConnector = new DBConnector();
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT songId, title, artist, category, AudioData, DurationSeconds FROM Songs";

        try (Connection con = dbConnector.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Song s = new Song(
                        rs.getInt("songId"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("category"),
                        rs.getBytes("AudioData"),
                        rs.getObject("DurationSeconds") != null ? rs.getInt("DurationSeconds") : null
                );
                songs.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }



    public void createSong(Song song) {
        String sql = "INSERT INTO Songs(title, artist, category, AudioData, DurationSeconds) VALUES(?,?,?,?,?)";

        try (Connection con = dbConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getCategory());
            ps.setBytes(4, song.getAudioData());
            if (song.getDurationSeconds() != null) ps.setInt(5, song.getDurationSeconds());
            else ps.setNull(5, java.sql.Types.INTEGER);

            ps.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void deleteSong(int id) {
        String sql = "DELETE FROM Songs WHERE songId = ?";

        try (Connection con = dbConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSong(Song song) {
        String sql = "UPDATE Songs SET title=?, artist=?, category=?, AudioData=?, DurationSeconds=? WHERE songId=?";
        try (Connection con = dbConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getCategory());
            ps.setBytes(4, song.getAudioData());
            if (song.getDurationSeconds() != null) ps.setInt(5, song.getDurationSeconds());
            else ps.setNull(5, java.sql.Types.INTEGER);
            ps.setInt(6, song.getSongId());

            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void updateSongMetadata(Song song) {
        try (Connection conn = dbConnector.getConnection()) {

            String sql = "UPDATE Songs " +
                    "SET title = ?, artist = ?, category = ? " +
                    "WHERE SongID = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getCategory());
            stmt.setInt(4, song.getSongId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Song getSongById(int songId) {
        String sql = "SELECT songId, title, artist, category, AudioData , DurationSeconds FROM Songs WHERE songId = ?";
        try (Connection con = dbConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, songId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Song(
                        rs.getInt("songId"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("category"),
                        rs.getBytes("AudioData"),
                        rs.getInt("DurationSeconds")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // hvis sangen ikke findes
    }

//playlist
public List<Song> getSongsByPlaylist(int playlistId) {
    List<Song> songs = new ArrayList<>();
    String sql = "SELECT s.SongID, s.Title, s.Artist, s.Category, s.AudioData, s.DurationSeconds " +
            "FROM Songs s " +
            "JOIN Playlist_Songs ps ON s.SongID = ps.song_id " +
            "WHERE ps.playlist_id = ?";

    try (Connection con = dbConnector.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, playlistId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Song s = new Song(
                    rs.getInt("SongID"),
                    rs.getString("Title"),
                    rs.getString("Artist"),
                    rs.getString("Category"),
                    rs.getBytes("AudioData"),
                    rs.getObject("DurationSeconds") != null ? rs.getInt("DurationSeconds") : null
            );
            songs.add(s);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return songs;
}

}
