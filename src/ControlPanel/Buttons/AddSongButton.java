package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class AddSongButton extends ButtonManager {
    public AddSongButton(Panel panel, UI ui) { super(0, 0, 0, 0, panel, ui); }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (!ui.insidePlaylistView) return;
        ui.isAddingToPlaylist = !ui.isAddingToPlaylist; // Toggles the "Select from Library" mode
        panel.repaint();
    }
}