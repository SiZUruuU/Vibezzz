package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles mouse interactions for the main Library song list.
 * Rather than creating hundreds of individual hitboxes for every song, this class 
 * uses a single large hitbox and spatial math to calculate exactly which song was clicked. 
 * It also intelligently routes the click based on the current UI state (Playback vs. Add to Playlist).
 */
public class LibraryListClicker extends ButtonManager {

    /**
     * Constructs the LibraryListClicker.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public LibraryListClicker(Panel panel, UI ui) { 
        // Bounds are initialized to 0; LibraryView dynamically sets them over the scrollable list.
        super(0, 0, 0, 0, panel, ui); 
    }

    /**
     * Executes the click logic based on mouse coordinates.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Guard clause: Do nothing if the global library is completely empty
        if (ui.musicHandler.getPlaylist().isEmpty()) return;
        
        // 2. Fetch the filtered list (Ensures clicks remain accurate even if the user is searching)
        java.util.ArrayList<ControlPanel.Song> filtered = ui.musicHandler.searchSongs(ui.searchText);
        if (filtered.isEmpty()) return; // Guard clause: Do nothing if the search has no results

        // 3. Spatial Math: Calculate which row the user clicked
        int rowHeight = 30; 
        int relativeY = mouseY - this.y; // Calculate click position relative to the top of the hitbox
        int clickedIndex = relativeY / rowHeight; // Divide by row height to get the exact array index

        // 4. Ensure the calculated index actually exists within our current list bounds
        if (clickedIndex >= 0 && clickedIndex < filtered.size()) {
            ControlPanel.Song clickedSong = filtered.get(clickedIndex);

            // --- BRANCH A: PLAYLIST INTERCEPTION MODE ---
            // If the user clicked "+ Add Song" in a playlist, grab the song and route it there!
            if (ui.isAddingToPlaylist && ui.insidePlaylistView) {
                
                // Initialize the playlist array if it happens to be empty
                ui.musicHandler.getPlaylistSongs().putIfAbsent(ui.selectedPlaylistName, new java.util.ArrayList<>());
                java.util.ArrayList<ControlPanel.Song> currentList = ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName);
                
                // Prevent duplicate entries in the same playlist
                if (!currentList.contains(clickedSong)) {
                    currentList.add(clickedSong);
                    ui.musicHandler.savePlaylists(); // Instantly write the new data to the hard drive
                    ui.refreshPlaylistButtons();     // Tell the UI to generate a hitbox for the new song
                }
                
                // Turn off the selection mode and repaint so the UI snaps back to normal
                ui.isAddingToPlaylist = false; 
                panel.repaint();
                
                return; // CRITICAL: Stop execution here so the audio engine doesn't play the song
            }

            // --- BRANCH B: NORMAL PLAYBACK MODE ---
            // If we are not adding to a playlist, update the active queue and play the music!
            
            // Tell the backend that the Global Library is now the active playing queue
            ui.musicHandler.setActiveList(ui.musicHandler.getPlaylist());
            
            // Sync the current song index with the backend's active list
            ui.currentSongIndex = ui.musicHandler.getActiveList().indexOf(clickedSong);
            
            // Command the audio engine to begin playback
            ui.audioEngine.playTrack(clickedSong.getAudioPath());
            
            // Request a screen update to render the new "Now Playing" highlights and metadata
            panel.repaint();
        }
    }
}