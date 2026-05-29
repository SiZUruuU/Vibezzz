package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class PlayPauseButton extends ButtonManager {
    public PlayPauseButton(Panel panel, UI ui) { super(0, 0, 0, 0, panel, ui); }

    @Override
    public void execute(int mouseX, int mouseY) {
        java.util.ArrayList<ControlPanel.Song> playlist = ui.musicHandler.getActiveList();
        if (playlist.isEmpty()) return; 
        
        if (ui.audioEngine.isPlaying()) {
            ui.audioEngine.pauseTrack();
        } else {
            ui.audioEngine.resumeTrack();
            if (!ui.audioEngine.isPlaying()) {
                ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
            }
        }
        panel.repaint();
    }
}