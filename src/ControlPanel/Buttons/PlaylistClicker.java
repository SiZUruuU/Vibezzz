package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles mouse interactions for the custom playlist menu items.
 * Each instance of this class represents a physical, clickable playlist name 
 * on the right side of the screen. When clicked, it transitions the UI from 
 * the main menu into the specific view for that playlist.
 */
public class PlaylistClicker extends ButtonManager {
    
    private String playlistName;

    /**
     * Constructs the PlaylistClicker.
     * @param panel        The main application panel used to refresh the screen.
     * @param ui           The main UI state manager.
     * @param playlistName The unique string identifier for the playlist this button represents.
     */
    public PlaylistClicker(Panel panel, UI ui, String playlistName) {
        // Bounds are initialized to 0; PlaylistView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
        this.playlistName = playlistName;
    }

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Guard clause: Ensure this button only works if we are on the main playlist menu
        if (!ui.insidePlaylistView) {
            
            // 2. Tell the UI state manager exactly which playlist the user wants to open
            ui.selectedPlaylistName = this.playlistName; 
            
            // 3. Toggle the state so the renderer knows to draw the inside of the playlist
            ui.insidePlaylistView = true;
            
            // 4. Destroy the main menu hitboxes and dynamically generate the hitboxes 
            // for the songs contained inside this specific playlist
            ui.refreshPlaylistButtons();
            
            // 5. Request a screen update to render the new state
            panel.repaint();
        }
    }
}