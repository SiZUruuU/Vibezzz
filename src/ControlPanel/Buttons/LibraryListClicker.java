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

        int rowHeight = 30; // Matches the spacing in LibraryView
        int relativeY = mouseY - this.y;
        int clickedIndex = relativeY / rowHeight;

        if (clickedIndex >= 0 && clickedIndex < ui.musicHandler.getPlaylist().size()) {
            ui.currentSongIndex = clickedIndex;
            ui.audioEngine.playTrack(ui.musicHandler.getPlaylist().get(clickedIndex).getAudioPath());
            panel.repaint();
        }
    }
}