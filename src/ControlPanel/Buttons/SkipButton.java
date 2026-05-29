package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import ControlPanel.Song;
import Main.Panel;
import Main.UI;
import java.util.ArrayList;


public class SkipButton extends ButtonManager {
    
    private boolean isForward; 


    public SkipButton(Panel panel, UI ui, boolean isForward) {
        super(0, 0, 0, 0, panel, ui);
        this.isForward = isForward;
    }


    @Override
    public void execute(int mouseX, int mouseY) {
        
        ArrayList<Song> playlist = ui.musicHandler.getActiveList();
        
        if (playlist.isEmpty()) return;

        if (isForward) {
            
            if (ui.isRepeat) {
            } else if (ui.isShuffle) {
                if (playlist.size() > 1) {
                    int newIndex;
                    do { 
                        newIndex = (int)(Math.random() * playlist.size());
                    } while (newIndex == ui.currentSongIndex);
                    ui.currentSongIndex = newIndex;
                }
            } else {

                ui.currentSongIndex = (ui.currentSongIndex + 1) % playlist.size();
            }
            
        } else {
            
            if (ui.isRepeat) {
            } else {
                
                ui.currentSongIndex = (ui.currentSongIndex - 1 + playlist.size()) % playlist.size();
            }
            
        }

        ui.audioEngine.playTrack(playlist.get(ui.currentSongIndex).getAudioPath());
        
        panel.repaint();
    }
}