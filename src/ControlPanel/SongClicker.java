package ControlPanel;

import Main.Panel;
import Main.UI;

public class SongClicker extends ButtonManager {
    private Song song;

    public SongClicker(Panel panel, UI ui, Song song) {
        super(0, 0, 0, 0, panel, ui);
        this.song = song;
    }

    public Song getSong() { return song; } 

    @Override
    public void execute(int mouseX, int mouseY) {
        // Sets queue strictly to THIS playlist
        ui.musicHandler.setActiveList(ui.musicHandler.getPlaylistSongs().get(ui.selectedPlaylistName));
        ui.currentSongIndex = ui.musicHandler.getActiveList().indexOf(song);
        ui.audioEngine.playTrack(song.getAudioPath());
        panel.repaint();
    }
}