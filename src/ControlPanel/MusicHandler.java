package ControlPanel;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;

public class MusicHandler {

    private ArrayList<Song> playlist = new ArrayList<>();
    private final String SAVE_FILE = "vibezz_settings.txt"; // Where we save your paths
    
    public MusicHandler() {
        loadSavedPaths(); // Auto-load on boot!
    }

    public void loadDynamicPlaylist() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        chooser.setDialogTitle("Select Your Music Folder");
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File musicFolder = chooser.getSelectedFile();

            chooser.setDialogTitle("Select Your Corresponding Images Folder");
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File imageFolder = chooser.getSelectedFile();
                
                // Save paths so the user doesn't have to do this again
                savePaths(musicFolder.getAbsolutePath(), imageFolder.getAbsolutePath());
                scanAndMatchFiles(musicFolder, imageFolder);
            }
        }
    }

    private void savePaths(String musicPath, String imagePath) {
        try (PrintWriter out = new PrintWriter(SAVE_FILE)) {
            out.println(musicPath);
            out.println(imagePath);
        } catch (Exception e) {
            System.out.println("Could not save settings.");
        }
    }

    private void loadSavedPaths() {
        try {
            File file = new File(SAVE_FILE);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                String mPath = scanner.nextLine();
                String iPath = scanner.nextLine();
                scanner.close();
                scanAndMatchFiles(new File(mPath), new File(iPath));
            }
        } catch (Exception e) {
            System.out.println("No saved paths found or file is corrupted.");
        }
    }

    private void scanAndMatchFiles(File musicDir, File imageDir) {
        playlist.clear(); 

        File[] audioFiles = musicDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav")
        );
        
        if (audioFiles == null || audioFiles.length == 0) return;

        for (File audioFile : audioFiles) {
            String fileName = audioFile.getName();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            File imgPng = new File(imageDir, baseName + ".png");
            File imgJpg = new File(imageDir, baseName + ".jpg");

            String finalImagePath = imgPng.exists() ? imgPng.getAbsolutePath() : 
                                   (imgJpg.exists() ? imgJpg.getAbsolutePath() : "NO_IMAGE");

            String trackArtist = "Unknown Artist";
            String trackTitle = baseName; 
            String trackDuration = "0:00"; // Default duration

            try {
                AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
                Map<String, Object> properties = baseFileFormat.properties();

                if (properties.containsKey("author")) trackArtist = ((String) properties.get("author")).trim();
                if (properties.containsKey("title")) trackTitle = ((String) properties.get("title")).trim();
                
                // --- NEW: Calculate Duration ---
                if (properties.containsKey("duration")) {
                    Long microseconds = (Long) properties.get("duration");
                    int mili = (int) (microseconds / 1000);
                    int sec = (mili / 1000) % 60;
                    int min = (mili / 1000) / 60;
                    trackDuration = String.format("%d:%02d", min, sec);
                }
            } catch (Exception e) {}

            Song newSong = new Song(trackTitle, trackArtist, audioFile.getAbsolutePath(), finalImagePath, trackDuration);
            playlist.add(newSong);
        }
        
        // --- NEW: Alphabetize the playlist ---
        playlist.sort((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
    }

    public ArrayList<Song> getPlaylist() { return playlist; }
}