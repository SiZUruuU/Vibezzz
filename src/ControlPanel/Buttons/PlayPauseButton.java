package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import ControlPanel.Song;
import Main.Panel;
import Main.UI;
import java.util.ArrayList;

public class PlayPauseButton extends ButtonManager {

    public PlayPauseButton(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui); // Bounds are 0 for now, UI.java will set them
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        ArrayList<Song> playlist = ui.musicHandler.getPlaylist();
        
        if (playlist.isEmpty()) {
            ui.musicHandler.loadDynamicPlaylist();
            if (!playlist.isEmpty()) {
                ui.currentSongIndex = 0;
                ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
            }
        } else {
            if (ui.audioEngine.isPlaying()) {
                ui.audioEngine.pauseTrack();
            } else {
                ui.audioEngine.resumeTrack();
                if (!ui.audioEngine.isPlaying()) { // fallback if track wasn't loaded
                    ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
                }
            }
        }
        panel.repaint();
    }
}