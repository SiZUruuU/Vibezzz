package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles navigation out of a specific playlist.
 * When clicked, this button returns the user to the main Playlists menu 
 * by resetting the active view states, clearing the selected playlist, 
 * and regenerating the appropriate hitboxes.
 */
public class BackButton extends ButtonManager {

    /**
     * Constructs the BackButton.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public BackButton(Panel panel, UI ui) { 
        // Bounds are initialized to 0; PlaylistView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui); 
    }
    
    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Guard clause: Only execute if the user is actually inside a playlist
        if (ui.insidePlaylistView) {
            
            // 2. Reset the visual states to return to the main menu
            ui.insidePlaylistView = false;
            ui.selectedPlaylistName = "";
            
            // 3. Safety reset: Ensure the "Select from Library" mode is turned off 
            // so clicks don't accidentally add songs to a playlist we just left
            ui.isAddingToPlaylist = false; 
            
            // 4. Tell the UI to destroy the song hitboxes and rebuild the playlist menu hitboxes
            ui.refreshPlaylistButtons();
            
            // 5. Request a screen update to render the main menu
            panel.repaint();
        }
    }
}