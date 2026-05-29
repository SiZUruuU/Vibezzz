package ControlPanel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;
import javax.swing.SwingUtilities;

/**
 * The core backend audio wrapper for the JLayer MP3 library.
 * Because standard JLayer playback completely freezes Java applications, this class 
 * wraps the player in a custom background Thread. It manually handles pausing, 
 * calculates progress via byte-math, and manages seamless hardware volume control.
 */
public class AudioEngine {

    // --- Core Audio Components ---
    private VolumeController volumeC;
    private Player player;
    private Thread playerThread;
    
    // --- Thread & Playback States ---
    // Split states allow the engine to differentiate between an active track, a paused track, 
    // and a track that was intentionally killed by the user skipping or closing the app.
    private boolean isThreadAlive = false; 
    private boolean isPaused = false;
    private boolean isManuallyStopped = false; 
    
    // --- File & Math Tracking ---
    private String currentFilePath;
    private FileInputStream fis;
    private long totalLength = 0; // The total size of the MP3 file in bytes
    private long seekLocation = 0; // How many bytes to skip when resuming/seeking
    private float globalVolume = 1.0f;
    
    // --- Event Callbacks ---
    private Runnable onTrackEnd; 

    /**
     * Registers a method to execute when a track finishes playing naturally.
     * @param callback The function to run (e.g., triggering the Skip Forward button).
     */
    public void setTrackEndCallback(Runnable callback) {
        this.onTrackEnd = callback;
    }

    /**
     * Standard method to start a new track from the very beginning.
     * @param filePath The absolute file path to the audio file.
     */
    public void playTrack(String filePath) {
        playTrack(filePath, false); 
    }

    /**
     * The master playback engine. Handles both fresh starts and seeking/scrubbing.
     * @param filePath  The absolute file path to the audio file.
     * @param isSeeking True if we are jumping to a specific timestamp, false for a fresh start.
     */
    public void playTrack(String filePath, boolean isSeeking) {
        try {
            // 1. Reset the byte-skip location if this is a brand new track
            if (!isSeeking) {
                seekLocation = 0; 
            }

            // 2. Safely terminate any currently playing track before starting a new one.
            // We temporarily cache the seekLocation because stopTrack() resets it.
            long tempSeek = seekLocation; 
            stopTrack(); 
            seekLocation = tempSeek;      

            // 3. Initialize the input streams
            currentFilePath = filePath;
            fis = new FileInputStream(filePath);
            
            // 4. If we are seeking, tell the stream to skip ahead by the calculated byte amount
            if (seekLocation > 0) {
                fis.skip(totalLength - seekLocation);
            }

            // Wrap in a buffered stream for performance, and initialize JLayer
            BufferedInputStream bis = new BufferedInputStream(fis);
            volumeC = new VolumeController();
            volumeC.setVolume(globalVolume);
            player = new Player(bis, volumeC);

            // 5. Only calculate the total file size on a fresh start
            if (seekLocation <= 0) {
                totalLength = fis.available();
            }

            // 6. Set the threading flags to active
            isThreadAlive = true;
            isPaused = false;
            isManuallyStopped = false; 

            // 7. Boot up the asynchronous playback thread
            playerThread = new Thread(() -> {
                try {
                    // This loop is the magic behind pausing JLayer.
                    while (isThreadAlive && !isManuallyStopped) {
                        if (isPaused) {
                            // If paused, put the thread to sleep to save CPU cycles
                            Thread.sleep(5); 
                        } else {
                            // If active, tell JLayer to decode exactly ONE frame of audio.
                            // If it fails (end of file), break the loop.
                            if (!player.play(1)) {
                                break; 
                            }
                        }
                    }
                    
                    // 8. Natural End-of-Track Logic
                    // If the loop broke naturally (not manually stopped by the user), fire the callback
                    if (player.isComplete() && !isManuallyStopped) {
                        isThreadAlive = false;
                        seekLocation = 0;
                        if (onTrackEnd != null) {
                            // CRITICAL: Push the UI update back to the main Event Dispatch Thread
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

    /**
     * Places the playback thread into a resting state and mutes the hardware buffer.
     */
    public void pauseTrack() {
        if (player != null && isThreadAlive && !isPaused) {
            isPaused = true;
            if (volumeC != null) volumeC.pauseHardware(); // Instantly mute the OS buffer to prevent audio trailing
        }
    }

    /**
     * Awakens the playback thread and un-mutes the hardware buffer.
     */
    public void resumeTrack() {
        if (player != null && isThreadAlive && isPaused) {
            isPaused = false;
            if (volumeC != null) volumeC.resumeHardware(); // Instantly wake the OS buffer
        }
    }

    /**
     * Completely destroys the current playback session and thread.
     */
    public void stopTrack() {
        if (player != null) {
            isManuallyStopped = true;
            isThreadAlive = false;
            isPaused = false;
            player.close();
            if (playerThread != null) playerThread.interrupt();
        }
    }

    /**
     * Calculates how far along the audio file we are.
     * @return A double between 0.0 (start) and 1.0 (finish).
     */
    public double getProgress() {
        if (totalLength <= 0 || fis == null) return 0.0;
        
        try {
            // Because we never destroy the stream on pause, this math is always 100% accurate!
            // Total bytes minus remaining bytes = bytes played.
            return (double) (totalLength - fis.available()) / totalLength;
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Scub/Jump to a specific point in the track.
     * Because JLayer streams cannot go backwards, this method calculates the target byte,
     * destroys the current stream, and instantly boots up a new stream starting at that byte.
     * @param percentage The target location (0.0 to 1.0).
     */
    public void seek(double percentage) {
        if (currentFilePath == null || totalLength <= 0) return;
        
        // Clamp the percentage to prevent out-of-bounds math
        percentage = Math.max(0.0, Math.min(1.0, percentage)); 
        
        // Calculate the exact number of bytes we need to leave remaining in the file
        seekLocation = (long) (totalLength * (1.0 - percentage));
        if (seekLocation <= 0) seekLocation = 1; 
        
        // Restart the track with the isSeeking flag set to true
        playTrack(currentFilePath, true); 
    }

    /**
     * Checks if music is currently outputting to the speakers.
     * @return True if the thread is alive and not paused.
     */
    public boolean isPlaying() { 
        return isThreadAlive && !isPaused; 
    }

    /**
     * Sets the global master volume.
     * @param volume A float between 0.0f (mute) and 1.0f (max).
     */
    public void setVolume(float volume) {
        this.globalVolume = volume;
        if (volumeC != null) {
            volumeC.setVolume(volume);
        }
    }

    /**
     * Retrieves the current global master volume.
     * @return A float between 0.0f (mute) and 1.0f (max).
     */
    public float getVolume() {
        return this.globalVolume;
    }
}