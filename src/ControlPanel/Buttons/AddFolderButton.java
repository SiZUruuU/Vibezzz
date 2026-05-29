package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;


public class AddFolderButton extends ButtonManager {


    public AddFolderButton(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        
        ui.musicHandler.loadDynamicPlaylist();
        
        if (!ui.musicHandler.getPlaylist().isEmpty()) {
            ui.currentSongIndex = 0; 
            ui.audioEngine.playTrack(ui.musicHandler.getPlaylist().get(0).getAudioPath());
        }
        
        panel.repaint();
    }
}