package ControlPanel;

import java.io.File;
import java.util.ArrayList;
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
            // Chop off the extension (e.g., "edamame.mp3" -> "edamame")
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            // Check for matching image files (checking both png and jpg just in case)
            File imgPng = new File(imageDir, baseName + ".png");
            File imgJpg = new File(imageDir, baseName + ".jpg");

            String finalImagePath;
            if (imgPng.exists()) {
                finalImagePath = imgPng.getAbsolutePath();
            } else if (imgJpg.exists()) {
                finalImagePath = imgJpg.getAbsolutePath();
            } else {
                // If no image is found, you can set a default fallback image path here
                finalImagePath = "NO_IMAGE"; 
            }

            // Note: Since we are loading dynamically, we use the base filename as the title.
            // (Extracting real artist/title data requires ID3 tag parsing libraries).
            Song newSong = new Song(baseName, "Unknown Artist", audioFile.getAbsolutePath(), finalImagePath);
            playlist.add(newSong);
        }
        
        System.out.println("Successfully loaded " + playlist.size() + " tracks.");
    }

    public ArrayList<Song> getPlaylist() {
        return playlist;
    }
}