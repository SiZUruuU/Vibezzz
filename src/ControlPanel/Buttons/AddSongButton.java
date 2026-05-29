package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles the state toggle for adding a song to a custom playlist.
 * Rather than opening a file chooser, this button flips the UI into a 
 * "selection mode," allowing the user to seamlessly pick a track directly 
 * from their existing global library list.
 */
public class AddSongButton extends ButtonManager {

    /**
     * Constructs the AddSongButton.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public AddSongButton(Panel panel, UI ui) { 
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
        

        if (!ui.insidePlaylistView) return;
        

        ui.isAddingToPlaylist = !ui.isAddingToPlaylist; 
        
        panel.repaint();
    }
}