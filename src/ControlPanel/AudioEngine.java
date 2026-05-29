package ControlPanel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;
import javax.swing.SwingUtilities;

public class AudioEngine {

    private VolumeController volumeC;
    private Player player;
    private Thread playerThread;
    
    // Split states so we know if the engine is alive, paused, or killed
    private boolean isThreadAlive = false; 
    private boolean isPaused = false;
    private boolean isManuallyStopped = false; 
    
    private String currentFilePath;
    private FileInputStream fis;
    private long totalLength = 0;
    private long seekLocation = 0; // Renamed from pauseLocation for clarity
    private float globalVolume = 1.0f;
    
    private Runnable onTrackEnd; 

    public void setTrackEndCallback(Runnable callback) {
        this.onTrackEnd = callback;
    }

    public void playTrack(String filePath) {
        playTrack(filePath, false); 
    }

    public void playTrack(String filePath, boolean isSeeking) {
        try {
            if (!isSeeking) {
                seekLocation = 0; 
            }

            long tempSeek = seekLocation; 
            stopTrack(); 
            seekLocation = tempSeek;      

            currentFilePath = filePath;
            fis = new FileInputStream(filePath);
            
            if (seekLocation > 0) {
                fis.skip(totalLength - seekLocation);
            }

            BufferedInputStream bis = new BufferedInputStream(fis);
           
            volumeC = new VolumeController();
            volumeC.setVolume(globalVolume);
            player = new Player(bis, volumeC);

            if (seekLocation <= 0) {
                totalLength = fis.available();
            }

            isThreadAlive = true;
            isPaused = false;
            isManuallyStopped = false; 

            playerThread = new Thread(() -> {
                try {

                    while (isThreadAlive && !isManuallyStopped) {
                        if (isPaused) {
                            Thread.sleep(5); 
                        } else {
                            if (!player.play(1)) {
                                break; 
                            }
                        }
                    }
                    
                    if (player.isComplete() && !isManuallyStopped) {
                        isThreadAlive = false;
                        seekLocation = 0;
                        if (onTrackEnd != null) {
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
        if (player != null && isThreadAlive && !isPaused) {
            isPaused = true;
            if (volumeC != null) volumeC.pauseHardware(); // Instantly mute the OS buffer
        }
    }

    public void resumeTrack() {
        if (player != null && isThreadAlive && isPaused) {
            isPaused = false;
            if (volumeC != null) volumeC.resumeHardware(); // Instantly wake the OS buffer
        }
    }

    public void stopTrack() {
        if (player != null) {
            isManuallyStopped = true;
            isThreadAlive = false;
            isPaused = false;
            player.close();
            if (playerThread != null) playerThread.interrupt();
        }
    }

    public double getProgress() {
        if (totalLength <= 0 || fis == null) return 0.0;
        
        try {
            // Because we never destroy the stream on pause, this math is now always 100% accurate!
            return (double) (totalLength - fis.available()) / totalLength;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void seek(double percentage) {
        if (currentFilePath == null || totalLength <= 0) return;
        
        percentage = Math.max(0.0, Math.min(1.0, percentage)); 
        seekLocation = (long) (totalLength * (1.0 - percentage));
        
        if (seekLocation <= 0) seekLocation = 1; 
        
        playTrack(currentFilePath, true); 
    }

    // Keeps UI in sync (e.g., changes Play icon to Pause icon)
    public boolean isPlaying() { 
        return isThreadAlive && !isPaused; 
    }

    public void setVolume(float volume) {
        this.globalVolume = volume;
        if (volumeC != null) {
            volumeC.setVolume(volume);
        }
    }

    public float getVolume() {
        return this.globalVolume;
    }
}