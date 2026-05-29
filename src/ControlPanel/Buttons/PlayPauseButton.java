package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles the main play/pause toggle functionality for the media player.
 * When clicked, this button checks the current state of the AudioEngine and 
 * either pauses active playback, resumes a paused track, or starts a fresh 
 * track if the engine is completely stopped.
 */
public class PlayPauseButton extends ButtonManager {

    /**
     * Constructs the PlayPauseButton.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public PlayPauseButton(Panel panel, UI ui) { 
        // Bounds are initialized to 0; PlayerView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui); 
    }

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Fetch the active queue (This ensures we play from the correct list, 
        // whether that is the global library or a specific custom playlist).
        java.util.ArrayList<ControlPanel.Song> playlist = ui.musicHandler.getActiveList();
        
        // 2. Guard clause: Do absolutely nothing if there are no songs loaded
        if (playlist.isEmpty()) return; 
        
        // 3. Playback Toggle Logic
        if (ui.audioEngine.isPlaying()) {
            
            // 3a. If music is currently playing, command the engine to pause
            ui.audioEngine.pauseTrack();
            
        } else {
            
            // 3b. If music is not playing, first attempt to just resume from a paused state
            ui.audioEngine.resumeTrack();
            
            // 3c. Fallback: If resumeTrack() didn't start the audio (meaning the player 
            // was fully stopped or newly booted), forcefully start the track from the beginning
            if (!ui.audioEngine.isPlaying()) {
                ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
            }
        }
        
        // 4. Request a screen update so the Play icon swaps to a Pause icon (or vice versa)
        panel.repaint();
    }
}