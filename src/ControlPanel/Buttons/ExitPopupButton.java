package ControlPanel.Buttons;

import ControlPanel.ButtonManager;
import Main.Panel;
import Main.UI;

/**
 * Handles the user interactions for the exit confirmation popup.
 * This single class elegantly acts as both the "Yes" (confirm exit) and 
 * "No" (cancel exit) button by passing a boolean flag during construction.
 */
public class ExitPopupButton extends ButtonManager {

    private boolean isConfirm; // true = Yes (Terminate App), false = No (Dismiss Popup)

    /**
     * Constructs the ExitPopupButton.
     * @param panel     The main application panel used to refresh the screen.
     * @param ui        The main UI state manager.
     * @param isConfirm Determines the button's behavior: true to exit the app, false to cancel.
     */
    public ExitPopupButton(Panel panel, UI ui, boolean isConfirm) {
        // Bounds are initialized to 0; PopupView dynamically sets them during the draw phase.
        super(0, 0, 0, 0, panel, ui);
        this.isConfirm = isConfirm;
    }

    /**
     * Executes the button's core logic.
     * @param mouseX The X coordinate of the user's mouse click.
     * @param mouseY The Y coordinate of the user's mouse click.
     */
    @Override
    public void execute(int mouseX, int mouseY) {
        
        // 1. Branch logic based on the button's assigned identity
        if (isConfirm) {
            // 2a. If this is the "Yes" button, instantly terminate the Java Virtual Machine
            System.exit(0);
        } else {
            // 2b. If this is the "No" button, tell the UI to toggle the exit state off
            // The exitInquiry() method automatically repaints the panel to hide the popup
            ui.exitInquiry();
        }
    }
}