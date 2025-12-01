package BLL;

import BE.Song;
import DAL.SongDAO;

import java.util.List;

public class SongManager {

    private SongDAO songDAO;

    public SongManager() throws Exception {
        songDAO = new SongDAO();
    }

    public List<Song> getAllSongs() throws Exception {
        return songDAO.getAllSongs();
    }
}
