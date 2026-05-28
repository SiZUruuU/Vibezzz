package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class PlayPauseButton extends ButtonManager {

    public PlayPauseButton(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui); // Bounds are 0 for now, UI.java will set them
    }

    // Inside PlayPauseButton.java execute() method...
    @Override
    public void execute(int mouseX, int mouseY) {
        if (ui.musicHandler.getPlaylist().isEmpty()) return; // Do nothing if empty
        
        if (ui.audioEngine.isPlaying()) {
            ui.audioEngine.pauseTrack();
        } else {
            ui.audioEngine.resumeTrack();
            if (!ui.audioEngine.isPlaying()) {
                ui.audioEngine.playTrack(ui.musicHandler.getPlaylist().get(ui.currentSongIndex).getAudioPath());
            }
        }
        panel.repaint();
    }
}