package ControlPanel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;
import javax.swing.SwingUtilities;

public class AudioEngine {
    
    private Player player;
    private Thread playerThread;
    private boolean isPlaying = false;
    private boolean isManuallyStopped = false; // Prevents double-skipping
    
    private String currentFilePath;
    private FileInputStream fis;
    private long totalLength = 0;
    private long pauseLocation = 0;
    
    private Runnable onTrackEnd; // Callback for when a song finishes

    // UI.java will use this to tell the engine what to do when a track finishes
    public void setTrackEndCallback(Runnable callback) {
        this.onTrackEnd = callback;
    }

    public void playTrack(String filePath) {
        playTrack(filePath, false); // Default to a fresh start
    }

    public void playTrack(String filePath, boolean isResuming) {
        try {
            // FIX 1: If we are not specifically resuming, force the track to 0:00
            if (!isResuming) {
                pauseLocation = 0; 
            }

            long tempPause = pauseLocation; 
            stopTrack(); 
            pauseLocation = tempPause;      

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
            isManuallyStopped = false; // Reset the flag before playing

            playerThread = new Thread(() -> {
                try {
                    player.play(); // This blocks the thread until the song finishes or is closed
                    
                    // FIX 2: If the song finished naturally (not skipped or paused)
                    if (player.isComplete() && !isManuallyStopped) {
                        isPlaying = false;
                        pauseLocation = 0;
                        
                        if (onTrackEnd != null) {
                            // Safely trigger the UI skip button from the background thread
                            SwingUtilities.invokeLater(onTrackEnd);
                        }
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
                isManuallyStopped = true;
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
            // Tell the engine we are explicitly resuming so it doesn't reset to 0
            playTrack(currentFilePath, true); 
        }
    }

    public void stopTrack() {
        if (player != null) {
            isManuallyStopped = true;
            player.close();
            pauseLocation = 0;
            isPlaying = false;
            if (playerThread != null) playerThread.interrupt();
        }
    }

    public double getProgress() {
        if (fis == null || totalLength <= 0) return 0.0;
        try {
            return (double) (totalLength - fis.available()) / totalLength;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void seek(double percentage) {
        if (currentFilePath == null || totalLength <= 0) return;
        
        percentage = Math.max(0.0, Math.min(1.0, percentage)); 
        pauseLocation = (long) (totalLength * (1.0 - percentage));
        
        if (pauseLocation <= 0) pauseLocation = 1; 
        
        playTrack(currentFilePath, true); 
    }

    public boolean isPlaying() { return isPlaying; }
}