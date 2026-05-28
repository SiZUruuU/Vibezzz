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
            stopTrack(); // Clean up before starting a new track
            
            currentFilePath = filePath;
            fis = new FileInputStream(filePath);
            
            // If we are resuming from a pause, skip to where we left off
            if (pauseLocation > 0) {
                fis.skip(totalLength - pauseLocation);
            } else {
                totalLength = fis.available();
            }

            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
            isPlaying = true;

            // Run the audio player on a background thread so the UI doesn't freeze
            playerThread = new Thread(() -> {
                try {
                    player.play();
                    
                    // When the song finishes naturally
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
            System.out.println("Error playing audio. Ensure the file is a valid MP3.");
            e.printStackTrace();
        }
    }

    public void pauseTrack() {
        if (player != null && isPlaying) {
            try {
                // Save the remaining bytes so we know where to resume
                pauseLocation = fis.available(); 
                player.close();
                isPlaying = false;
                
                if (playerThread != null) {
                    playerThread.interrupt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeTrack() {
        // To resume in JLayer, we just play the track again. 
        // The playTrack method handles skipping the bytes we already played.
        if (currentFilePath != null && !isPlaying) {
            playTrack(currentFilePath);
        }
    }

    public void stopTrack() {
        if (player != null) {
            player.close();
            pauseLocation = 0;
            isPlaying = false;
            
            if (playerThread != null) {
                playerThread.interrupt();
            }
        }
    }

    public boolean isPlaying() { 
        return isPlaying; 
    }
}