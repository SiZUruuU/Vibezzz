package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class LibraryListClicker extends ButtonManager {

    public LibraryListClicker(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (ui.musicHandler.getPlaylist().isEmpty()) return;

        // Always index into the FILTERED list so clicks match what the user sees
        java.util.ArrayList<ControlPanel.Song> filtered = ui.musicHandler.searchSongs(ui.searchText);
        if (filtered.isEmpty()) return;

        int rowHeight = 30; // Matches the spacing in LibraryView
        int relativeY = mouseY - this.y;
        int clickedIndex = relativeY / rowHeight;

        if (clickedIndex >= 0 && clickedIndex < filtered.size()) {
            ControlPanel.Song clickedSong = filtered.get(clickedIndex);
            // Map back to the real index in the full playlist so skip/repeat still work
            int realIndex = ui.musicHandler.getPlaylist().indexOf(clickedSong);
            if (realIndex >= 0) {
                ui.currentSongIndex = realIndex;
                ui.audioEngine.playTrack(clickedSong.getAudioPath());
                panel.repaint();
            }
        }
    }
}
