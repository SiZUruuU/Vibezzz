package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class ToggleButton extends ButtonManager {

    private String mode;

    public ToggleButton(Panel panel, UI ui, String mode) {
        super(0, 0, 0, 0, panel, ui);
        this.mode = mode;
    }

    @Override
    public void execute(int mouseX, int mouseY) {
        if (mode.equals("repeat")) {
            ui.isRepeat = !ui.isRepeat;
        } else if (mode.equals("shuffle")) {
            ui.isShuffle = !ui.isShuffle;
        }
        panel.repaint();
    }
}