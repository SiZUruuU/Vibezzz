package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;


public class ProgressBarSeeker extends ButtonManager {


    public ProgressBarSeeker(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
    }

    public void updateVisual(int mouseX) {
        if (this.width == 0) return;
        
        double percentage = (double) (mouseX - this.x) / this.width;
        
        ui.dragProgress = Math.max(0.0, Math.min(1.0, percentage));
        
        ui.panel.repaint();
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        
        if (!ui.musicHandler.getPlaylist().isEmpty() && ui.audioEngine.isPlaying()) {
            
            double percentage = (double) (mouseX - this.x) / this.width;
            
            ui.audioEngine.seek(Math.max(0.0, Math.min(1.0, percentage)));
        }
    }
}