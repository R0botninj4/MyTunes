package BE;

public class PlayList {
    private int id;
    private String name;
    private int numberOfSongs;  // antal sange i playlist
    private String duration;    // samlet tid i "mm:ss" format

    public PlayList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumberOfSongs() { return numberOfSongs; }
    public void setNumberOfSongs(int numberOfSongs) { this.numberOfSongs = numberOfSongs; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    @Override
    public String toString() {
        return name;
    }
}
