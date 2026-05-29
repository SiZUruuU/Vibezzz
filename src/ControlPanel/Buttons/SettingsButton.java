package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles the interaction for the settings gear icon.
 * When clicked, this button toggles the visibility of the settings/volume overlay 
 * (handled by VolumeView) on and off.
 */
public class SettingsButton extends ButtonManager {

    /**
     * Constructs the SettingsButton.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     */
    public SettingsButton(Panel panel, UI ui) {
        // Bounds are initialized to 0; LibraryView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
    }

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Toggle the boolean state that controls the settings overlay visibility
        ui.settingsPressed = !ui.settingsPressed;
        
        // 2. Request a screen update to either render or hide the VolumeView popup
        panel.repaint();
    }
}