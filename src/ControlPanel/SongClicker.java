package ControlPanel;

import Main.Panel;
import Main.UI;

public class SongClicker extends ButtonManager {
    private Song song;

    public SongClicker(Panel panel, UI ui, Song song) {
        super(0, 0, 0, 0, panel, ui);
        this.song = song;
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        // Since UI has 'public AudioEngine audioEngine', we reach it through 'ui'
        ui.audioEngine.playTrack(song.getAudioPath());
    }
}