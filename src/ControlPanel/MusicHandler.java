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

/**
 * The central data model and business logic handler for the application.
 * This class is responsible for loading directories, parsing audio metadata (ID3 tags),
 * managing playlist structures, saving/loading user data to the hard drive, and 
 * extracting/cropping embedded album art directly from MP3 binaries.
 */
public class MusicHandler {

    // --- GLOBAL LIBRARY SETTINGS ---
    private ArrayList<Song> playlist = new ArrayList<>();
    private final String SAVE_FILE = "vibezz_settings.txt"; 
    private final String PLAYLIST_SAVE_FILE = "vibezz_playlists.txt";
    
    // --- PLAYLIST & QUEUE DATA ---
    private ArrayList<String> createdPlaylists = new ArrayList<>();
    private Map<String, ArrayList<Song>> playlistSongs = new HashMap<>();
    
    // The active list dictates whether the player is looping the global library or a specific playlist
    private ArrayList<Song> activePlayingList = new ArrayList<>();

    /**
     * Constructs the MusicHandler and automatically attempts to load 
     * the user's previously saved music directory and custom playlists.
     */
    public MusicHandler() {
        loadSavedPaths(); 
        loadPlaylists(); 
    }

    // --- PLAYLIST & QUEUE LOGIC ---
    
    public ArrayList<String> getCreatedPlaylists() { return createdPlaylists; }
    public Map<String, ArrayList<Song>> getPlaylistSongs() { return playlistSongs; }
    
    /**
     * Retrieves the currently active playback queue. 
     * Defaults to the global library if no specific playlist is active.
     */
    public ArrayList<Song> getActiveList() {
        return (activePlayingList != null && !activePlayingList.isEmpty()) ? activePlayingList : playlist;
    }
    
    public void setActiveList(ArrayList<Song> newList) {
        this.activePlayingList = newList;
    }

    /**
     * Persists all custom playlists and their associated songs to a local text file.
     * Uses a simple bracket tag [PLAYLIST] to separate different lists in the file.
     */
    public void savePlaylists() {
        try (PrintWriter out = new PrintWriter(PLAYLIST_SAVE_FILE)) {
            for (String pName : createdPlaylists) {
                out.println("[PLAYLIST] " + pName);
                ArrayList<Song> songs = playlistSongs.get(pName);
                if (songs != null) {
                    for (Song s : songs) {
                        out.println(s.getAudioPath()); // Only save the file path to save space
                    }
                }
            }
        } catch (Exception e) { 
            System.out.println("Failed to save playlists."); 
        }
    }

