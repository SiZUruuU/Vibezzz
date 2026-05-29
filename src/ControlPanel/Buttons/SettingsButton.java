package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

public class SettingsButton extends ButtonManager {
    
    UI ui;
    Panel panel;

    public SettingsButton(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
        this.ui = ui;
        this.panel = panel;

    }

    @Override
    public void execute(int mouseX, int mouseY) {

        ui.settingsPressed = !ui.settingsPressed;
        panel.repaint();
        
    }
    
}
