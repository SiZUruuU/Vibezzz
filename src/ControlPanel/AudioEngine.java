package ControlPanel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

public class AudioEngine {
    
    private Player player;
    private Thread playerThread;
    private boolean isPlaying = false;
    
    private String currentFilePath;
    private FileInputStream fis;
    private long totalLength = 0;
    private long pauseLocation = 0;

    public void playTrack(String filePath) {
        try {
            // Check if we are starting a completely new song
            if (currentFilePath != null && !currentFilePath.equals(filePath)) {
                pauseLocation = 0; // Reset completely for new songs
            }

            long tempPause = pauseLocation; // Save pause state before stopping
            stopTrack(); 
            pauseLocation = tempPause;      // Restore it!

            currentFilePath = filePath;
            fis = new FileInputStream(filePath);
            
            if (pauseLocation > 0) {
                fis.skip(totalLength - pauseLocation);
            } else {
                totalLength = fis.available();
            }

            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
            isPlaying = true;

            playerThread = new Thread(() -> {
                try {
                    player.play();
                    if (player.isComplete()) {
                        isPlaying = false;
                        pauseLocation = 0;
                    }
                } catch (Exception e) {
                    System.out.println("Playback interrupted.");
                }
            });
            playerThread.start();

        } catch (Exception e) {
            System.out.println("Error playing audio.");
            e.printStackTrace();
        }
    }

    public void pauseTrack() {
        if (player != null && isPlaying) {
            try {
                pauseLocation = fis.available(); 
                player.close();
                isPlaying = false;
                if (playerThread != null) playerThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeTrack() {
        if (currentFilePath != null && !isPlaying) {
            playTrack(currentFilePath);
        }
    }

    public void stopTrack() {
        if (player != null) {
            player.close();
            pauseLocation = 0;
            isPlaying = false;
            if (playerThread != null) playerThread.interrupt();
        }
    }

    // --- NEW: Calculate current progress (0.0 to 1.0) ---
    public double getProgress() {
        if (fis == null || totalLength <= 0) return 0.0;
        try {
            return (double) (totalLength - fis.available()) / totalLength;
        } catch (Exception e) {
            return 0.0;
        }
    }

    // --- NEW: Jump to a specific percentage of the song ---
    public void seek(double percentage) {
        if (currentFilePath == null || totalLength <= 0) return;
        
        percentage = Math.max(0.0, Math.min(1.0, percentage)); // Keep between 0 and 1
        pauseLocation = (long) (totalLength * (1.0 - percentage));
        
        if (pauseLocation <= 0) pauseLocation = 1; // Prevent full reset
        
        playTrack(currentFilePath); // Replay from new location
    }

    public boolean isPlaying() { return isPlaying; }
}