    /**
     * Reads the local save file and reconstructs the user's custom playlists in memory.
     */
    public void loadPlaylists() {
        createdPlaylists.clear();
        playlistSongs.clear();
        try {
            File file = new File(PLAYLIST_SAVE_FILE);
            if (!file.exists()) return; // Guard clause: First time booting the app
            
            Scanner scanner = new Scanner(file);
            String currentPlaylist = null;
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                if (line.startsWith("[PLAYLIST] ")) {
                    // Create a new playlist category
                    currentPlaylist = line.substring(11);
                    createdPlaylists.add(currentPlaylist);
                    playlistSongs.put(currentPlaylist, new ArrayList<>());
                } else if (currentPlaylist != null) {
                    // Match the saved path string to actual Song objects in the global library
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
    
    /**
     * Opens a native OS file dialog allowing the user to select their main music directory.
     */
    public void loadDynamicPlaylist() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Your Music Folder");
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File musicFolder = chooser.getSelectedFile();
            savePaths(musicFolder.getAbsolutePath()); // Save for future boot-ups
            scanAndMatchFiles(musicFolder);
        }
    }

    private void savePaths(String musicPath) {
        try (PrintWriter out = new PrintWriter(SAVE_FILE)) {
            out.println(musicPath); 
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

    /**
     * Scans the selected directory for audio files, extracts metadata, 
     * finds or creates cover art, and builds the global Song objects.
     * @param musicDir The directory containing the user's music.
     */
    private void scanAndMatchFiles(File musicDir) {
        playlist.clear(); 
        
        // 1. Ensure the cache directory for extracted album covers exists
        File cacheDir = new File("vibezz_covers");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        // 2. Filter the folder to only look at MP3 and WAV files
        File[] audioFiles = musicDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav"));
        if (audioFiles == null || audioFiles.length == 0) return;

        for (File audioFile : audioFiles) {
            String fileName = audioFile.getName();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            
            // --- IMAGE PRIORITY LOGIC ---
            String finalImagePath = "NO_IMAGE";
            // Attempt to rip embedded cover art from the MP3 file itself
            String metaImage = extractAlbumArt(audioFile, cacheDir, baseName);
            
            if (metaImage != null) {
                // Priority 1: Use the Embedded Metadata Image!
                finalImagePath = metaImage;
            } else {
                // Priority 2: Fall back to checking the Music Folder itself for identically named images
                File imgPng = new File(musicDir, baseName + ".png");
                File imgJpg = new File(musicDir, baseName + ".jpg");
                File imgJpeg = new File(musicDir, baseName + ".jpeg");

                if (imgPng.exists()) finalImagePath = imgPng.getAbsolutePath();
                else if (imgJpg.exists()) finalImagePath = imgJpg.getAbsolutePath();
                else if (imgJpeg.exists()) finalImagePath = imgJpeg.getAbsolutePath();
            }

            // Default fallback metadata
            String trackArtist = "Unknown Artist", trackTitle = baseName, trackDuration = "0:00"; 

            // --- STANDARD METADATA EXTRACTION ---
            try {
                // Uses Java's native AudioSystem to pull basic properties (Author, Title, Duration)
                AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
                Map<String, Object> properties = baseFileFormat.properties();
                
                if (properties.containsKey("author")) trackArtist = ((String) properties.get("author")).trim();
                if (properties.containsKey("title")) trackTitle = ((String) properties.get("title")).trim();
                
                // Convert microsecond duration into standard MM:SS string format
                if (properties.containsKey("duration")) {
                    Long microseconds = (Long) properties.get("duration");
                    int mili = (int) (microseconds / 1000);
                    int sec = (mili / 1000) % 60;
                    int min = (mili / 1000) / 60;
                    trackDuration = String.format("%d:%02d", min, sec);
                }
            } catch (Exception e) {}
            
            // Register the fully built song into the global library
            playlist.add(new Song(trackTitle, trackArtist, audioFile.getAbsolutePath(), finalImagePath, trackDuration));
        }
        
        // Sort the final library alphabetically by title
        playlist.sort((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
    }

    public ArrayList<Song> getPlaylist() { return playlist; }

    /**
     * Filters the global library based on a user's search query.
     * Checks both the song title and the artist name.
     */
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

    // --- CUSTOM METADATA BYTE-PARSER ---
    
    /**
     * Cracks open an MP3 file at the binary level to locate and extract embedded album art (ID3v2 APIC frame).
     * It then mathematically crops the image into a perfect 1:1 square and saves it to a cache folder.
     * @param mp3File  The audio file to parse.
     * @param cacheDir The hidden folder to save the cropped image into.
     * @param baseName The name of the track to use as the image filename.
     * @return The absolute path to the extracted image, or null if none exists.
     */
    private String extractAlbumArt(File mp3File, File cacheDir, String baseName) {
        
        // OPTIMIZATION: If we already extracted this image in a previous session, just return it instantly!
        File cachedImg = new File(cacheDir, baseName + "_meta.png");
        if (cachedImg.exists()) return cachedImg.getAbsolutePath();

        try (RandomAccessFile file = new RandomAccessFile(mp3File, "r")) {
            // Read the first 10 bytes (The standard ID3v2 Header)
            byte[] header = new byte[10];
            file.readFully(header);
            
            // Check for valid ID3 magic number ('I' 'D' '3')
            if (header[0] != 'I' || header[1] != 'D' || header[2] != '3') return null;

            int majorVersion = header[3];
            
            // Calculate total tag size. ID3v2 uses "sync-safe" integers, 
            // meaning the 8th bit (MSB) of each byte is intentionally ignored.
            int tagSize = ((header[6] & 0x7F) << 21) | ((header[7] & 0x7F) << 14) | ((header[8] & 0x7F) << 7) | (header[9] & 0x7F);

            // Read the entire tag block into memory
            byte[] tagData = new byte[tagSize];
            file.readFully(tagData);

            int i = 0;
            // Iterate through the frames until we find the Attached Picture (APIC) frame
            while (i < tagSize - 10) {
                String frameId = new String(tagData, i, 4);
                
                // If we hit empty padding (0x00), stop parsing
                if (!frameId.matches("^[A-Z0-9]{4}$")) break;

                // Calculate the size of the current frame
                int frameSize;
                if (majorVersion == 4) { 
                    // v2.4 uses sync-safe integers for frame sizes as well
                    frameSize = ((tagData[i+4] & 0x7F) << 21) | ((tagData[i+5] & 0x7F) << 14) | ((tagData[i+6] & 0x7F) << 7) | (tagData[i+7] & 0x7F);
                } else { 
                    // v2.3 uses standard 32-bit integers
                    frameSize = ((tagData[i+4] & 0xFF) << 24) | ((tagData[i+5] & 0xFF) << 16) | ((tagData[i+6] & 0xFF) << 8) | (tagData[i+7] & 0xFF);
                }
                
                // We found the Picture Frame!
                if (frameId.equals("APIC")) {
                    int pos = i + 10;
                    int encoding = tagData[pos++]; // Text encoding byte
                    
                    // Skip the MIME type string (e.g., "image/jpeg") which is null-terminated
                    while (pos < tagData.length && tagData[pos] != 0) pos++;
                    pos++; 
                    
                    // Skip Picture Type byte (e.g., 0x03 for Front Cover)
                    pos++; 
                    
                    // Skip Description string (Accounts for 2-byte UTF-16 null terminators)
                    if (encoding == 1 || encoding == 2) {
                        while (pos < tagData.length - 1 && (tagData[pos] != 0 || tagData[pos+1] != 0)) pos++;
                        pos += 2;
                    } else {
                        while (pos < tagData.length && tagData[pos] != 0) pos++;
                        pos++;
                    }
                    
                    // We have reached the raw image bytes!
                    int imgSize = frameSize - (pos - (i + 10));
                    if (imgSize > 0 && pos + imgSize <= tagData.length) {
                        byte[] imgData = new byte[imgSize];
                        System.arraycopy(tagData, pos, imgData, 0, imgSize);
                        
                        // Load the raw bytes into a Java BufferedImage for manipulation
                        ByteArrayInputStream bais = new ByteArrayInputStream(imgData);
                        BufferedImage originalImage = ImageIO.read(bais);
                        
                        if (originalImage != null) {
                            // Math: Calculate the exact center point and crop to the smallest dimension (Perfect 1:1 Square)
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
                // Skip to the start of the next frame
                i += 10 + frameSize;
            }
        } catch (Exception e) {}
        
        return null; // Return null if no image was found in the binary
    }
}