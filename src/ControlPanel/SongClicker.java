package ControlPanel;

import Main.Panel;
import Main.UI;

/**
 * Handles mouse interactions for individual songs inside a custom playlist.
 * Unlike the global library (which uses one giant hitbox for the whole list), 
 * each instance of this class acts as a physical, independent hitbox for a specific track.
 * When clicked, it restricts the active playback queue exclusively to the current playlist.
 */
public class SongClicker extends ButtonManager {
    
    private Song song; // The specific audio file this button represents

    /**
     * Constructs the SongClicker.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     * @param song  The specific Song object this button is attached to.
     */
    public SongClicker(Panel panel, UI ui, Song song) {
        // Bounds are initialized to 0; PlaylistView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
        this.song = song;
    }

    /**
     * Retrieves the specific song associated with this button.
     * This is used by PlaylistView to extract the title and render the text inside the hitbox.
     * @return The Song object.
     */
    public Song getSong() { 
        return song; 
    } 

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Context Switch: Tell the backend that the active queue is now STRICTLY this specific playlist.
        // This prevents the player from accidentally bleeding over into the global library when skipping tracks.
        ui.musicHandler.setActiveList(ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName));
        
        // 2. Locate exactly where this clicked song exists inside that newly active list
        ui.currentSongIndex = ui.musicHandler.getActiveList().indexOf(song);
        
        // 3. Command the audio engine to begin playback of this specific file
        ui.audioEngine.playTrack(song.getAudioPath());
        
        // 4. Request a screen update to render the "Now Playing" highlight on this song
        panel.repaint();
    }
}