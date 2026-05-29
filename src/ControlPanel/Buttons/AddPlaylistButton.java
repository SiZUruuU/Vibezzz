package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;
import javax.swing.JOptionPane;

/**
 * Handles the creation of new custom playlists.
 * When clicked, it prompts the user for a name, validates the input to prevent 
 * duplicates or blank names, and saves the new state to the hard drive.
 */
public class AddPlaylistButton extends ButtonManager {

    /**
     * Constructs the AddPlaylistButton.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public AddPlaylistButton(Panel panel, UI ui) {
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
        
        // 1. Prompt the user with a standard Swing input dialog
        String playlistName = JOptionPane.showInputDialog(null, "Enter Playlist Name:", "Create New Playlist", JOptionPane.PLAIN_MESSAGE);
        
        // 2. Validate the input (Ensure they didn't hit 'Cancel' or just type spaces)
        if (playlistName != null && !playlistName.trim().isEmpty()) {
            String cleanName = playlistName.trim();
            
            // 3. Prevent duplicate playlist names to avoid overwriting or database confusion
            if (ui.musicHandler.getCreatedPlaylists().contains(cleanName)) {
                JOptionPane.showMessageDialog(null, "A playlist with that name already exists!", "Duplicate Name", JOptionPane.WARNING_MESSAGE);
                return; // Abort the creation process
            }
            
            // 4. Add the verified name to the backend model and instantly save to the text file
            ui.musicHandler.getCreatedPlaylists().add(cleanName);
            ui.musicHandler.savePlaylists();
            
            // 5. Tell the UI to generate a new physical hitbox for the newly created playlist
            ui.refreshPlaylistButtons();
            
            // 6. Request a screen update so the new playlist appears in the list
            panel.repaint();
        }
    }
}