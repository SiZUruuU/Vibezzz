package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class VolumeSlider extends ButtonManager {

    public VolumeSlider(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui); 
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        // Immediate update on click
        updateVolume(mouseX);
    }

    public void updateVolume(int mouseX) {
        if (this.width == 0) return;

        // Math: Click position relative to slider start divided by track width
        float percentage = (float) (mouseX - this.x) / this.width;
        
        // Clamp value between 0.0 (mute) and 1.0 (max)
        percentage = Math.max(0.0f, Math.min(1.0f, percentage));
        
        ui.audioEngine.setVolume(percentage);
        panel.repaint(); // Inform panel to redraw the track fill bar
    }
}