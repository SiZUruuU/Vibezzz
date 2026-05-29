package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles the action of adding a new music folder to the global library.
 * When clicked, this button prompts the user to select a directory, parses the 
 * audio files via the MusicHandler, and automatically starts playing the first track.
 */
public class AddFolderButton extends ButtonManager {

    /**
     * Constructs the AddFolderButton.
     * * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public AddFolderButton(Panel panel, UI ui) {
        // Bounds are initialized to 0; LibraryView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
    }

    /**
     * Executes the button's core logic.
     * * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Prompt the user with a file chooser and load the songs into the backend model
        ui.musicHandler.loadDynamicPlaylist();
        
        // 2. If the loaded folder actually contains valid audio files, begin playback
        if (!ui.musicHandler.getPlaylist().isEmpty()) {
            ui.currentSongIndex = 0; // Reset the active queue index to the top of the list
            ui.audioEngine.playTrack(ui.musicHandler.getPlaylist().get(0).getAudioPath());
        }
        
        // 3. Request a screen update to render the newly populated library list
        panel.repaint();
    }
}