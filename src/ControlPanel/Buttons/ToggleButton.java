package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles the toggle states for playback modifiers like Repeat and Shuffle.
 * By passing a specific "mode" string during construction, this single class 
 * can act as multiple different buttons, keeping the codebase streamlined.
 */
public class ToggleButton extends ButtonManager {

    private String mode; // Identifies the specific function of this button instance

    /**
     * Constructs the ToggleButton.
     * @param panel The main application panel used to refresh the screen.
     * @param ui    The main UI state manager.
     * @param mode  A string identifier dictating the button's behavior (e.g., "repeat" or "shuffle").
     */
    public ToggleButton(Panel panel, UI ui, String mode) {
        // Bounds are initialized to 0; PlayerView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
        this.mode = mode;
    }

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Branch logic based on the button's assigned mode
        if (mode.equals("repeat")) {
            
            // 2a. Toggle the global repeat state (flips between true and false)
            ui.isRepeat = !ui.isRepeat;
            
        } else if (mode.equals("shuffle")) {
            
            // 2b. Toggle the global shuffle state (flips between true and false)
            ui.isShuffle = !ui.isShuffle;
            
        }
        
        // 3. Request a screen update so the button can visually update to its "active" accent color
        panel.repaint();
    }
}