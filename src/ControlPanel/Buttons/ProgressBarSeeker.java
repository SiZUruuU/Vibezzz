package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles mouse interactions for the audio playback progress bar.
 * This class is unique because it manages two distinct phases: 
 * real-time visual "scrubbing" (dragging the knob without interrupting the audio) 
 * and the final execution (committing the new timestamp to the audio engine).
 */
public class ProgressBarSeeker extends ButtonManager {

    /**
     * Constructs the ProgressBarSeeker.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public ProgressBarSeeker(Panel panel, UI ui) {
        // Bounds are initialized to 0; PlayerView dynamically sets them over the progress bar track.
        super(0, 0, 0, 0, panel, ui);
    }

    /**
     * Updates the visual UI state while the user is actively dragging the mouse.
     * This allows the knob to move smoothly on screen without spamming the 
     * JLayer audio engine with seek requests, which would cause horrible audio glitching.
     * * @param mouseX The current X coordinate of the user's dragged mouse.
     */
    public void updateVisual(int mouseX) {
        // 1. Guard clause: Prevent division by zero if bounds aren't set yet
        if (this.width == 0) return;
        
        // 2. Calculate how far along the bar the mouse is (as a decimal percentage)
        double percentage = (double) (mouseX - this.x) / this.width;
        
        // 3. Clamp the value between 0.0 (0%) and 1.0 (100%) so the knob can't be dragged off-screen
        ui.dragProgress = Math.max(0.0, Math.min(1.0, percentage));
        
        // 4. Request a screen update to render the knob at its new temporary position
        ui.panel.repaint();
    }

    /**
     * Executes the actual seek command when the user clicks or releases the drag.
     * * @param mouseX The X coordinate of the user's mouse click or drag release.
     * @param mouseY The Y coordinate of the user's mouse click or drag release.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Guard clause: Only attempt to seek if there is music loaded and actively playing
        if (!ui.musicHandler.getPlaylist().isEmpty() && ui.audioEngine.isPlaying()) {
            
            // 2. Calculate the final requested percentage based on the drop location
            double percentage = (double) (mouseX - this.x) / this.width;
            
            // 3. Command the audio engine to jump to that specific percentage of the track's byte length
            ui.audioEngine.seek(Math.max(0.0, Math.min(1.0, percentage)));
        }
    }
}