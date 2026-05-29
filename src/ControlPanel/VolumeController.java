package ControlPanel;

import java.lang.reflect.Field;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;


public class VolumeController extends JavaSoundAudioDevice {
    
    private float volume = 1.0f; 

    
    public void setVolume(float volume) {
       
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    
    @Override
    public void write(short[] samples, int offs, int len) throws JavaLayerException {
        
        
        if (this.volume != 1.0f) {
            
            for (int i = offs; i < offs + len; i++) {
                
                
                int scaledSample = (int) (samples[i] * this.volume);
                
                
                if (scaledSample > Short.MAX_VALUE) {
                    scaledSample = Short.MAX_VALUE;
                } else if (scaledSample < Short.MIN_VALUE) {
                    scaledSample = Short.MIN_VALUE;
                }
                
               
                samples[i] = (short) scaledSample;
            }
        }
        
        
        super.write(samples, offs, len);
    }

    
    public void pauseHardware() {
        try {
            
            Field field = JavaSoundAudioDevice.class.getDeclaredField("source");
            field.setAccessible(true); // Bypass private encapsulation
            SourceDataLine line = (SourceDataLine) field.get(this);
            
           
            if (line != null && line.isRunning()) {
                line.stop(); 
            }
        } catch (Exception e) {
            
    }

    
    public void resumeHardware() {
        try {
            // Re-acquire the hidden hardware line
            Field field = JavaSoundAudioDevice.class.getDeclaredField("source");
            field.setAccessible(true);
            SourceDataLine line = (SourceDataLine) field.get(this);
            
           
            if (line != null && !line.isRunning()) {
                line.start(); 
            }
        } catch (Exception e) {
            
        }
    }
}