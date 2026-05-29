package ControlPanel;

import java.lang.reflect.Field;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;

/**
 * A custom audio output device that overrides standard JLayer behavior.
 * Because JLayer lacks native volume control and instant hardware pausing, this class 
 * intercepts the raw audio byte stream before it hits the OS. It mathematically scales 
 * the amplitude for volume control, and uses Java Reflection to directly command 
 * the hidden hardware buffer for instantaneous pausing.
 */
public class VolumeController extends JavaSoundAudioDevice {
    
    private float volume = 1.0f; // Range: 0.0f (Mute) to 1.0f (Max Volume)

    /**
     * Sets the global volume multiplier.
     * @param volume A float representing the desired volume percentage.
     */
    public void setVolume(float volume) {
        // Clamp the value to ensure it never exceeds standard bounds, preventing math crashes
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    /**
     * Intercepts the decoded audio samples right before they are sent to the speakers.
     * @param samples The raw PCM audio data.
     * @param offs    The offset index to start reading from.
     * @param len     The length of the data to process.
     */
    @Override
    public void write(short[] samples, int offs, int len) throws JavaLayerException {
        
        // Optimization: Only run the heavy math loop if the volume actually needs to be reduced
        if (this.volume != 1.0f) {
            
            for (int i = offs; i < offs + len; i++) {
                
                // Scale the amplitude of the audio wave by multiplying it by our volume decimal
                int scaledSample = (int) (samples[i] * this.volume);
                
                // Hardware Clipping Protection: 
                // If the scaled wave exceeds standard 16-bit audio limits, it causes horrible static distortion.
                // We prevent this by artificially flattening the peak of the wave.
                if (scaledSample > Short.MAX_VALUE) {
                    scaledSample = Short.MAX_VALUE;
                } else if (scaledSample < Short.MIN_VALUE) {
                    scaledSample = Short.MIN_VALUE;
                }
                
                // Cast it back to a short and overwrite the original sample array
                samples[i] = (short) scaledSample;
            }
        }
        
        // Pass the modified, quieter audio buffer down to the native OS to be played
        super.write(samples, offs, len);
    }

    // --- DIRECT HARDWARE CONTROL FOR INSTANT PAUSE/PLAY ---

    /**
     * Instantly halts the operating system's audio buffer.
     * Bypasses JLayer's standard architecture using Java Reflection to stop 
     * the audio "trailing" effect when the user hits pause.
     */
    public void pauseHardware() {
        try {
            // Reach inside the compiled JavaSoundAudioDevice class to grab its hidden "source" variable
            Field field = JavaSoundAudioDevice.class.getDeclaredField("source");
            field.setAccessible(true); // Bypass private encapsulation
            SourceDataLine line = (SourceDataLine) field.get(this);
            
            // Instantly halt the hardware line to prevent lingering buffer audio from playing
            if (line != null && line.isRunning()) {
                line.stop(); 
            }
        } catch (Exception e) {
            // Safely ignore reflection failures
        }
    }

    /**
     * Instantly wakes the operating system's audio buffer back up.
     */
    public void resumeHardware() {
        try {
            // Re-acquire the hidden hardware line
            Field field = JavaSoundAudioDevice.class.getDeclaredField("source");
            field.setAccessible(true);
            SourceDataLine line = (SourceDataLine) field.get(this);
            
            // Instantly start the hardware line back up so the audio resumes cleanly
            if (line != null && !line.isRunning()) {
                line.start(); 
            }
        } catch (Exception e) {
            // Safely ignore reflection failures
        }
    }
}