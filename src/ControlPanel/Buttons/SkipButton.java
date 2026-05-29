package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import ControlPanel.Song;
import Main.Panel;
import Main.UI;
import java.util.ArrayList;

/**
 * Handles skipping to the next or previous track in the active queue.
 * This class dynamically adapts to the "Repeat" and "Shuffle" states, 
 * and uses a boolean flag to act as either the Skip Forward or Skip Backward button.
 */
public class SkipButton extends ButtonManager {
    
    private boolean isForward; // true = Skip Forward, false = Skip Backward

    /**
     * Constructs the SkipButton.
     * @param panel     The main application panel used to refresh the screen.
     * @param ui        The main UI state manager.
     * @param isForward Determines the direction: true for next track, false for previous track.
     */
    public SkipButton(Panel panel, UI ui, boolean isForward) {
        // Bounds are initialized to 0; PlayerView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
        this.isForward = isForward;
    }

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Fetch the active queue (ensures we skip within the correct playlist or library)
        ArrayList<Song> playlist = ui.musicHandler.getActiveList();
        
        // Guard clause: Do nothing if the list is empty
        if (playlist.isEmpty()) return;

        // 2. Determine the target index based on playback direction and active modes
        if (isForward) {
            
            if (ui.isRepeat) {
                // Repeat Mode: Intentionally do nothing to the index so the current song simply replays
            } else if (ui.isShuffle) {
                // Shuffle Mode: Pick a random track 
                if (playlist.size() > 1) {
                    int newIndex;
                    // Keep generating a random index until it picks a song different from the current one
                    do { 
                        newIndex = (int)(Math.random() * playlist.size());
                    } while (newIndex == ui.currentSongIndex);
                    ui.currentSongIndex = newIndex;
                }
            } else {
                // Normal Mode: Move to the next song. 
                // The modulo (%) operator automatically loops the index back to 0 if we hit the end of the list.
                ui.currentSongIndex = (ui.currentSongIndex + 1) % playlist.size();
            }
            
        } else {
            
            if (ui.isRepeat) {
                // Repeat Mode: Intentionally do nothing to the index so the current song simply replays
            } else {
                // Normal Mode: Move to the previous song. 
                // Adding playlist.size() before the modulo ensures the math never produces a negative index 
                // when skipping backward from the very first track (index 0).
                ui.currentSongIndex = (ui.currentSongIndex - 1 + playlist.size()) % playlist.size();
            }
            
        }

        // 3. Command the audio engine to play the newly selected track
        ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
        
        // 4. Request a screen update to render the new metadata and artwork
        panel.repaint();
    }
}