package ControlPanel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;

public class MusicHandler {

    // --- GLOBAL LIBRARY SETTINGS ---
    private ArrayList<Song> playlist = new ArrayList<>();
    private final String SAVE_FILE = "vibezz_settings.txt"; 
    private final String PLAYLIST_SAVE_FILE = "vibezz_playlists.txt";
    
    // --- PLAYLIST & QUEUE DATA ---
    private ArrayList<String> createdPlaylists = new ArrayList<>();
    private Map<String, ArrayList<Song>> playlistSongs = new HashMap<>();
    private ArrayList<Song> activePlayingList = new ArrayList<>();

    public MusicHandler() {
        loadSavedPaths(); 
        loadPlaylists(); 
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

    // --- GLOBAL LIBRARY LOGIC ---
    public void loadDynamicPlaylist() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Your Music Folder");
        
        // FIX: Removed the second folder prompt! It now only asks for the Music Folder.
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File musicFolder = chooser.getSelectedFile();
            savePaths(musicFolder.getAbsolutePath());
            scanAndMatchFiles(musicFolder);
        }
    }

    private void savePaths(String musicPath) {
        try (PrintWriter out = new PrintWriter(SAVE_FILE)) {
            out.println(musicPath); // Only saves the single music path now
        } catch (Exception e) { }
    }

    private void loadSavedPaths() {
        try {
            File file = new File(SAVE_FILE);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextLine()) {
                    String mPath = scanner.nextLine();
                    scanAndMatchFiles(new File(mPath));
                }
                scanner.close();
            }
        } catch (Exception e) { }
    }

    // FIX: Only requires the music directory now
    private void scanAndMatchFiles(File musicDir) {
        playlist.clear(); 
        
        // Create a dedicated folder in your app directory to hold the cropped cover art
        File cacheDir = new File("vibezz_covers");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        File[] audioFiles = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
        if (audioFiles == null || audioFiles.length == 0) return;

        for (File audioFile : audioFiles) {
            String fileName = audioFile.getName();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            String finalImagePath = "NO_IMAGE";
            String metaImage = extractAlbumArt(audioFile, cacheDir, baseName);
            
            if (metaImage != null) {
                // Priority 1: Use the Embedded Metadata Image!
                finalImagePath = metaImage;
            } else {
                // Priority 2: Fall back to checking the Music Folder itself for manual matches
                File imgPng = new File(musicDir, baseName + ".png");
                File imgJpg = new File(musicDir, baseName + ".jpg");
                File imgJpeg = new File(musicDir, baseName + ".jpeg");

                if (imgPng.exists()) finalImagePath = imgPng.getAbsolutePath();
                else if (imgJpg.exists()) finalImagePath = imgJpg.getAbsolutePath();
                else if (imgJpeg.exists()) finalImagePath = imgJpeg.getAbsolutePath();
            }

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

    // --- NEW: SQUARE-CROP METADATA PARSER ---
    private String extractAlbumArt(File mp3File, File cacheDir, String baseName) {
        // Only ever extract it once. If the square PNG already exists, load it instantly!
        File cachedImg = new File(cacheDir, baseName + "_meta.png");
        if (cachedImg.exists()) return cachedImg.getAbsolutePath();

        try (RandomAccessFile file = new RandomAccessFile(mp3File, "r")) {
            byte[] header = new byte[10];
            file.readFully(header);
            
            if (header[0] != 'I' || header[1] != 'D' || header[2] != '3') return null;

            int majorVersion = header[3];
            int tagSize = ((header[6] & 0x7F) << 21) | ((header[7] & 0x7F) << 14) | ((header[8] & 0x7F) << 7) | (header[9] & 0x7F);

            byte[] tagData = new byte[tagSize];
            file.readFully(tagData);

            int i = 0;
            while (i < tagSize - 10) {
                String frameId = new String(tagData, i, 4);
                
                if (!frameId.matches("^[A-Z0-9]{4}$")) break;

                int frameSize;
                if (majorVersion == 4) { 
                    frameSize = ((tagData[i+4] & 0x7F) << 21) | ((tagData[i+5] & 0x7F) << 14) | ((tagData[i+6] & 0x7F) << 7) | (tagData[i+7] & 0x7F);
                } else { 
                    frameSize = ((tagData[i+4] & 0xFF) << 24) | ((tagData[i+5] & 0xFF) << 16) | ((tagData[i+6] & 0xFF) << 8) | (tagData[i+7] & 0xFF);
                }
                
                if (frameId.equals("APIC")) {
                    int pos = i + 10;
                    int encoding = tagData[pos++];
                    
                    while (pos < tagData.length && tagData[pos] != 0) pos++;
                    pos++; 
                    pos++; 
                    
                    if (encoding == 1 || encoding == 2) {
                        while (pos < tagData.length - 1 && (tagData[pos] != 0 || tagData[pos+1] != 0)) pos++;
                        pos += 2;
                    } else {
                        while (pos < tagData.length && tagData[pos] != 0) pos++;
                        pos++;
                    }
                    
                    int imgSize = frameSize - (pos - (i + 10));
                    if (imgSize > 0 && pos + imgSize <= tagData.length) {
                        byte[] imgData = new byte[imgSize];
                        System.arraycopy(tagData, pos, imgData, 0, imgSize);
                        
                        // FIX: Read the raw bytes into a BufferedImage so we can do math on it!
                        ByteArrayInputStream bais = new ByteArrayInputStream(imgData);
                        BufferedImage originalImage = ImageIO.read(bais);
                        
                        if (originalImage != null) {
                            // Calculate the exact center and crop to the smallest dimension (Perfect 1:1 Square)
                            int minDim = Math.min(originalImage.getWidth(), originalImage.getHeight());
                            int x = (originalImage.getWidth() - minDim) / 2;
                            int y = (originalImage.getHeight() - minDim) / 2;
                            
                            BufferedImage squareImage = originalImage.getSubimage(x, y, minDim, minDim);
                            
                            // Save the standardized, perfectly square PNG to the cache folder
                            ImageIO.write(squareImage, "png", cachedImg);
                            return cachedImg.getAbsolutePath();
                        }
                    }
                }
                i += 10 + frameSize;
            }
        } catch (Exception e) {}
        
        return null;
    }
}