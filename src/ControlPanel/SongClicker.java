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
        ui.activePlayingList = ui.playlistSongs.get(ui.selectedPlaylistName);
        ui.currentSongIndex = ui.activePlayingList.indexOf(song);
        ui.audioEngine.playTrack(song.getAudioPath());
        panel.repaint();
    }
}