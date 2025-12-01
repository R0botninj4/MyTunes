package BLL;

import BE.PlayList;
import DAL.PlayListDAO;

import java.util.List;

public class PlayListManager {

    private final PlayListDAO dao;

    public PlayListManager() throws Exception {
        dao = new PlayListDAO();
    }

    public List<PlayList> getAllPlaylists() throws Exception {
        return dao.getAllPlaylists();
    }

    public PlayList createPlaylist(String name) throws Exception {
        return dao.createPlaylist(name);
    }

    public void deletePlaylist(int id) throws Exception {
        dao.deletePlaylist(id);
    }

    // ----------------------------
    // TILFØJ RENAMING
    // ----------------------------
    public void renamePlaylist(int id, String newName) throws Exception {
        dao.renamePlaylist(id, newName);
    }

    public void addSongToPlaylist(int playlistId, int songId) throws Exception {
        dao.addSongToPlaylist(playlistId, songId);
    }

    public void removeSongFromPlaylist(int playlistId, int songId) throws Exception {
        dao.removeSongFromPlaylist(playlistId, songId);
    }


}
