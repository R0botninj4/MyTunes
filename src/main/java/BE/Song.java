package BE;

public class Song {
    private int songId;
    private String title;
    private String artist;
    private String category;
    private byte[] audioData;
    private Integer durationSeconds; // ny
    private String duration;         // formateret tekst, fx 03:45

    public Song(int songId, String title, String artist, String category, byte[] audioData, Integer durationSeconds) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.audioData = audioData;
        this.durationSeconds = durationSeconds;
        this.duration = durationSeconds != null ? String.format("%02d:%02d", durationSeconds / 60, durationSeconds % 60) : "00:00";
    }

    public int getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getCategory() { return category; }
    public byte[] getAudioData() { return audioData; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public String getDuration() { return duration; }

    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setCategory(String category) { this.category = category; }
    public void setAudioData(byte[] audioData) { this.audioData = audioData; }
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
        this.duration = durationSeconds != null ? String.format("%02d:%02d", durationSeconds / 60, durationSeconds % 60) : "00:00";
    }
}
