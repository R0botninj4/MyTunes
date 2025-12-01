package DAL;

import DAL.DB.DBConnector;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OneTimeSongUploader {

    private static final String SONG_FOLDER = "E:\\Songs\\"; // ← DIN mappe med MP3

    public static void main(String[] args) {

        // Start JavaFX Toolkit uden GUI
        new JFXPanel();

        try {
            DBConnector dbConnector = new DBConnector();

            try (Connection conn = dbConnector.getConnection()) {

                File folder = new File(SONG_FOLDER);

                File[] mp3Files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

                if (mp3Files == null || mp3Files.length == 0) {
                    System.out.println("⚠ Der blev ikke fundet nogen MP3-filer!");
                    return;
                }

                for (File file : mp3Files) {
                    uploadSong(conn, file);
                }

                System.out.println("\n✔ Alle sange uploadet!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void uploadSong(Connection conn, File file) {

        try {
            String fileName = file.getName().replace(".mp3", "");

            fileName = fileName.replace("_SPOTISAVER", "").trim();

            String[] parts = fileName.split(" - ");

            String artist = parts.length > 1 ? parts[0].trim() : "Unknown Artist";
            String title  = parts.length > 1 ? parts[1].trim() : fileName.trim();

            String category = "Pop"; // default – kan ændres

            byte[] audioBytes = Files.readAllBytes(file.toPath());

            // ⭐ Hent korrekt MP3-længde via MediaPlayer
            int duration = getDurationUsingMediaPlayer(file);

            String sql = "INSERT INTO Songs (Title, Artist, Category, AudioData, DurationSeconds) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, artist);
            ps.setString(3, category);
            ps.setBytes(4, audioBytes);
            ps.setInt(5, duration);

            ps.executeUpdate();
            ps.close();

            System.out.println("✔ Uploadet: " + artist + " - " + title + " (" + duration + " sek)");

        } catch (Exception e) {
            System.out.println("✖ Fejl ved " + file.getName());
            e.printStackTrace();
        }
    }


    // ============================
    // ⭐ MEDIA PLAYER DURATION
    // ============================
    private static int getDurationUsingMediaPlayer(File file) {
        final int[] secondsOut = {0};
        final Object lock = new Object();

        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            player.setOnReady(() -> {
                double sec = media.getDuration().toSeconds();
                secondsOut[0] = (int) sec;

                synchronized (lock) {
                    lock.notify();
                }
            });

            synchronized (lock) {
                lock.wait();  // vent til MediaPlayer er klar
            }

        } catch (Exception e) {
            System.out.println("Fejl i MediaPlayer duration for: " + file.getName());
        }

        return secondsOut[0];
    }
}
