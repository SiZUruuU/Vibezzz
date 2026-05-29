package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class LibraryListClicker extends ButtonManager {
    public LibraryListClicker(Panel panel, UI ui) { super(0, 0, 0, 0, panel, ui); }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (ui.musicHandler.getPlaylist().isEmpty()) return;
        java.util.ArrayList<ControlPanel.Song> filtered = ui.musicHandler.searchSongs(ui.searchText);
        if (filtered.isEmpty()) return;

        int rowHeight = 30; 
        int relativeY = mouseY - this.y;
        int clickedIndex = relativeY / rowHeight;

        if (clickedIndex >= 0 && clickedIndex < filtered.size()) {
            ControlPanel.Song clickedSong = filtered.get(clickedIndex);

            if (ui.isAddingToPlaylist && ui.insidePlaylistView) {
                ui.playlistSongs.putIfAbsent(ui.selectedPlaylistName, new java.util.ArrayList<>());
                java.util.ArrayList<ControlPanel.Song> currentList = ui.playlistSongs.get(ui.selectedPlaylistName);
                if (!currentList.contains(clickedSong)) {
                    currentList.add(clickedSong);
                    ui.savePlaylists(); // SAVE HOOK
                    ui.refreshPlaylistButtons(); 
                }
                ui.isAddingToPlaylist = false; 
                panel.repaint();
                return; 
            }

            // Sets queue to GLOBAL library
            ui.activePlayingList = ui.musicHandler.getPlaylist();
            ui.currentSongIndex = ui.activePlayingList.indexOf(clickedSong);
            ui.audioEngine.playTrack(clickedSong.getAudioPath());
            panel.repaint();
        }
    }
}