package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles mouse interactions for the audio volume slider.
 * This class translates the user's horizontal mouse position (via clicks or drags) 
 * into a floating-point percentage, which is then fed directly to the AudioEngine 
 * to smoothly control playback volume.
 */
public class VolumeSlider extends ButtonManager {

    /**
     * Constructs the VolumeSlider.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public VolumeSlider(Panel panel, UI ui) {
        // Bounds are initialized to 0; VolumeView dynamically sets them over the slider track.
        super(0, 0, 0, 0, panel, ui); 
    }

    /**
     * Executes the button's core logic when clicked.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        // 1. Instantly snap the volume to wherever the user clicked
        updateVolume(mouseX);
    }

    /**
     * Calculates and applies the new volume level based on mouse coordinates.
     * This method is separated from execute() so it can also be called continuously 
     * when the user clicks and drags the volume knob.
     * * @param mouseX The current X coordinate of the user's mouse.
     */
    public void updateVolume(int mouseX) {
        
        // 1. Guard clause: Prevent division by zero if bounds aren't set yet
        if (this.width == 0) return;

        // 2. Spatial Math: Calculate how far along the bar the mouse is
        // Subtracting 'this.x' gives us the position relative to the start of the slider.
        float percentage = (float) (mouseX - this.x) / this.width;
        
        // 3. Clamp the value between 0.0f (mute) and 1.0f (maximum volume)
        // This ensures dragging the mouse way off the screen doesn't crash the audio engine.
        percentage = Math.max(0.0f, Math.min(1.0f, percentage));
        
        // 4. Send the calculated percentage to the audio engine to adjust the master gain
        ui.audioEngine.setVolume(percentage);
        
        // 5. Request a screen update to render the filled portion of the volume track 
        // at its new length, giving the user instant visual feedback.
        panel.repaint(); 
    }
}