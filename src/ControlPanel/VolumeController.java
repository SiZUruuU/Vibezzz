package ControlPanel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;

public class VolumeController extends JavaSoundAudioDevice {
    
    private float volume = 1.0f; // 1.0f is full volume, 0.0f is mute

    public void setVolume(float volume) {
        // Keep volume bounded safely between 0.0 and 1.0
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    @Override
    public void write(short[] samples, int offs, int len) throws JavaLayerException {
        // If volume is less than 100%, adjust the sound waves mathematically
        if (this.volume != 1.0f) {
            for (int i = offs; i < offs + len; i++) {
                int scaledSample = (int) (samples[i] * this.volume);
                
                // Clamp values to prevent raw audio clipping distortion
                if (scaledSample > Short.MAX_VALUE) {
                    scaledSample = Short.MAX_VALUE;
                } else if (scaledSample < Short.MIN_VALUE) {
                    scaledSample = Short.MIN_VALUE;
                }
                
                samples[i] = (short) scaledSample;
            }
        }
        // Send the modified audio samples back up to JLayer to actually play
        super.write(samples, offs, len);
    }
}