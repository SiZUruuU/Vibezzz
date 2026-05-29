package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;


public class SettingsButton extends ButtonManager {

    public SettingsButton(Panel panel, UI ui) {
        super(0, 0, 0, 0, panel, ui);
    }

 
    @Override
    public void execute(int mouseX, int mouseY) {
        
        ui.settingsPressed = !ui.settingsPressed;
        
        panel.repaint();
    }
}