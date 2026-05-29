package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class BackButton extends ButtonManager {
    public BackButton(Panel panel, UI ui) { super(0,0,0,0, panel, ui); }
    
    @Override
    public void execute(int mouseX, int mouseY) {
        if (ui.insidePlaylistView) {
            ui.insidePlaylistView = false;
            ui.selectedPlaylistName = "";
            ui.isAddingToPlaylist = false; // Reset the UI mode just in case
            ui.refreshPlaylistButtons();
            panel.repaint();
        }
    }
}