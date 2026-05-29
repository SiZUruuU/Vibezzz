package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class PlaylistClicker extends ButtonManager {
    private String playlistName;

    public PlaylistClicker(Panel panel, UI ui, String playlistName) {
        super(0, 0, 0, 0, panel, ui);
        this.playlistName = playlistName;
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (!ui.insidePlaylistView) {
            ui.selectedPlaylistName = this.playlistName; // Sets the name when entering
            ui.insidePlaylistView = true;
        } else {
            ui.insidePlaylistView = false;
            ui.selectedPlaylistName = "";
        }
        panel.repaint();
    }
}