package ControlPanel;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class MusicHandler {

    private ArrayList<Song> playlist = new ArrayList<>();
    
    public MusicHandler() {
        // We leave this empty now. The playlist will be populated when the user selects folders.
    }

    // Call this method via a button click to let the user select their folders
    public void loadDynamicPlaylist() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // 1. Get Music Folder
        chooser.setDialogTitle("Select Your Music Folder");
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File musicFolder = chooser.getSelectedFile();

            // 2. Get Image Folder
            chooser.setDialogTitle("Select Your Corresponding Images Folder");
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File imageFolder = chooser.getSelectedFile();
                
                // 3. Match and Connect
                scanAndMatchFiles(musicFolder, imageFolder);
            }
        }
    }

    private void scanAndMatchFiles(File musicDir, File imageDir) {
        playlist.clear(); // Clear out any old songs

        // Filter out everything except .mp3 and .wav files
        File[] audioFiles = musicDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav")
        );
        
        if (audioFiles == null || audioFiles.length == 0) {
            JOptionPane.showMessageDialog(null, "No audio files found in the selected directory.");
            return;
        }

for (File audioFile : audioFiles) {
            String fileName = audioFile.getName();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            // Image matching logic remains the same
            File imgPng = new File(imageDir, baseName + ".png");
            File imgJpg = new File(imageDir, baseName + ".jpg");

            String finalImagePath;
            if (imgPng.exists()) {
                finalImagePath = imgPng.getAbsolutePath();
            } else if (imgJpg.exists()) {
                finalImagePath = imgJpg.getAbsolutePath();
            } else {
                finalImagePath = "NO_IMAGE"; 
            }

            // --- NEW METADATA EXTRACTION LOGIC ---
            String trackArtist = "Unknown Artist";
            String trackTitle = baseName; // Default to filename just in case

            try {
                // Ask the AudioSystem to read the file's properties (requires mp3spi.jar)
                AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
                Map<String, Object> properties = baseFileFormat.properties();

                // Look for the "author" (Artist) tag
                if (properties.containsKey("author")) {
                    trackArtist = ((String) properties.get("author")).trim();
                }
                // Look for the real "title" tag
                if (properties.containsKey("title")) {
                    trackTitle = ((String) properties.get("title")).trim();
                }
            } catch (Exception e) {
                System.out.println("Could not read metadata for: " + fileName);
            }

            // Create the song using the extracted data!
            Song newSong = new Song(trackTitle, trackArtist, audioFile.getAbsolutePath(), finalImagePath);
            playlist.add(newSong);
        }
        
        System.out.println("Successfully loaded " + playlist.size() + " tracks.");
    }

    public ArrayList<Song> getPlaylist() {
        return playlist;
    }
}