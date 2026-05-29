package ControlPanel;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;

public class MusicHandler {

    // --- GLOBAL LIBRARY SETTINGS ---
    private ArrayList<Song> playlist = new ArrayList<>();
    private final String SAVE_FILE = "vibezz_settings.txt"; 
    private final String PLAYLIST_SAVE_FILE = "vibezz_playlists.txt"; // Moved from UI
    
    // --- PLAYLIST & QUEUE DATA (Moved from UI) ---
    private ArrayList<String> createdPlaylists = new ArrayList<>();
    private Map<String, ArrayList<Song>> playlistSongs = new HashMap<>();
    private ArrayList<Song> activePlayingList = new ArrayList<>();

    public MusicHandler() {
        loadSavedPaths(); 
        loadPlaylists(); // Loads playlists automatically on boot
    }

    // --- PLAYLIST & QUEUE LOGIC ---
    public ArrayList<String> getCreatedPlaylists() { return createdPlaylists; }
    public Map<String, ArrayList<Song>> getPlaylistSongs() { return playlistSongs; }
    
    public ArrayList<Song> getActiveList() {
        return (activePlayingList != null && !activePlayingList.isEmpty()) ? activePlayingList : playlist;
    }
    
    public void setActiveList(ArrayList<Song> newList) {
        this.activePlayingList = newList;
    }

    public void savePlaylists() {
        try (PrintWriter out = new PrintWriter(PLAYLIST_SAVE_FILE)) {
            for (String pName : createdPlaylists) {
                out.println("[PLAYLIST] " + pName);
                ArrayList<Song> songs = playlistSongs.get(pName);
                if (songs != null) {
                    for (Song s : songs) {
                        out.println(s.getAudioPath()); 
                    }
                }
            }
        } catch (Exception e) { System.out.println("Failed to save playlists."); }
    }

    public void loadPlaylists() {
        createdPlaylists.clear();
        playlistSongs.clear();
        try {
            File file = new File(PLAYLIST_SAVE_FILE);
            if (!file.exists()) return;
            
            Scanner scanner = new Scanner(file);
            String currentPlaylist = null;
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                if (line.startsWith("[PLAYLIST] ")) {
                    currentPlaylist = line.substring(11);
                    createdPlaylists.add(currentPlaylist);
                    playlistSongs.put(currentPlaylist, new ArrayList<>());
                } else if (currentPlaylist != null) {
                    for (Song s : playlist) {
                        if (s.getAudioPath().equals(line)) {
                            playlistSongs.get(currentPlaylist).add(s);
                            break;
                        }
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {}
    }

    // --- GLOBAL LIBRARY LOGIC (Unchanged) ---
    public void loadDynamicPlaylist() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Your Music Folder");
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File musicFolder = chooser.getSelectedFile();
            chooser.setDialogTitle("Select Your Corresponding Images Folder");
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File imageFolder = chooser.getSelectedFile();
                savePaths(musicFolder.getAbsolutePath(), imageFolder.getAbsolutePath());
                scanAndMatchFiles(musicFolder, imageFolder);
            }
        }
    }

    private void savePaths(String musicPath, String imagePath) {
        try (PrintWriter out = new PrintWriter(SAVE_FILE)) {
            out.println(musicPath);
            out.println(imagePath);
        } catch (Exception e) { }
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
        } catch (Exception e) { }
    }

    private void scanAndMatchFiles(File musicDir, File imageDir) {
        playlist.clear(); 
        File[] audioFiles = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
        if (audioFiles == null || audioFiles.length == 0) return;

        for (File audioFile : audioFiles) {
            String fileName = audioFile.getName();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            File imgPng = new File(imageDir, baseName + ".png");
            File imgJpg = new File(imageDir, baseName + ".jpg");
            String finalImagePath = imgPng.exists() ? imgPng.getAbsolutePath() : (imgJpg.exists() ? imgJpg.getAbsolutePath() : "NO_IMAGE");
            String trackArtist = "Unknown Artist", trackTitle = baseName, trackDuration = "0:00"; 

            try {
                AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
                Map<String, Object> properties = baseFileFormat.properties();
                if (properties.containsKey("author")) trackArtist = ((String) properties.get("author")).trim();
                if (properties.containsKey("title")) trackTitle = ((String) properties.get("title")).trim();
                if (properties.containsKey("duration")) {
                    Long microseconds = (Long) properties.get("duration");
                    int mili = (int) (microseconds / 1000);
                    int sec = (mili / 1000) % 60;
                    int min = (mili / 1000) / 60;
                    trackDuration = String.format("%d:%02d", min, sec);
                }
            } catch (Exception e) {}
            playlist.add(new Song(trackTitle, trackArtist, audioFile.getAbsolutePath(), finalImagePath, trackDuration));
        }
        playlist.sort((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
    }

    public ArrayList<Song> getPlaylist() { return playlist; }

    public ArrayList<Song> searchSongs(String query) {
        if (query == null || query.trim().isEmpty()) return playlist;
        ArrayList<Song> results = new ArrayList<>();
        String search = query.toLowerCase().trim();
        for (Song song : playlist) {
            if (song.getTitle().toLowerCase().contains(search) || song.getArtist().toLowerCase().contains(search)) {
                results.add(song);
            }
        }
        return results;
    }
}