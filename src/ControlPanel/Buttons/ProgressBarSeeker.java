package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class ProgressBarSeeker extends ButtonManager {

    public ProgressBarSeeker(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (!ui.musicHandler.getPlaylist().isEmpty() && ui.audioEngine.isPlaying()) {
            // Because we passed mouseX, the button handles its own complex math!
            double percentage = (double) (mouseX - this.x) / this.width;
            ui.audioEngine.seek(percentage);
        }
    }
}